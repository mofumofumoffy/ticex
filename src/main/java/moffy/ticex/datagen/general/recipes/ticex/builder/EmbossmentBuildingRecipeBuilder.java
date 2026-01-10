package moffy.ticex.datagen.general.recipes.ticex.builder;

import moffy.ticex.lib.recipe.EmbossmentBuildingRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.tools.item.IModifiable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EmbossmentBuildingRecipeBuilder extends AbstractRecipeBuilder<EmbossmentBuildingRecipeBuilder> {
    private final IModifiable output;
    private int outputSize = 1;
    @Nullable
    private ResourceLocation layoutSlot = null;
    private final List<Ingredient> extraRequirements = new ArrayList<>();

    public EmbossmentBuildingRecipeBuilder addExtraRequirement(Ingredient ingredient) {
        this.extraRequirements.add(ingredient);
        return this;
    }

    @SuppressWarnings("deprecation")
    public void save(@NotNull Consumer<FinishedRecipe> consumerIn) {
        this.save(consumerIn, BuiltInRegistries.ITEM.getKey(this.output.asItem()));
    }

    public void save(Consumer<FinishedRecipe> consumerIn, @NotNull ResourceLocation id) {
        ResourceLocation advancementId = this.buildOptionalAdvancement(id, "parts");
        consumerIn.accept(new LoadableFinishedRecipe<>(
                new EmbossmentBuildingRecipe(id, this.group, this.output, this.outputSize, this.layoutSlot, this.extraRequirements),
                EmbossmentBuildingRecipe.LOADER, advancementId));
    }

    private EmbossmentBuildingRecipeBuilder(IModifiable output) {
        this.output = output;
    }

    public static EmbossmentBuildingRecipeBuilder buildingRecipe(IModifiable output) {
        return new EmbossmentBuildingRecipeBuilder(output);
    }

    public EmbossmentBuildingRecipeBuilder outputSize(int outputSize) {
        this.outputSize = outputSize;
        return this;
    }

    public EmbossmentBuildingRecipeBuilder layoutSlot(@Nullable ResourceLocation layoutSlot) {
        this.layoutSlot = layoutSlot;
        return this;
    }
}
