package moffy.ticex.modifier;

import moffy.ticex.mixin.CriticalAccessor;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ModifierSassy extends NoLevelsModifier implements MeleeDamageModifierHook {

    @Override
    public int getPriority() {
        return 1001;
    }

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
    }

    @Override
    public float getMeleeDamage(
            @NotNull IToolStackView tool,
            @NotNull ModifierEntry modifierEntry,
            ToolAttackContext context,
            float baseDamage,
            float damage
    ) {
        if(context.getCriticalModifier() < 1.5) {
            ((CriticalAccessor) context).setCriticalModifier(1.5f); // set critical true
        }
        if(context.getPlayerAttacker() != null){
            ForgeHooks.getCriticalHit(context.getPlayerAttacker(), context.getTarget(), false, 1.5f);
        }
        return damage * 1.5f;
    }
}
