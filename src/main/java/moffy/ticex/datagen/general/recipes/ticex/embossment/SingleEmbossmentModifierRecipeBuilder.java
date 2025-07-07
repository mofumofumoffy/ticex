package moffy.ticex.datagen.general.recipes.ticex.embossment;

import moffy.ticex.lib.recipe.SingleEmbossmentModifierRecipe;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.modifiers.adding.AbstractModifierRecipeBuilder;

import java.util.function.Consumer;

public class SingleEmbossmentModifierRecipeBuilder extends AbstractModifierRecipeBuilder<SingleEmbossmentModifierRecipeBuilder> {
    private Ingredient embossInput;

    protected SingleEmbossmentModifierRecipeBuilder(ModifierId result, Ingredient embossInput) {
        super(result);
        this.embossInput = embossInput;
    }

    public SingleEmbossmentModifierRecipeBuilder setEmbossInput(Ingredient embossInput) {
        this.embossInput = embossInput;
        return this;
    }

    public static SingleEmbossmentModifierRecipeBuilder modifier(ModifierId result, Ingredient input) {
        return new SingleEmbossmentModifierRecipeBuilder(result, input);
    }

    @Override
    public void save(@NotNull Consumer<FinishedRecipe> consumer, @NotNull ResourceLocation id) {
        if (this.embossInput.isEmpty()) {
            throw new IllegalStateException("Must have at least 1 input");
        } else {
            ResourceLocation advancementId = this.buildOptionalAdvancement(id, "modifiers");
            consumer.accept(new LoadableFinishedRecipe<>(
                    new SingleEmbossmentModifierRecipe(id, embossInput, tools, maxToolSize, result, ModifierEntry.VALID_LEVEL.range(minLevel, maxLevel), slots),
                    SingleEmbossmentModifierRecipe.LOADER,
                    advancementId));
        }

    }
}
