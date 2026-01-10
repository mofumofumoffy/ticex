package moffy.ticex.item.modifiable;

import moffy.ticex.client.modules.ticex.UnsyncedToolContainerMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.utils.Util;

import java.util.List;

public class ModifiableGauntlet extends ModifiableItem {
    private int[] cooldowns;

    public ModifiableGauntlet(Properties properties, ToolDefinition toolDefinition) {
        super(properties, toolDefinition);
        this.cooldowns = new int[6]; // max slots;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level levelIn, Player playerIn, @NotNull InteractionHand handIn) {
        if (playerIn.isShiftKeyDown()) {
            ItemStack stack = playerIn.getItemInHand(handIn);
            InteractionResult result = UnsyncedToolContainerMenu.tryOpenContainer(stack, null, getToolDefinition(), playerIn, Util.getSlotType(handIn));
            if (result.consumesAction()) {
                return new InteractionResultHolder<>(result, stack);
            }
        }

        return super.use(levelIn, playerIn, handIn);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler -> {
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                ItemStack stackInSlot = itemHandler.getStackInSlot(i);
                tooltip.add(
                        Component.empty().append(stackInSlot.getDisplayName())
                                .withStyle(style -> style.withColor(ChatFormatting.GRAY))
                );
            }
        });
    }

    @Override
    public @NotNull Rarity getRarity(@NotNull ItemStack stack) {
        return Rarity.EPIC;
    }
}
