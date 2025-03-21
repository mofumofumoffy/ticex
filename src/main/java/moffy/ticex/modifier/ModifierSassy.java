package moffy.ticex.modifier;

import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ModifierSassy extends NoLevelsModifier implements MeleeHitModifierHook{
    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage,
            float baseKnockback, float knockback) {
        ToolAttackContext newContext = new ToolAttackContext(context.getAttacker(), context.getPlayerAttacker(), context.getHand(), context.getTarget(), context.getLivingTarget(), true, context.getCooldown(), context.isExtraAttack());
        return MeleeHitModifierHook.super.beforeMeleeHit(tool, modifier, newContext, damage, baseKnockback, knockback);
    }
}
