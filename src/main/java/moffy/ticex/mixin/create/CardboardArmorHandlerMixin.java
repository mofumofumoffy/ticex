package moffy.ticex.mixin.create;

import com.simibubi.create.content.equipment.armor.CardboardArmorHandler;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@Mixin(value = CardboardArmorHandler.class, remap = false)
public class CardboardArmorHandlerMixin {

    @Inject(at = @At("RETURN"), method = "testForStealth", cancellable = true)
    private static void testForStealth(Entity entityIn, CallbackInfoReturnable<Boolean> ci) {
        if (!(entityIn instanceof LivingEntity entity)) return;

        if (entity.getPose() != Pose.CROUCHING) return;

        if (entity instanceof Player player && player.getAbilities().flying) return;
        if (TicEXRegistry.CARDBOARD_MODIFIER != null) {
            for (ItemStack armorStack : entityIn.getArmorSlots()) {
                if (armorStack.getItem() instanceof IModifiable) {
                    ToolStack armor = ToolStack.from(armorStack);
                    if (armor.getModifierLevel(TicEXRegistry.CARDBOARD_MODIFIER.get()) <= 0) {
                        return;
                    }
                } else {
                    return;
                }
            }
        }

        ci.setReturnValue(true);
    }
}
