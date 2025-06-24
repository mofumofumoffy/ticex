package moffy.ticex.network.mekanism;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import mekanism.api.MekanismAPI;
import mekanism.api.gear.ModuleData;
import mekanism.api.gear.config.ModuleBooleanData;
import mekanism.api.gear.config.ModuleColorData;
import mekanism.api.gear.config.ModuleConfigData;
import mekanism.api.gear.config.ModuleEnumData;
import mekanism.api.gear.config.ModuleIntegerData;
import mekanism.api.math.MathUtils;
import mekanism.common.Mekanism;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.content.gear.ModuleConfigItem;
import mekanism.common.content.gear.ModuleHelper;
import mekanism.common.network.to_server.PacketUpdateModuleSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class ConfigSyncToClientPacket {

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

    public int getConfigIndex() {
        return configIndex;
    }

    public ModuleDataType getDataType() {
        return dataType;
    }

    public ModuleData<?> getModuleType() {
        return moduleType;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }

    public Object getValue() {
        return value;
    }

    public static ConfigSyncToClientPacket decode(FriendlyByteBuf buf) {
        EquipmentSlot slot = buf.readEnum(EquipmentSlot.class);
        ModuleData<?> moduleData = buf.readRegistryIdSafe(ModuleData.class);
        int configDataIndex = buf.readInt();
        ModuleDataType dataType = buf.readEnum(ModuleDataType.class);
        Object data =
            switch (dataType) {
                case BOOLEAN -> buf.readBoolean();
                case COLOR -> buf.readInt();
                case INTEGER, ENUM -> buf.readVarInt();
            };
        return new ConfigSyncToClientPacket(moduleData, slot, configDataIndex, dataType, data);
    }

    public static void encode(ConfigSyncToClientPacket packet, FriendlyByteBuf buf) {
        buf.writeEnum(packet.getSlot());
        buf.writeRegistryId(MekanismAPI.moduleRegistry(), packet.getModuleType());
        buf.writeInt(packet.getConfigIndex());
        buf.writeEnum(packet.getDataType());
        switch (packet.getDataType()) {
            case BOOLEAN -> buf.writeBoolean((boolean) packet.getValue());
            case COLOR -> buf.writeInt((int) packet.getValue());
            case INTEGER, ENUM -> buf.writeVarInt((int) packet.getValue());
        }
    }

    public static void handle(ConfigSyncToClientPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            ItemStack stack = player.getItemBySlot(packet.getSlot());
            if (!stack.isEmpty() && stack.getItem() instanceof IModuleContainerItem) {
                mekanism.common.content.gear.Module<?> module = ModuleHelper.get().load(stack, packet.getModuleType());
                if (module != null) {
                    List<ModuleConfigItem<?>> configItems = module.getConfigItems();
                    if (packet.getConfigIndex() < configItems.size()) {
                        ModuleConfigItem<?> configItem = configItems.get(packet.getConfigIndex());
                        setValue(configItem, packet.getDataType(), packet.getValue());
                        if (stack.getItem() instanceof ArmorItem) {
                            Mekanism.packetHandler()
                                .sendToServer(
                                    PacketUpdateModuleSettings.create(
                                        36 + (3 - packet.getSlot().getIndex()),
                                        module.getData(),
                                        packet.getConfigIndex(),
                                        configItem.getData()
                                    )
                                );
                        } else {
                            Optional<ItemStack> optionalStack = player
                                .getInventory()
                                .items.stream()
                                .filter(item -> ItemStack.isSameItem(stack, item))
                                .findFirst();
                            if (optionalStack.isPresent()) {
                                Mekanism.packetHandler()
                                    .sendToServer(
                                        PacketUpdateModuleSettings.create(
                                            player.getInventory().findSlotMatchingItem(optionalStack.get()),
                                            module.getData(),
                                            packet.getConfigIndex(),
                                            configItem.getData()
                                        )
                                    );
                            }
                        }
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

    private static enum ModuleDataType {
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
