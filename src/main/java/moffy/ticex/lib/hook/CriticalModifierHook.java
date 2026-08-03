package moffy.ticex.lib.hook;

import moffy.ticex.registry.TicEXModifierHooks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Collection;

public interface CriticalModifierHook {
    default boolean isCritical(IToolStackView tool, ModifierEntry entry, Player attacker, Entity target, boolean isCritical, boolean original){
        return isCritical;
    }
    default float setCriticalRate(IToolStackView tool, ModifierEntry entry, Player attacker, Entity target, boolean isCritical, float currentRate, float originalRate){
        return currentRate;
    }

    static CriticalContext modifyCritical(Player player, Entity target, boolean isCritical, float criticalModifier){
        boolean currentCrit = isCritical;
        float currentModifier = criticalModifier;

        for(EquipmentSlot slot : EquipmentSlot.values()){
            ItemStack stack = player.getItemBySlot(slot);
            if(stack.getItem() instanceof IModifiable){
                ToolStack tool = ToolStack.from(stack);

                for(ModifierEntry entry : tool.getModifierList()){
                    CriticalModifierHook hook = entry.getHook(TicEXModifierHooks.CRITICAL);
                    currentCrit = hook.isCritical(tool, entry, player, target, currentCrit, isCritical);
                }

                for(ModifierEntry entry : tool.getModifierList()) {
                    CriticalModifierHook hook = entry.getHook(TicEXModifierHooks.CRITICAL);
                    currentModifier = hook.setCriticalRate(tool, entry, player, target, currentCrit, currentModifier, criticalModifier);
                }
            }
        }

        if(currentCrit){
            CriticalHitEvent criticalHitEvent = ForgeHooks.getCriticalHit(player, target, currentCrit, currentModifier);
            if(criticalHitEvent != null && !criticalHitEvent.isCanceled()){
                return new CriticalContext(criticalHitEvent.isVanillaCritical(), criticalHitEvent.getDamageModifier());
            }
        }

        return new CriticalContext(currentCrit, criticalModifier);
    }

    class DefaultClass implements CriticalModifierHook{

    }

    record AllMerger(Collection<CriticalModifierHook> hooks) implements CriticalModifierHook{
        @Override
        public boolean isCritical(IToolStackView tool, ModifierEntry entry, Player attacker, Entity target, boolean isCritical, boolean original) {
            boolean currentValue = original;
            for(CriticalModifierHook hook : hooks){
                currentValue = hook.isCritical(tool, entry, attacker, target, currentValue, original);
            }
            return currentValue;
        }

        @Override
        public float setCriticalRate(IToolStackView tool, ModifierEntry entry, Player attacker, Entity target, boolean isCritical, float currentRate, float originalRate) {
            float rate = originalRate;
            for(CriticalModifierHook hook : hooks){
                rate = hook.setCriticalRate(tool, entry, attacker, target, isCritical, rate, originalRate);
            }
            return rate;
        }
    }

    record CriticalContext(boolean isCritical, float criticalModifier){

    }
}
