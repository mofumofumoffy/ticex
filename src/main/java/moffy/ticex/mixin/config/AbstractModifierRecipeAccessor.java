package moffy.ticex.mixin.config;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.recipe.modifiers.adding.AbstractModifierRecipe;

@Mixin(value = AbstractModifierRecipe.class, remap = false)
public interface AbstractModifierRecipeAccessor {
    @Accessor("level")
    @Mutable
    void setLevel(IntRange level);
}
