package moffy.ticex.mixin.config;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import slimeknights.tconstruct.library.recipe.casting.AbstractCastingRecipe;

@Mixin(value = AbstractCastingRecipe.class, remap = false)
public interface AbstractCastingRecipeAccessor {
    @Accessor("consumed")
    @Mutable
    void setConsumed(boolean consumed);
}
