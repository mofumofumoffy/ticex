package moffy.ticex.lib.hook;

import moffy.ticex.TicEX;
import moffy.ticex.lib.utils.TicEXUtils;
import moffy.ticex.registry.TicEXModifierHooks;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Collection;

public interface DamageSourceModifierHook {
    DamageSource modifyDamageSource(IToolStackView tool, ModifierEntry modifierEntry, ToolAttackContext context, DamageSource original, DamageSource currentSource);

    class DefaultClass implements DamageSourceModifierHook {
        @Override
        public DamageSource modifyDamageSource(IToolStackView tool, ModifierEntry modifierEntry, ToolAttackContext context, DamageSource original, DamageSource currentSource) {
            return currentSource;
        }
    }

    record AllMerger(Collection<DamageSourceModifierHook> modules) implements DamageSourceModifierHook {
        @Override
        public DamageSource modifyDamageSource(IToolStackView tool, ModifierEntry modifierEntry, ToolAttackContext context, DamageSource original, DamageSource currentSource) {
            DamageSource source = original;
            for(DamageSourceModifierHook hook : modules){
                source = hook.modifyDamageSource(tool, modifierEntry, context, source, original);
            }
            return source;
        }
    }

    static DamageSource modifyDamageSource(LivingEntity entity, ToolAttackContext context, DamageSource original){
        DamageSource source = original;
        for(EquipmentSlot slot : TicEXUtils.EquipmentSlotLists.WITHOUT_OFFHAND){
            ItemStack stack = entity.getItemBySlot(slot);
            if(stack.getItem() instanceof IModifiable){
                ToolStack tool = ToolStack.from(stack);
                for(ModifierEntry entry : tool.getModifierList()){
                    source = entry.getHook(TicEXModifierHooks.DAMAGE_SOURCE).modifyDamageSource(tool, entry, context, original, source);
                }
            }
        }
        return source;
    }
}
