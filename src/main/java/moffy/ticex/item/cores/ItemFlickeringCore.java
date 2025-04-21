package moffy.ticex.item.cores;

import java.util.List;

import moffy.ticex.TicEX;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ItemFlickeringCore extends Item{

    public ItemFlickeringCore(Properties pProperties) {
        super(pProperties);
    }
    
    @Override
    public boolean isFoil(ItemStack pStack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> pTooltipComponents,
            TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("item." + TicEX.MODID + ".flickering_reconstruction_core.desc").withStyle(ChatFormatting.GOLD));
    }
}
