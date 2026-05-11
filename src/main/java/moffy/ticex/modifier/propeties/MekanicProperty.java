package moffy.ticex.modifier.propeties;

import dan200.computercraft.api.lua.ILuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.api.gear.IModule;
import mekanism.api.gear.IModuleHelper;
import mekanism.api.gear.ModuleData;
import mekanism.api.gear.config.ModuleBooleanData;
import mekanism.api.gear.config.ModuleColorData;
import mekanism.api.gear.config.ModuleConfigData;
import mekanism.api.gear.config.ModuleEnumData;
import mekanism.client.sound.SoundHandler;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.content.gear.ModuleConfigItem;
import mekanism.common.registries.MekanismSounds;
import moffy.ticex.TicEX;
import moffy.ticex.lib.modules.mekanism.MekaGearCapability;
import moffy.ticex.lib.modules.mekanism.interfaces.IMekaGear;
import moffy.ticex.network.mekanism.ConfigSyncToClientPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

public class MekanicProperty {

    public static BiFunction<Player, ItemStack, Map<String, Object>> getProperties() {
        return (user, stack) -> {
            Map<String, Object> result = new HashMap<>();

            result.put("getMekanismEnergy", getMekanismEnergy(user, stack));
            result.put("getModuleProps", getModuleData(user, stack));
            result.put("setConfigValue", setModuleData(user, stack));
            result.put("getModuleData", getModuleData(user, stack));
            result.put("setModuleData", setModuleData(user, stack));

            return result;
        };
    }

    public static ILuaFunction getMekanismEnergy(Player user, ItemStack stack){
        return args -> {
            var strictEnergyHandlerLazyOptional = stack.getCapability(Capabilities.STRICT_ENERGY);
            if(strictEnergyHandlerLazyOptional.isPresent()){
                IStrictEnergyHandler strictEnergyHandler = strictEnergyHandlerLazyOptional.orElseThrow(IllegalStateException::new);
                return MethodResult.of(strictEnergyHandler.getEnergy(0), strictEnergyHandler.getMaxEnergy(0));
            }
            return MethodResult.of();
        };
    }

    @SuppressWarnings("unchecked")
    public static ILuaFunction getModuleData(Player user, ItemStack stack) {
        return args -> {
            Map<String, Object> result = new HashMap<>();
            for (IModule<?> moduleInterface : IModuleHelper.INSTANCE.loadAll(stack)) {
                if (moduleInterface instanceof mekanism.common.content.gear.Module module) {
                    Map<String, Object> dataMap = new HashMap<>();
                    List<ModuleConfigItem<?>> configItems = module.getConfigItems();
                    configItems
                        .stream()
                        .forEach(configItem -> {
                            dataMap.put(configItem.getName(), configItem.get());
                        });
                    result.put(module.getData().getName(), new HashMap<>(dataMap));
                }
            }
            return MethodResult.of(result);
        };
    }

    @SuppressWarnings("unchecked")
    public static ILuaFunction setModuleData(Player user, ItemStack stack) {
        return args -> {
            String moduleName = args.getString(0);
            String configName = args.getString(1);
            Object newValue = args.get(2);

            boolean succeed = false;
            for (IModule<?> moduleInterface : IModuleHelper.INSTANCE.loadAll(stack)) {
                if (
                    moduleInterface instanceof mekanism.common.content.gear.Module module &&
                    module.getData().getName().equals(moduleName)
                ) {
                    List<ModuleConfigItem<?>> configItems = module.getConfigItems();
                    for (int i = 0; i < configItems.size(); i++) {
                        ModuleConfigItem<?> configItem = configItems.get(i);
                        if (configItem.getName().equals(configName)) {
                            if (
                                newValue instanceof Boolean booleanValue &&
                                configItem.getData() instanceof ModuleBooleanData booleanData
                            ) {
                                booleanData.set(booleanValue);
                                sendUpdatePacket(user, stack, module.getData(), i, booleanData, booleanValue);
                                succeed = true;
                                break;
                            } else if (
                                newValue instanceof Integer colorValue &&
                                configItem.getData() instanceof ModuleColorData colorData
                            ) {
                                colorData.set(colorValue);
                                sendUpdatePacket(user, stack, module.getData(), i, colorData, colorValue);
                                succeed = true;
                                break;
                            } else if (
                                newValue instanceof Enum enumValue &&
                                configItem.getData() instanceof ModuleEnumData enumData
                            ) {
                                if (enumValue.ordinal() < enumData.getEnums().size()) {
                                    enumData.set(enumValue);
                                    sendUpdatePacket(user, stack, module.getData(), i, enumData, enumValue);
                                    succeed = true;
                                    break;
                                }
                            }
                        }
                    }
                    break;
                }
            }
            return MethodResult.of(succeed);
        };
    }

    public static void sendUpdatePacket(
        Player player,
        ItemStack stack,
        ModuleData<?> moduleData,
        int configDataIndex,
        ModuleConfigData<?> configData,
        Object newValue
    ) {
        if (player instanceof ServerPlayer serverPlayer) {
            TicEX.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> serverPlayer),
                ConfigSyncToClientPacket.create(
                    moduleData,
                    Player.getEquipmentSlotForItem(stack),
                    configDataIndex,
                    configData,
                    newValue
                )
            );
        }
        SoundHandler.playSound(MekanismSounds.HYDRAULIC);
    }
}
