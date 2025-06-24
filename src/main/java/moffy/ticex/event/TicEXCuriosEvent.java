package moffy.ticex.event;

import moffy.ticex.caps.curios.IncomparableCuriosCapability;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingSwapItemsEvent;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class TicEXCuriosEvent {

    public static void onItemSwap(LivingSwapItemsEvent.Hands event) {
        if (hasIncomparable(event.getItemSwappedToMainHand()) || hasIncomparable(event.getItemSwappedToOffHand())) {
            event.setCanceled(true);
        }
    }

    public static void onRegisterCaps(RegisterCapabilitiesEvent event) {
        event.register(IncomparableCuriosCapability.class);
    }

    private static boolean hasIncomparable(ItemStack stack) {
        if (stack.getItem() instanceof IModifiable) {
            ToolStack tool = ToolStack.from(stack);
            return tool.getModifierLevel(TicEXRegistry.INCOMPARABLE_MODIFIER.get()) > 0;
        }
        return false;
    }
}
