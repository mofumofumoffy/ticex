package moffy.ticex.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.utils.RomanNumeralHelper;

@Mixin(value = RomanNumeralHelper.class, remap = false)
public class RomanNumeralHelperMixin {
    @Inject(method = "intToRomanNumeral", at = @At("HEAD"), cancellable = true)
    private static void modifyRomanNumeral(int value, CallbackInfoReturnable<String> cir) {
        if (value >= 50000) {
            cir.setReturnValue(Integer.toString(value));
        }
    }
}
