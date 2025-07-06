package moffy.ticex.datagen.general.recipes.ticex.embossment;

import moffy.ticex.lib.recipe.EmbossmentCastingRecipe;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.recipe.helper.TypeAwareRecipeSerializer;
import slimeknights.tconstruct.library.recipe.casting.material.AbstractMaterialCastingRecipe;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class EmbossmentCastingRecipeBuilder extends AbstractRecipeBuilder<EmbossmentCastingRecipeBuilder> {
    @Nullable
    private final IMaterialItem result;
    private final TypeAwareRecipeSerializer<? extends AbstractMaterialCastingRecipe> recipeSerializer;
    private Ingredient cast;
    private int itemCost;
    private boolean consumed;
    private boolean switchSlots;

    public EmbossmentCastingRecipeBuilder setCast(TagKey<Item> tag, boolean consumed) {
        return this.setCast(Ingredient.of(tag), consumed);
    }

    public EmbossmentCastingRecipeBuilder setCast(ItemLike item, boolean consumed) {
        return this.setCast(Ingredient.of(item), consumed);
    }

    public EmbossmentCastingRecipeBuilder setCast(Ingredient cast, boolean consumed) {
        this.cast = cast;
        this.consumed = consumed;
        return this;
    }

    public EmbossmentCastingRecipeBuilder setSwitchSlots() {
        this.switchSlots = true;
        return this;
    }

    @SuppressWarnings("deprecation")
    public void save(@NotNull Consumer<FinishedRecipe> consumer) {
        if (this.result != null) {
            this.save(consumer, BuiltInRegistries.ITEM.getKey(this.result.asItem()));
        }
    }

    public void save(@NotNull Consumer<FinishedRecipe> consumer, @NotNull ResourceLocation id) {
        if (this.itemCost <= 0) {
            throw new IllegalStateException("Material casting recipes require a positive amount of fluid");
        } else if (this.result == null) {
            throw new IllegalStateException("Result must not be null");
        } else {
            ResourceLocation advancementId = this.buildOptionalAdvancement(id, "casting");
            consumer.accept(new LoadableFinishedRecipe<>(
                    new EmbossmentCastingRecipe(this.recipeSerializer, id, this.group, this.cast, this.itemCost, this.result, this.consumed, this.switchSlots),
                    EmbossmentCastingRecipe.LOADER, advancementId));

        }
    }

    private EmbossmentCastingRecipeBuilder(@Nullable IMaterialItem result, TypeAwareRecipeSerializer<? extends AbstractMaterialCastingRecipe> recipeSerializer) {
        this.cast = Ingredient.EMPTY;
        this.itemCost = 0;
        this.consumed = false;
        this.switchSlots = false;
        this.result = result;
        this.recipeSerializer = recipeSerializer;
    }

    public static EmbossmentCastingRecipeBuilder castingRecipe(@Nullable IMaterialItem result) {
        return new EmbossmentCastingRecipeBuilder(result, TicEXRegistry.CASTING_EMBOSSMENT_RECIPE_SERIALIZER.get());
    }

    public static EmbossmentCastingRecipeBuilder builder(@Nullable IMaterialItem result, TypeAwareRecipeSerializer<? extends AbstractMaterialCastingRecipe> recipeSerializer) {
        return new EmbossmentCastingRecipeBuilder(result, recipeSerializer);
    }

    public EmbossmentCastingRecipeBuilder setItemCost(int itemCost) {
        this.itemCost = itemCost;
        return this;
    }
}
