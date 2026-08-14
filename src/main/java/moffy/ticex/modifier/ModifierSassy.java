package moffy.ticex.modifier;

import moffy.ticex.lib.hook.CriticalModifierHook;
import moffy.ticex.registry.TicEXModifierHooks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ModifierSassy extends NoLevelsModifier implements CriticalModifierHook {

    @Override
    public int getPriority() {
        return 1001;
    }

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, TicEXModifierHooks.CRITICAL);
    }

    @Override
    public boolean isCritical(IToolStackView tool, ModifierEntry entry, Player attacker, Entity target, boolean isCritical, boolean original) {
        return true;
    }

    @Override
    public float setCriticalModifier(IToolStackView tool, ModifierEntry entry, Player attacker, Entity target, boolean isCritical, float originalModifier, float currentModifier) {
        return Math.max(currentModifier, 1.5f);
    }
}
