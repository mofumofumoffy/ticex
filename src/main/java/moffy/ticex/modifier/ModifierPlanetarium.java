package moffy.ticex.modifier;

import moffy.ticex.lib.hook.CriticalModifierHook;
import moffy.ticex.registry.TicEXModifierHooks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ModifierPlanetarium extends Modifier implements CriticalModifierHook {

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, TicEXModifierHooks.CRITICAL);
    }

    @Override
    public boolean isCritical(IToolStackView tool, ModifierEntry entry, Player attacker, Entity target, boolean isCritical, boolean original) {
        if(!isCritical){
            int moonPhase = attacker.level().getMoonPhase();
            int critRoll = attacker.getRandom().nextIntBetweenInclusive(0, Math.abs(4 - moonPhase));
            if(critRoll < entry.getLevel()){
                return true;
            }
        }
        return isCritical;
    }

    @Override
    public float setCriticalModifier(IToolStackView tool, ModifierEntry entry, Player attacker, Entity target, boolean isCritical, float originalModifier, float currentModifier) {
        if(isCritical){
            return Math.max(currentModifier, 1.5f);
        }
        return currentModifier;
    }
}
