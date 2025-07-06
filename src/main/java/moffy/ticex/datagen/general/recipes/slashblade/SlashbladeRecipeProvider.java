package moffy.ticex.datagen.general.recipes.slashblade;

import mods.flammpfeil.slashblade.init.SBItems;
import moffy.ticex.TicEX;
import moffy.ticex.datagen.general.recipes.ITicEXRecipeHelper;
import moffy.ticex.datagen.general.recipes.TicEXRecipeProvider;
import moffy.ticex.datagen.general.recipes.ticex.IEmbossmentToolRecipeHelper;
import moffy.ticex.datagen.general.recipes.ticex.embossment.EmbossmentBuildingRecipeBuilder;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.library.data.recipe.ICastCreationHelper;
import slimeknights.tconstruct.library.data.recipe.IToolRecipeHelper;
import slimeknights.tconstruct.library.recipe.casting.material.CompositeCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.partbuilder.PartRecipeBuilder;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class SlashbladeRecipeProvider implements ITicEXRecipeHelper, ICastCreationHelper, IToolRecipeHelper, IEmbossmentToolRecipeHelper {
    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> topConsumer = withCondition(
                pWriter,
                modsAvailable(new ResourceLocation(TicEX.MODID, "slashblade_compat"))
        );

       if(TicEXRegistry.KONPAKU_CORE != null) {
           ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TicEXRegistry.KONPAKU_CORE.get())
                   .define('A', SBItems.proudsoul_tiny)
                   .define('C', TicEXRegistry.RECONSTRUCTION_CORE.get())
                   .pattern(" A ")
                   .pattern("ACA")
                   .pattern(" A ")
                   .unlockedBy("has_item", TicEXRecipeProvider.has(TicEXRegistry.RECONSTRUCTION_CORE.get()))
                   .save(topConsumer, prefix(TicEXRegistry.KONPAKU_CORE, coresFolder));
       }

        if(TicEXRegistry.REFORGED_SLASHBLADE != null) {
            EmbossmentBuildingRecipeBuilder.buildingRecipe((IModifiable) TicEXRegistry.REFORGED_SLASHBLADE.asItem())
                    .outputSize(1)
                    .save(topConsumer, prefix(TicEXRegistry.REFORGED_SLASHBLADE, buildingFolder));
        }

        if(TicEXRegistry.SLASHBLADE_BLADE != null) {
            sbCasting(topConsumer, TicEXRegistry.SLASHBLADE_BLADE, TicEXRegistry.SLASHBLADE_BLADE_CAST, "slashblade_blade", 4, 2, 4);
        }

        if(TicEXRegistry.SLASHBLADE_SAYA != null) {
            sbCasting(topConsumer, TicEXRegistry.SLASHBLADE_SAYA, TicEXRegistry.SLASHBLADE_SAYA_CAST, "slashblade_saya", 6, 2, 6);
        }

        if(TicEXRegistry.CATALYST_SLASHBLADE != null) {
            embossmentCasting(topConsumer, TicEXRegistry.CATALYST_SLASHBLADE.get(), 1, SBItems.slashblade.asItem(), true,
                    prefix(TicEXRegistry.CATALYST_SLASHBLADE, toolCastingFolder));
        }
    }

    public void sbCasting(Consumer<FinishedRecipe> topConsumer, ItemObject<ToolPartItem> itemObj, CastItemObject castItem, String pattern, int cost, int partCost, int compositeCost) {
        PartRecipeBuilder.partRecipe(itemObj.get())
                .setCost(cost)
                .setPattern(new ResourceLocation(TicEX.MODID, pattern))
                .setPatternItem(Ingredient.fromValues(Stream.of(
                        new Ingredient.TagValue(TinkerTags.Items.DEFAULT_PATTERNS),
                        new Ingredient.ItemValue(new ItemStack(castItem))
                )))
                .save(topConsumer, prefix(itemObj, partsBuilderFolder));

        partCasting(topConsumer, itemObj.get(), castItem, partCost, compositeCost, partsFolder);
        castCreation(topConsumer, Ingredient.of(itemObj.asItem()), castItem, smelteryCastsFolder, itemObj.getId().getPath());
    }

    public void partCasting(@NotNull Consumer<FinishedRecipe> consumer, @NotNull IMaterialItem part, CastItemObject cast, int cost, int compositeCost, @NotNull String partFolder) {
        String name = this.id(part).getPath();
        String partCasting = partFolder + "casting/";
        MaterialCastingRecipeBuilder.tableRecipe(part)
                .setItemCost(cost)
                .setCast(cast.getMultiUseTag(), false)
                .save(consumer, this.location(partCasting + name + "_gold_cast"));
        MaterialCastingRecipeBuilder.tableRecipe(part)
                .setItemCost(cost)
                .setCast(cast.getSingleUseTag(), true)
                .save(consumer, this.location(partCasting + name + "_sand_cast"));
        CompositeCastingRecipeBuilder.table(part, compositeCost).save(consumer, this.location(partCasting + name + "_composite"));
    }
}
