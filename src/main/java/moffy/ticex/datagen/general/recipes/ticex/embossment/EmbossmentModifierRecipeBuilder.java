package moffy.ticex.datagen.general.recipes.ticex.embossment;

import moffy.ticex.lib.recipe.EmbossmentModifierRecipe;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.modifiers.adding.AbstractModifierRecipeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EmbossmentModifierRecipeBuilder extends AbstractModifierRecipeBuilder<EmbossmentModifierRecipeBuilder> {
    private List<SizedIngredient> embossItem;
    private List<SizedIngredient> inputs;

    protected EmbossmentModifierRecipeBuilder(ModifierId result, List<SizedIngredient> embossItem, List<SizedIngredient> inputs) {
        super(result);
        this.embossItem = embossItem;
        this.inputs = inputs;
    }

    public static EmbossmentModifierRecipeBuilder modifier(ModifierId result) {
        return new EmbossmentModifierRecipeBuilder(result, new ArrayList<>(), new ArrayList<>());
    }

    public EmbossmentModifierRecipeBuilder addInput(SizedIngredient input) {
        this.inputs.add(input);
        return this;
    }

    public EmbossmentModifierRecipeBuilder addInputs(SizedIngredient... inputs) {
        this.inputs.addAll(List.of(inputs));
        return this;
    }

    public EmbossmentModifierRecipeBuilder setInputs(SizedIngredient... inputs) {
        this.inputs = List.of(inputs);
        return this;
    }

    public EmbossmentModifierRecipeBuilder setInputs(List<SizedIngredient> inputs) {
        this.inputs = inputs;
        return this;
    }

    public EmbossmentModifierRecipeBuilder addEmbossItem(SizedIngredient input) {
        this.embossItem.add(input);
        return this;
    }

    public EmbossmentModifierRecipeBuilder addEmbossItems(SizedIngredient... inputs) {
        this.embossItem.addAll(List.of(inputs));
        return this;
    }

    public EmbossmentModifierRecipeBuilder setEmbossItems(SizedIngredient... inputs) {
        this.embossItem = List.of(inputs);
        return this;
    }

    public EmbossmentModifierRecipeBuilder setEmbossItems(List<SizedIngredient> inputs) {
        this.embossItem = inputs;
        return this;
    }

    @Override
    public void save(@NotNull Consumer<FinishedRecipe> consumer, @NotNull ResourceLocation id) {
        if (this.inputs.isEmpty() || this.embossItem.isEmpty()) {
            throw new IllegalStateException("Must have at least 1 input");
        } else {
            ResourceLocation advancementId = this.buildOptionalAdvancement(id, "modifiers");
            consumer.accept(new LoadableFinishedRecipe<>(
                    new EmbossmentModifierRecipe(id, inputs, embossItem, tools, maxToolSize, result, ModifierEntry.VALID_LEVEL.range(minLevel, maxLevel), slots),
                    EmbossmentModifierRecipe.LOADER,
                    advancementId));
        }

    }
}
