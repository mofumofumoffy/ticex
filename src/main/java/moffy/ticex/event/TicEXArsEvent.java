package moffy.ticex.event;

/*
 * This file is part of the TicEXArsModule.
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 * See the LICENSES/LGPL-3.0.md file for details.
 * 2025 Moffy
 */

import com.hollingsworth.arsnouveau.api.event.SpellResolveEvent;
import moffy.ticex.TicEX;
import moffy.ticex.TicEXConfig;
import moffy.ticex.modifier.ModifierReactive;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class TicEXArsEvent {
    public static void beforeCastSpell(SpellResolveEvent.Pre event){
        ItemStack stack = event.context.getCasterTool();

        if(stack.getItem() instanceof IModifiable){
            ToolStack toolStack = ToolStack.from(stack);
            if(toolStack.getPersistentData().contains(ModifierReactive.REACTIVE_COOLDOWN) && toolStack.getPersistentData().getInt(ModifierReactive.REACTIVE_COOLDOWN) > 0){
                event.context.setCanceled(true);
                event.setCanceled(true);
            }
        }
    }

    public static void afterCastSpell(SpellResolveEvent.Post event){
        LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER).execute(() -> {
            ItemStack stack = event.context.getCasterTool();

            if(stack.getItem() instanceof IModifiable){
                ToolStack toolStack = ToolStack.from(stack);
                if(toolStack.getPersistentData().contains(ModifierReactive.REACTIVE_COOLDOWN)){
                    toolStack.getPersistentData().putInt(ModifierReactive.REACTIVE_COOLDOWN, TicEXConfig.REACTIVE_COOLDOWN_TICK.get());
                    TicEX.LOGGER.info("{}", toolStack.getPersistentData().getInt(ModifierReactive.REACTIVE_COOLDOWN));
                }
            }
        });

    }
}
