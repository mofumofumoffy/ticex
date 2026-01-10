package moffy.ticex.lib.modules.mekanism.interfaces;

import mekanism.api.math.FloatingLong;
import mekanism.api.radial.RadialData;
import mekanism.api.radial.mode.IRadialMode;
import mekanism.api.radial.mode.NestedRadialMode;
import mekanism.common.capabilities.ItemCapabilityWrapper;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.energy.item.RateLimitEnergyHandler;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.content.gear.Module;
import mekanism.common.lib.radial.IGenericRadialModeItem;
import mekanism.common.lib.radial.data.NestingRadialData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public interface IMekaGear extends IModuleContainerItem, IGenericRadialModeItem {
    default boolean areCapabilityConfigsLoaded() {
        return MekanismConfig.gear.isLoaded();
    }

    default void gatherCapabilities(ItemStack stack, List<ItemCapabilityWrapper.ItemCapability> capabilities) {
        capabilities.add(RateLimitEnergyHandler.create(() -> getChargeRate(stack), () -> getMaxEnergy(stack), BasicEnergyContainer.manualOnly,
                BasicEnergyContainer.alwaysTrue));
    }

    @Override
    default @Nullable RadialData<?> getRadialData(ItemStack stack) {
        List<NestedRadialMode> nestedModes = new ArrayList<>();
        Consumer<NestedRadialMode> adder = nestedModes::add;
        for (Module<?> module : getModules(stack)) {
            if (module.handlesRadialModeChange()) {
                module.addRadialModes(stack, adder);
            }
        }
        if (nestedModes.isEmpty()) {
            return null;
        } else if (nestedModes.size() == 1) {
            return nestedModes.get(0).nestedData();
        }
        return new NestingRadialData(getRadialId(), nestedModes);
    }

    @Override
    default <M extends IRadialMode> @Nullable M getMode(ItemStack stack, RadialData<M> radialData) {
        for (Module<?> module : getModules(stack)) {
            if (module.handlesRadialModeChange()) {
                M mode = module.getMode(stack, radialData);
                if (mode != null) {
                    return mode;
                }
            }
        }
        return null;
    }

    @Override
    default <M extends IRadialMode> void setMode(ItemStack stack, Player player, RadialData<M> radialData, M mode) {
        for (Module<?> module : getModules(stack)) {
            if (module.handlesRadialModeChange() && module.setMode(player, stack, radialData, mode)) {
                return;
            }
        }
    }

    @Override
    default void changeMode(@NotNull Player player, @NotNull ItemStack stack, int shift, DisplayChange displayChange) {
        for (Module<?> module : getModules(stack)) {
            if (module.handlesModeChange()) {
                module.changeMode(player, stack, shift, displayChange);
                return;
            }
        }
    }

    ResourceLocation getRadialId();
    FloatingLong getChargeRate(ItemStack stack);
    FloatingLong getMaxEnergy(ItemStack stack);
}
