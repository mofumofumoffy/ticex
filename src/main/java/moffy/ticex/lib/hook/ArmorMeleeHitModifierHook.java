package moffy.ticex.lib.hook;

import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

public interface ArmorMeleeHitModifierHook extends MeleeHitModifierHook {
    record AllMerger(Collection<ArmorMeleeHitModifierHook> modules) implements ArmorMeleeHitModifierHook{
        public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
            for(ArmorMeleeHitModifierHook module : this.modules) {
                knockback = module.beforeMeleeHit(tool, modifier, context, damage, baseKnockback, knockback);
            }

            return knockback;
        }

        public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
            for(ArmorMeleeHitModifierHook module : this.modules) {
                module.afterMeleeHit(tool, modifier, context, damageDealt);
            }

        }

        public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
            for(ArmorMeleeHitModifierHook module : this.modules) {
                module.failedMeleeHit(tool, modifier, context, damageAttempted);
            }

        }
    }

    class DefaultClass implements ArmorMeleeHitModifierHook{

    }
}
