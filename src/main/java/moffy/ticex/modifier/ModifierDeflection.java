package moffy.ticex.modifier;

import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ModifierDeflection extends NoLevelsModifier implements MeleeDamageModifierHook{

    @Override
    public int getPriority() {
        return 1000;
    }

    @Override
    protected void registerHooks(Builder hookBuilder) {
        super.registerHooks(hookBuilder);
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
    }

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifierEntry, ToolAttackContext context, float baseDamage,
            float damage) {
        for(ModifierEntry toolEntry:tool.getModifierList()){
            toolEntry.getHook(ModifierHooks.MELEE_HIT).beforeMeleeHit(tool, modifierEntry, context, damage, baseDamage, damage);
        }
        for(ModifierEntry toolEntry:tool.getModifierList()){
            toolEntry.getHook(ModifierHooks.MELEE_HIT).afterMeleeHit(tool, modifierEntry, context, damage);
        }
        return 0;
    }
}
