package moffy.ticex.event;

/*
 * This file is part of the TicEXArsModule.
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 * See the LICENSES/LGPL-3.0.md file for details.
 * 2025 Moffy
 */

import com.hollingsworth.arsnouveau.api.event.SpellResolveEvent;
import moffy.ticex.TicEXConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class TicEXArsEvent {
    public static void onResolveSpellPre(SpellResolveEvent.Pre event){
        ItemStack toolStack = event.context.getCasterTool();
        CompoundTag nbt = toolStack.getOrCreateTag();
        if(nbt.contains("reactive_cooldown") && nbt.getInt("reactive_cooldown") > 0){
            event.setCanceled(true);
        }
    }

    public static void onResolveSpellPost(SpellResolveEvent.Pre event){
        ItemStack toolStack = event.context.getCasterTool();
        CompoundTag nbt = toolStack.getOrCreateTag();
        nbt.putInt("reactive_cooldown", TicEXConfig.REACTIVE_COOLDOWN.get());
    }
}
