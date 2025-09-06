package moffy.ticex.mixin.mekanism;

import mekanism.common.inventory.container.ModuleTweakerContainer;
import moffy.ticex.lib.modules.mekanism.MekaGearCapability;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.tools.item.IModifiable;

@Mixin(value = ModuleTweakerContainer.class, remap = false)
public class ModuleTweakerContainerMixin {
    @Inject(
            at = @At("RETURN"),
            method = "isTweakableItem",
            cancellable = true
    )
    private static void isTweakableItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if(stack.getItem() instanceof IModifiable){
            cir.setReturnValue(stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent());
        }
    }
}
