package moffy.ticex.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.materials.RandomMaterial;

@Mixin(value = RandomMaterial.RandomBuilder.class, remap = false)
public abstract class RandomMaterialMixin {

    @Accessor("tier")
    public abstract void setTier(IntRange tier);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        setTier(new IntRange(0, 100));
    }
}
