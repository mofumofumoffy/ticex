package moffy.ticex.event;

/*
 * This file is part of the TicEXArsModule.
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 * See the LICENSES/LGPL-3.0.md file for details.
 * 2025 Moffy
 */

import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import com.hollingsworth.arsnouveau.api.event.SpellResolveEvent;
import moffy.ticex.TicEX;
import moffy.ticex.TicEXConfig;
import moffy.ticex.mixin.arsnouveau.OriginalStackAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.item.IModifiable;

public class TicEXArsEvent {
    public static void onResolveSpellPre(SpellResolveEvent.Pre event){
        ItemStack toolStack = ((OriginalStackAccessor)event.context).getOriginalStack();
        if(!toolStack.isEmpty() && toolStack.getItem() instanceof IModifiable){
            CompoundTag nbt = toolStack.getOrCreateTag();
            if(nbt.contains("reactive_cooldown") && nbt.getInt("reactive_cooldown") > 0){
                event.context.setCanceled(true);
                event.setCanceled(true);
            }
        }
    }

    public static void onResolveSpellPost(SpellResolveEvent.Post event){
        if(!event.context.isCanceled()){
            ItemStack toolStack = ((OriginalStackAccessor)event.context).getOriginalStack();
            if(!toolStack.isEmpty() && toolStack.getItem() instanceof IModifiable){
                CompoundTag nbt = toolStack.getOrCreateTag();
                //nbt.putInt("reactive_cooldown", TicEXConfig.REACTIVE_COOLDOWN.get());
            }

        }
    }
}
