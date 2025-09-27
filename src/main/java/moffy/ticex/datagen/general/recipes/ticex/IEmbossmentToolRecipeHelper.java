package moffy.ticex.datagen.general.recipes.ticex;

import moffy.ticex.datagen.general.recipes.ticex.embossment.EmbossmentBuildingRecipeBuilder;
import moffy.ticex.datagen.general.recipes.ticex.embossment.EmbossmentCastingRecipeBuilder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.tconstruct.library.data.recipe.IToolRecipeHelper;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IEmbossmentToolRecipeHelper extends IToolRecipeHelper {
    default void embossmentBuilding(Consumer<FinishedRecipe> consumer, IModifiable tool, String folder) {
        EmbossmentBuildingRecipeBuilder.buildingRecipe(tool)
                .save(consumer, this.prefix(this.id(tool), folder));
    }

    default void embossmentBuilding(Consumer<FinishedRecipe> consumer, IModifiable tool, String folder, ResourceLocation layoutSlot) {
        EmbossmentBuildingRecipeBuilder.buildingRecipe(tool)
                .layoutSlot(layoutSlot)
                .save(consumer, this.prefix(this.id(tool), folder));
    }

    default void embossmentBuilding(Consumer<FinishedRecipe> consumer, Supplier<? extends IModifiable> tool, String folder) {
        this.embossmentBuilding(consumer, tool.get(), folder);
    }

    default void embossmentCasting(Consumer<FinishedRecipe> topConsumer, IMaterialItem materialItem, int cost, Ingredient castIngredient, boolean consumed, ResourceLocation rl) {
        EmbossmentCastingRecipeBuilder.castingRecipe(materialItem)
                .setItemCost(cost)
                .setCast(castIngredient, consumed)
                .save(topConsumer, rl);
    }

    default void embossmentCasting(Consumer<FinishedRecipe> topConsumer, IMaterialItem materialItem, int cost, Item castItem, boolean consumed, ResourceLocation rl) {
        EmbossmentCastingRecipeBuilder.castingRecipe(materialItem)
                .setItemCost(cost)
                .setCast(castItem, consumed)
                .save(topConsumer, rl);
    }

    default void embossmentCasting(Consumer<FinishedRecipe> topConsumer, IMaterialItem materialItem, int cost, TagKey<Item> castItemTag, boolean consumed, ResourceLocation rl) {
        EmbossmentCastingRecipeBuilder.castingRecipe(materialItem)
                .setItemCost(cost)
                .setCast(castItemTag, consumed)
                .save(topConsumer, rl);
    }
}
