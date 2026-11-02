package moffy.ticex.item.modifiable;

import mekanism.common.config.MekanismConfig;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.StorageUtils;
import moffy.ticex.registry.TicEXToolDefinitions;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;

public class ModifiableMekaTool extends ModifiableItem {
    public ModifiableMekaTool(Properties properties) {
        super(properties, TicEXToolDefinitions.MEKA_TOOL_DEFINITION);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ToolCapabilityProvider(stack);
    }

    public boolean isBarVisible(@NotNull ItemStack stack) {
        return true;
    }

    public int getBarWidth(@NotNull ItemStack stack) {
        return StorageUtils.getEnergyBarWidth(stack);
    }

    public int getBarColor(@NotNull ItemStack stack) {
        return MekanismConfig.client.energyColor.get();
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }

    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }

    public boolean isCorrectToolForDrops(@NotNull BlockState state) {
        return true;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return true;
    }

    public boolean isNotReplaceableByPickAction(ItemStack stack, Player player, int inventorySlot) {
        return super.isNotReplaceableByPickAction(stack, player, inventorySlot) || ItemDataUtils.hasData(stack, "modules", 10);
    }

    @Override
    public Component getName(ItemStack stack) {
        return MutableComponent.create(super.getName(stack).getContents()).withStyle(ChatFormatting.LIGHT_PURPLE);
    }
}
