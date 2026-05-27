package moffy.ticex.lib.hook;

import moffy.ticex.TicEX;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Collection;

public interface CriticalModifierHook {
    default boolean isCritical(IToolStackView tool, ModifierEntry entry, boolean isCritical, boolean original){
        return isCritical;
    }
    default float setCriticalRate(IToolStackView tool, ModifierEntry entry, float currentRate, float originalRate){
        return currentRate;
    }

    static CriticalContext modifyCritical(LivingEntity entity, boolean isCritical, float criticalModifier){
        boolean currentCrit = isCritical;
        float currentModifier = criticalModifier;

        for(EquipmentSlot slot : EquipmentSlot.values()){
            ItemStack stack = entity.getItemBySlot(slot);
            if(stack.getItem() instanceof IModifiable){
                ToolStack tool = ToolStack.from(stack);

                for(ModifierEntry entry : tool.getModifierList()){
                    CriticalModifierHook hook = entry.getHook(TicEXModifierHooks.CRITICAL);
                    currentCrit = hook.isCritical(tool, entry, currentCrit, isCritical);
                    currentModifier = hook.setCriticalRate(tool, entry, currentModifier, criticalModifier);
                }
            }
        }

        return new CriticalContext(currentCrit, criticalModifier);
    }

    class DefaultClass implements CriticalModifierHook{

    }

    record AllMerger(Collection<CriticalModifierHook> hooks) implements CriticalModifierHook{
        @Override
        public boolean isCritical(IToolStackView tool, ModifierEntry entry, boolean isCritical, boolean original) {
            boolean currentValue = original;
            for(CriticalModifierHook hook : hooks){
                currentValue = hook.isCritical(tool, entry, currentValue, original);
            }
            return currentValue;
        }

        @Override
        public float setCriticalRate(IToolStackView tool, ModifierEntry entry, float currentRate, float originalRate) {
            float rate = originalRate;
            for(CriticalModifierHook hook : hooks){
                rate = hook.setCriticalRate(tool, entry, rate, originalRate);
            }
            return rate;
        }
    }

    record CriticalContext(boolean isCritical, float criticalModifier){

    }
}
