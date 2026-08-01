package moffy.ticex.modifier;

import moffy.ticex.lib.hook.CriticalModifierHook;
import moffy.ticex.lib.hook.TicEXModifierHooks;
import moffy.ticex.mixin.CriticalAccessor;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
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
    public float setCriticalRate(IToolStackView tool, ModifierEntry entry, Player attacker, Entity target, boolean isCritical, float currentRate, float originalRate) {
        return Math.max(currentRate, 1.5f);
    }
}
