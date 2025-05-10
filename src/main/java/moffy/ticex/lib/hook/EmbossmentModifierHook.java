package moffy.ticex.lib.hook;

import java.util.Collection;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;

public interface EmbossmentModifierHook {
    public boolean applyItem(EmbossmentContext context, int inputIndex, boolean simulate);

    public static class DefaultClass implements EmbossmentModifierHook{
        @Override
        public boolean applyItem(EmbossmentContext context, int inputIndex, boolean simulate) {
            return false;
        }
    }

    record AllMerger(Collection<EmbossmentModifierHook> modules) implements EmbossmentModifierHook{
        @Override
        public boolean applyItem(EmbossmentContext context, int inputIndex, boolean simulate) {
            for(EmbossmentModifierHook module : modules){
                boolean result = module.applyItem(context, inputIndex, simulate);
                if(result){
                    return true;
                }
            }
            return false;
        }
    }

    public static class EmbossmentContext{
        private ItemStack toolStack;
        private ITinkerStationContainer inv;
        private Component errorMsg;

        public EmbossmentContext(ItemStack toolStack, ITinkerStationContainer inv){
            this.toolStack = toolStack;
            this.inv = inv;
            this.errorMsg = Component.translatable("recipe.ticex.embossment_not_allowed");
        }

        public ItemStack getToolStack() {
            return toolStack;
        }

        public ITinkerStationContainer getInv() {
            return inv;
        }

        public ItemStack getInputStack(int index){
            return inv.getInput(index);
        }

        public Component getErrorMsg() {
            return errorMsg;
        }

        public void setToolStack(ItemStack toolStack) {
            this.toolStack = toolStack;
        }

        public void setInv(ITinkerStationContainer inv) {
            this.inv = inv;
        }

        public void setErrorMsg(Component errorMsg) {
            this.errorMsg = errorMsg;
        }
    }
}
