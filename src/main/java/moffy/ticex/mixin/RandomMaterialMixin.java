package moffy.ticex.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.materials.RandomMaterial;

@Mixin(value = RandomMaterial.RandomBuilder.class, remap = false)
public class RandomMaterialMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        ((RandomBuilderAccessor) (Object) this).setTier(new IntRange(0, 100));
    }
}
