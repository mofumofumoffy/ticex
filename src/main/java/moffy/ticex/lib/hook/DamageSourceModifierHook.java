package moffy.ticex.lib.hook;

import moffy.ticex.TicEX;
import moffy.ticex.registry.TicEXModifierHooks;
import net.minecraft.world.damagesource.DamageSource;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

public interface DamageSourceModifierHook {
    DamageSource modifyDamageSource(IToolStackView tool, ModifierEntry modifierEntry, ToolAttackContext context, DamageSource currentSource, DamageSource original);

    class DefaultClass implements DamageSourceModifierHook {
        @Override
        public DamageSource modifyDamageSource(IToolStackView tool, ModifierEntry modifierEntry, ToolAttackContext context, DamageSource currentSource, DamageSource original) {
            return currentSource;
        }
    }

    record AllMerger(Collection<DamageSourceModifierHook> modules) implements DamageSourceModifierHook {
        @Override
        public DamageSource modifyDamageSource(IToolStackView tool, ModifierEntry modifierEntry, ToolAttackContext context, DamageSource currentSource, DamageSource original) {
            DamageSource source = original;
            for(DamageSourceModifierHook hook : modules){
                source = hook.modifyDamageSource(tool, modifierEntry, context, source, original);
            }
            return source;
        }
    }

    static DamageSource modifyDamageSource(IToolStackView tool, ToolAttackContext context, DamageSource original){
        DamageSource currentDamageSource = original;
        for(ModifierEntry entry : tool.getModifierList()){
            currentDamageSource = entry.getHook(TicEXModifierHooks.DAMAGE_SOURCE).modifyDamageSource(tool, entry, context, currentDamageSource, original);
        }
        TicEX.LOGGER.info(currentDamageSource.type().toString());
        return currentDamageSource;
    }
}
