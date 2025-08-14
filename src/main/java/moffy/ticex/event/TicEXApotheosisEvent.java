package moffy.ticex.event;

import dev.shadowsoffire.apotheosis.adventure.event.ItemSocketingEvent;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class TicEXApotheosisEvent {
    public static void onSocketGem(ItemSocketingEvent.ModifyResult event){
        ItemStack stack = event.getOutput();
        if(stack.getItem() instanceof IModifiable){
            ToolStack tool = ToolStack.from(stack);
            if(tool.getModifierLevel(TicEXRegistry.APOTH_SUPPLIER_MODIFIER.get()) < 1){
                tool.addModifier(TicEXRegistry.APOTH_SUPPLIER_MODIFIER.getId(), 1);
            }
        }
    }
}
