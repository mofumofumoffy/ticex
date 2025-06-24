package moffy.ticex.event;

import moffy.ticex.modifier.ModifierGravitiy;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class TicEXPEEvent {

    public static void onJump(LivingEvent.LivingJumpEvent evt) {
        if (evt.getEntity() instanceof Player player && player.level().isClientSide) {
            ItemStack leggingsStack = player.getItemBySlot(EquipmentSlot.LEGS);
            if (leggingsStack.getItem() instanceof IModifiable) {
                ToolStack leggings = ToolStack.from(leggingsStack);
                for (ModifierEntry entry : leggings.getModifierList()) {
                    if (entry.getLazyModifier().get() instanceof ModifierGravitiy gravitiyModifier) {
                        gravitiyModifier.getLastJumpTracker().put(player.getId(), player.level().getGameTime());
                        break;
                    }
                }
            }
        }
    }
}
