package moffy.ticex.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.materials.RandomMaterial;

@Mixin(value = RandomMaterial.RandomBuilder.class, remap = false)
public interface RandomBuilderAccessor {
    @Accessor("tier")
    void setTier(IntRange tier);
}
