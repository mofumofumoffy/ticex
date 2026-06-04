package moffy.ticex.lib.hook;

import moffy.ticex.TicEX;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.damagesource.DamageSource;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

public interface DamageSourceModifierHook {
    DamageSource modifyDamageSource(IToolStackView tool, ModifierEntry modifierEntry, DamageSource currentSource, DamageSource original);

    class DefaultClass implements DamageSourceModifierHook {
        @Override
        public DamageSource modifyDamageSource(IToolStackView tool, ModifierEntry modifierEntry, DamageSource currentSource, DamageSource original) {
            return currentSource;
        }
    }

    record AllMerger(Collection<DamageSourceModifierHook> modules) implements DamageSourceModifierHook {
        @Override
        public DamageSource modifyDamageSource(IToolStackView tool, ModifierEntry modifierEntry, DamageSource currentSource, DamageSource original) {
            DamageSource source = original;
            for(DamageSourceModifierHook hook : modules){
                source = hook.modifyDamageSource(tool, modifierEntry, source, original);
            }
            return source;
        }
    }

    static DamageSource modifyDamageSource(IToolStackView tool, DamageSource original){
        DamageSource currentDamageSource = original;
        for(ModifierEntry entry : tool.getModifierList()){
            currentDamageSource = entry.getHook(TicEXModifierHooks.DAMAGE_SOURCE).modifyDamageSource(tool, entry, currentDamageSource, original);
        }
        TicEX.LOGGER.info(currentDamageSource.type().toString());
        return currentDamageSource;
    }
}
