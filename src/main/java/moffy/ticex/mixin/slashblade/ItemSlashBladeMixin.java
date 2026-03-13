package moffy.ticex.mixin.slashblade;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.tools.item.IModifiable;

import java.util.function.Consumer;

@Mixin(value = ItemSlashBlade.class, remap = false)
public class ItemSlashBladeMixin {

    @Inject(at = @At("HEAD"), method = "getOnBroken", cancellable = true)
    private static void getOnBroken(ItemStack stack, CallbackInfoReturnable<Consumer<LivingEntity>> cb) {
        if (stack.getItem() instanceof IModifiable) {
            cb.setReturnValue(user ->
                    user.broadcastBreakEvent(user.getUsedItemHand())
            );
        }
    }
}
