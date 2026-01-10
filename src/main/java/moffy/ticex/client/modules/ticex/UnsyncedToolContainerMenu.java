package moffy.ticex.client.modules.ticex;

import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.inventory.EmptyItemHandler;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.display.ToolNameHook;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.menu.ToolContainerMenu;

public class UnsyncedToolContainerMenu extends ToolContainerMenu {
    public UnsyncedToolContainerMenu(int id, Inventory playerInventory, ItemStack stack, IItemHandler handler, int slotIndex) {
        super(TicEXRegistry.UNSYNCED_TOOL_CONTAINER.get(), id, playerInventory, stack, handler, slotIndex);
    }

    public static ToolContainerMenu forClient(int id, Inventory inventory, FriendlyByteBuf buffer) {
        int slotIndex = buffer.readVarInt();

        ItemStack itemStack = inventory.getItem(slotIndex);
        inventory.setItem(slotIndex, itemStack);

        IItemHandler handler = itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).filter((cap) -> cap instanceof IItemHandlerModifiable).orElse(EmptyItemHandler.INSTANCE);
        return new UnsyncedToolContainerMenu(id, inventory, itemStack, handler, slotIndex);
    }

    public static InteractionResult tryOpenContainer(ItemStack stack, IToolStackView tool, Player player, EquipmentSlot slotType) {
        return tryOpenContainer(stack, tool, tool.getDefinition(), player, slotType);
    }

    public static InteractionResult tryOpenContainer(ItemStack stack, @Nullable IToolStackView tool, ToolDefinition definition, Player player, EquipmentSlot slotType) {
        int slotIndex;
        switch (slotType) {
            case MAINHAND -> slotIndex = player.getInventory().selected;
            case OFFHAND -> slotIndex = 40;
            default -> slotIndex = 36 + slotType.getIndex();
        }

        return tryOpenContainer(stack, tool, definition, player, slotIndex);
    }

    public static InteractionResult tryOpenContainer(ItemStack stack, @Nullable IToolStackView tool, ToolDefinition definition, Player player, int slotIndex) {
        IItemHandler handler = stack.getCapability(ForgeCapabilities.ITEM_HANDLER).filter((cap) -> cap instanceof IItemHandlerModifiable).orElse(EmptyItemHandler.INSTANCE);
        if (handler.getSlots() <= 0 && !ModifierUtil.checkVolatileFlag(stack, ToolInventoryCapability.CRAFTING_TABLE) && !ModifierUtil.checkVolatileFlag(stack, ToolInventoryCapability.INVENTORY_CRAFTING)) {
            return InteractionResult.PASS;
        } else {
            if (player instanceof ServerPlayer serverPlayer) {
                NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((id, inventory, p) -> new UnsyncedToolContainerMenu(id, inventory, stack, handler, slotIndex), ToolNameHook.getName(definition, stack, tool)), (buf) -> {
                    buf.writeVarInt(slotIndex);
                });
            }

            return InteractionResult.sidedSuccess(player.level().isClientSide);
        }
    }
}
