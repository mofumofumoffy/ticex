package moffy.ticex.item.cores;

import java.util.List;

import moffy.ticex.TicEX;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ItemReconstCore extends Item{

    protected String modifierName;
    protected int modifierLevel;
    protected boolean applyLevel;

    public ItemReconstCore(Properties properties, String modifierName) {
        this(properties, modifierName, 1);
        this.applyLevel = false;
    }

    public ItemReconstCore(Properties properties, String modifierName, int modifierLevel){
        super(properties);
        this.modifierName = modifierName;
        this.modifierLevel = modifierLevel;
        this.applyLevel = true;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return modifierName != null;
    }

    @Override
    public Component getName(ItemStack pStack) {
        return Component.translatable("item.ticex.reconstruction_core").withStyle(ChatFormatting.BLUE);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> components, TooltipFlag tooltipFlag) {
        if(modifierName == null){
            components.add(Component.translatable("item." + TicEX.MODID + ".reconstruction_core.desc").withStyle(ChatFormatting.GRAY));
        } else if(applyLevel){
            components.add(Component.translatable("modifier." + TicEX.MODID + "." + modifierName + "." + modifierLevel).withStyle(ChatFormatting.AQUA));
        } else {
            components.add(Component.translatable("modifier." + TicEX.MODID + "." + modifierName).withStyle(ChatFormatting.AQUA));
        }
    }
}
