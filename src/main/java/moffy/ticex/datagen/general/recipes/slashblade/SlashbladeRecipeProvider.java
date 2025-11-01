package moffy.ticex.datagen.general.recipes.slashblade;

import mods.flammpfeil.slashblade.data.tag.SlashBladeItemTags;
import mods.flammpfeil.slashblade.init.SBItems;
import moffy.ticex.TicEX;
import moffy.ticex.datagen.general.recipes.ITicEXRecipeHelper;
import moffy.ticex.datagen.general.recipes.TicEXRecipeProvider;
import moffy.ticex.datagen.general.recipes.ticex.IEmbossmentToolRecipeHelper;
import moffy.ticex.datagen.general.recipes.ticex.builder.EmbossmentBuildingRecipeBuilder;
import moffy.ticex.datagen.general.recipes.ticex.builder.EmbossmentModifierRecipeBuilder;
import moffy.ticex.datagen.general.recipes.ticex.builder.SingleEmbossmentModifierRecipeBuilder;
import moffy.ticex.lib.TicEXTags;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.library.data.recipe.ICastCreationHelper;
import slimeknights.tconstruct.library.data.recipe.IToolRecipeHelper;
import slimeknights.tconstruct.library.recipe.casting.material.CompositeCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.partbuilder.PartRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;

import java.util.function.Consumer;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class SlashbladeRecipeProvider implements ITicEXRecipeHelper, ICastCreationHelper, IToolRecipeHelper, IEmbossmentToolRecipeHelper {
    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> topConsumer = withCondition(
                pWriter,
                modsAvailable(new ResourceLocation(TicEX.MODID, "slashblade_compat"))
        );

        // slotless

        if(TicEXRegistry.PROUD_MODIFIER != null) {
            SingleEmbossmentModifierRecipeBuilder.modifier(TicEXRegistry.PROUD_MODIFIER.getId(), Ingredient.of(SlashBladeItemTags.PROUD_SOULS))
                    .setTools(TicEXTags.Items.REFORGED_SLASHBLADE_TOOL)
                    .save(topConsumer, prefix(TicEXRegistry.PROUD_MODIFIER, slotlessFolder));
        }

        if(TicEXRegistry.KOSHIRAE_MODIFIER != null) {
            SingleEmbossmentModifierRecipeBuilder.modifier(TicEXRegistry.KOSHIRAE_MODIFIER.getId(), Ingredient.of(TicEXRegistry.CATALYST_SLASHBLADE))
                    .setTools(TicEXTags.Items.REFORGED_SLASHBLADE_TOOL)
                    .save(topConsumer, prefix(TicEXRegistry.KOSHIRAE_MODIFIER, slotlessFolder));
        }

        // upgrades

        if(TicEXRegistry.KONPAKU_MODIFIER != null) {
            EmbossmentModifierRecipeBuilder.modifier(TicEXRegistry.KONPAKU_MODIFIER.getId())
                    .addInput(SizedIngredient.fromItems(TicEXRegistry.KONPAKU_CORE.get()))
                    .addEmbossItem(SizedIngredient.fromItems(Items.ENCHANTED_BOOK))
                    .setTools(TicEXTags.Items.REFORGED_SLASHBLADE_TOOL)
                    .setSlots(SlotType.UPGRADE, 1)
                    .save(topConsumer, prefix(TicEXRegistry.KONPAKU_MODIFIER, upgradeFolder));
        }

        // shaped

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

       // other

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
            embossmentCasting(topConsumer, TicEXRegistry.CATALYST_SLASHBLADE.get(), 1, TicEXTags.Items.SLASHBLADE, true,
                    prefix(TicEXRegistry.CATALYST_SLASHBLADE, partsCastingFolder));
        }
    }

    public void sbCasting(Consumer<FinishedRecipe> topConsumer, ItemObject<ToolPartItem> itemObj, CastItemObject castItem, String pattern, int cost, int partCost, int compositeCost) {
        PartRecipeBuilder.partRecipe(itemObj.get())
                .setCost(cost)
                .setPattern(new ResourceLocation(TicEX.MODID, pattern))
                .setPatternItem(Ingredient.fromValues(Stream.of(
                        new Ingredient.TagValue(TinkerTags.Items.DEFAULT_PATTERNS)
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
