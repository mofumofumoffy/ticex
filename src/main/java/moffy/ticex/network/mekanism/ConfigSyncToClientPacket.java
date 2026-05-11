package moffy.ticex.network.mekanism;

import mekanism.api.MekanismAPI;
import mekanism.api.gear.ModuleData;
import mekanism.api.gear.config.*;
import mekanism.api.math.MathUtils;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.content.gear.ModuleConfigItem;
import mekanism.common.content.gear.ModuleHelper;
import mekanism.common.network.to_server.PacketUpdateModuleSettings;
import mekanism.common.registries.MekanismSounds;
import moffy.ticex.network.TicEXPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ConfigSyncToClientPacket extends TicEXPacket.ClientBoundPacket {

    private final ModuleData<?> moduleType;
    private final EquipmentSlot slot;
    private final int configIndex;
    private final ModuleDataType dataType;
    private final Object value;

    public static ConfigSyncToClientPacket create(
        ModuleData<?> moduleType,
        EquipmentSlot slot,
        int configIndex,
        ModuleConfigData<?> configData,
        Object newValue
    ) {
        if (configData instanceof ModuleEnumData<?>) {
            return new ConfigSyncToClientPacket(moduleType, slot, configIndex, ModuleDataType.ENUM, newValue);
        }
        for (ModuleDataType type : ModuleDataType.VALUES) {
            if (type.typeMatches(configData)) {
                return new ConfigSyncToClientPacket(moduleType, slot, configIndex, type, newValue);
            }
        }
        throw new IllegalArgumentException("Unknown config data type.");
    }

    protected ConfigSyncToClientPacket(
        ModuleData<?> moduleType,
        EquipmentSlot slot,
        int configIndex,
        ModuleDataType dataType,
        Object value
    ) {
        this.moduleType = moduleType;
        this.slot = slot;
        this.configIndex = configIndex;
        this.dataType = dataType;
        this.value = value;
    }

    public ConfigSyncToClientPacket(FriendlyByteBuf buf) {
        this.slot = buf.readEnum(EquipmentSlot.class);
        this.moduleType = buf.readRegistryIdSafe(ModuleData.class);
        this.configIndex = buf.readInt();
        this.dataType = buf.readEnum(ModuleDataType.class);
        this.value =
                switch (dataType) {
                    case BOOLEAN -> buf.readBoolean();
                    case COLOR -> buf.readInt();
                    case INTEGER, ENUM -> buf.readVarInt();
                };
    }

    public EquipmentSlot getSlot() {
        return slot;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(slot);
        buf.writeRegistryId(MekanismAPI.moduleRegistry(), moduleType);
        buf.writeInt(configIndex);
        buf.writeEnum(dataType);
        switch (dataType) {
            case BOOLEAN -> buf.writeBoolean((boolean) value);
            case COLOR -> buf.writeInt((int) value);
            case INTEGER, ENUM -> buf.writeVarInt((int) value);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            ItemStack stack = player.getItemBySlot(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof IModuleContainerItem) {
                mekanism.common.content.gear.Module<?> module = ModuleHelper.get().load(stack, moduleType);
                if (module != null) {
                    List<ModuleConfigItem<?>> configItems = module.getConfigItems();
                    if (configIndex < configItems.size()) {
                        setValue(configItems.get(configIndex), dataType, value);
                        SoundHandler.playSound(MekanismSounds.HYDRAULIC);
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }

    @SuppressWarnings("unchecked")
    private static <T> void setValue(ModuleConfigItem<T> moduleConfigItem, ModuleDataType dataType, Object value) {
        ModuleConfigData<T> configData = moduleConfigItem.getData();
        if (configData instanceof ModuleEnumData && dataType == ModuleDataType.ENUM) {
            moduleConfigItem.set((T) MathUtils.getByIndexMod(((ModuleEnumData<?>) configData).getEnums(), (int) value));
        } else if (dataType.typeMatches(configData)) {
            moduleConfigItem.set((T) value);
        }
    }

    public enum ModuleDataType {
        BOOLEAN(data -> data instanceof ModuleBooleanData),
        COLOR(data -> data instanceof ModuleColorData),
        INTEGER(data -> data instanceof ModuleIntegerData),
        ENUM(data -> data instanceof ModuleEnumData);

        private static final ModuleDataType[] VALUES = values();

        private final Predicate<ModuleConfigData<?>> configDataPredicate;

        ModuleDataType(Predicate<ModuleConfigData<?>> configDataPredicate) {
            this.configDataPredicate = configDataPredicate;
        }

        public boolean typeMatches(ModuleConfigData<?> data) {
            return configDataPredicate.test(data);
        }
    }
}
