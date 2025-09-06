package moffy.ticex.lib.modules.mekanism.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.ToolAction;

import java.util.Map;

public interface IMekaForgeItemHandler {
    boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, Player player);
    boolean canPerformAction(ItemStack stack, ToolAction action);
    boolean isNotReplaceableByPickAction(ItemStack stack, Player player, int inventorySlot);
    int getEnchantmentLevel(ItemStack stack, Enchantment enchantment);
    Map<Enchantment, Integer> getAllEnchantments(ItemStack stack);
}
