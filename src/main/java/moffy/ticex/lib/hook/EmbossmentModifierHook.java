package moffy.ticex.lib.hook;

import java.util.Collection;

import net.minecraft.world.item.ItemStack;

public interface EmbossmentModifierHook {
    public boolean applyItem(ItemStack toolStack, ItemStack input, boolean simulate);

    public static class DefaultClass implements EmbossmentModifierHook{
        @Override
        public boolean applyItem(ItemStack toolStack, ItemStack input, boolean simulate) {
            return false;
        }
    }

    record AllMerger(Collection<EmbossmentModifierHook> modules) implements EmbossmentModifierHook{
        @Override
        public boolean applyItem(ItemStack toolStack, ItemStack input, boolean simulate) {
            for(EmbossmentModifierHook module : modules){
                boolean result = module.applyItem(toolStack, input, simulate);
                if(result){
                    return true;
                }
            }
            return false;
        }
    }
}
