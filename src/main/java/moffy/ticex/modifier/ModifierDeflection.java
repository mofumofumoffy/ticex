package moffy.ticex.modifier;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ModifierDeflection extends NoLevelsModifier implements MeleeHitModifierHook{

    @Override
    public int getPriority() {
        return -1;
    }

    @Override
    protected void registerHooks(Builder hookBuilder) {
        super.registerHooks(hookBuilder);
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
    }

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage,
            float baseKnockback, float knockback) {
        if(!context.isExtraAttack()){
            Entity entity = context.getTarget();
            for (ModifierEntry modifierEntry : tool.getModifierList()){
                ToolAttackContext newContext = new ToolAttackContext(context.getAttacker(), context.getPlayerAttacker(), context.getHand(), entity, entity instanceof LivingEntity ? (LivingEntity)entity : null, context.isCritical(), damage, false);
                modifierEntry.getHook(ModifierHooks.MELEE_HIT).afterMeleeHit(tool, modifier, newContext, damage);
            }
        }
        return 0;
    }
}
