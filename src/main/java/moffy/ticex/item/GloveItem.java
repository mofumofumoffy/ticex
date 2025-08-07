package moffy.ticex.item;

import java.util.List;

import moffy.ticex.TicEX;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class GloveItem extends Item{

    public GloveItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Rarity getRarity(ItemStack pStack) {
        return Rarity.UNCOMMON;
    }

    @Override
    public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> pTooltipComponents,
            TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("item.ticex.exhausted_glove.desc").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
    }
}
