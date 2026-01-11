package moffy.ticex.event;

/*
 * This file is part of the TicEXArsModule.
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 * See the LICENSES/LGPL-3.0.md file for details.
 * 2025 Moffy
 */

import com.hollingsworth.arsnouveau.api.event.SpellResolveEvent;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import moffy.ticex.TicEXConfig;
import moffy.ticex.lib.modules.arsnouveau.interfaces.OriginalStackAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.item.IModifiable;

public class TicEXArsEvent {

    public static void onResolveSpellPre(SpellResolveEvent.Pre event){
        ItemStack toolStack = findTinkerTool(event.context);

        if(!toolStack.isEmpty()){
            CompoundTag nbt = toolStack.getOrCreateTag();
            if(nbt.contains("reactive_cooldown") && nbt.getInt("reactive_cooldown") > 0){
                event.context.setCanceled(true);
                event.setCanceled(true);
            }
        }
    }
    public static void onResolveSpellPost(SpellResolveEvent.Post event){
        if(!event.context.isCanceled()){
            ItemStack toolStack = findTinkerTool(event.context);

            if(!toolStack.isEmpty()){
                CompoundTag nbt = toolStack.getOrCreateTag();
                nbt.putInt("reactive_cooldown", TicEXConfig.REACTIVE_COOLDOWN.get());
            }
        }
    }
    private static ItemStack findTinkerTool(SpellContext context) {
        if (context instanceof OriginalStackAccessor accessor) {
            ItemStack stack = accessor.getOriginalStack();
            if (isTinkerTool(stack)) return stack;
        }
        ItemStack arsTool = context.getCasterTool();
        if (isTinkerTool(arsTool)) return arsTool;
        LivingEntity caster = context.getUnwrappedCaster();

        if (caster != null) {
            ItemStack mainHand = caster.getMainHandItem();
            if (isTinkerTool(mainHand)) return mainHand;

            ItemStack offHand = caster.getOffhandItem();
            if (isTinkerTool(offHand)) return offHand;
        }
        return ItemStack.EMPTY;
    }

    private static boolean isTinkerTool(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.getItem() instanceof IModifiable;
    }
}