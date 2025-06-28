package moffy.ticex.datagen.general.recipes;

import moffy.ticex.TicEX;
import moffy.ticex.lib.TicEXMaterials;
import moffy.ticex.lib.TicEXTags;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.data.recipe.IMaterialRecipeHelper;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.SlimeType;

import java.util.function.Consumer;

public class CommonRecipeProvider implements ITicEXRecipeHelper, IMaterialRecipeHelper {

    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        metalMaterialRecipe(pWriter, TicEXMaterials.ETHERIC, materialFolder, "etheric", false);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TicEXRegistry.RECONSTRUCTION_CORE.get())
            .define('c', TinkerCommons.slimeball.get(SlimeType.SKY))
            .define('a', Items.AMETHYST_SHARD)
            .define('s', Items.SHULKER_SHELL)
            .define('p', Items.BLAZE_POWDER)
            .pattern("asa")
            .pattern("pcp")
            .pattern("asa")
            .unlockedBy("has_item", TicEXRecipeProvider.has(TinkerCommons.slimeball.get(SlimeType.SKY)))
            .save(pWriter, prefix(TicEXRegistry.RECONSTRUCTION_CORE, coresFolder));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TicEXRegistry.FLICKERING_RECONSTRUCTION_CORE.get())
            .define('c', TicEXRegistry.RECONSTRUCTION_CORE.get())
            .define('s', Items.NETHER_STAR)
            .pattern("ccc")
            .pattern("csc")
            .pattern("ccc")
            .unlockedBy("has_item", TicEXRecipeProvider.has(TicEXRegistry.RECONSTRUCTION_CORE.get()))
            .save(pWriter, prefix(TicEXRegistry.FLICKERING_RECONSTRUCTION_CORE, coresFolder));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TicEXRegistry.ETHERIC_BLOCK.get())
            .showNotification(true)
            .define('#', TicEXTags.Items.ETHERIC_INGOT)
            .define('*', TicEXRegistry.ETHERIC_INGOT.get())
            .pattern("###")
            .pattern("#*#")
            .pattern("###")
            .unlockedBy("has_item", TicEXRecipeProvider.has(TicEXRegistry.ETHERIC_INGOT.get()))
            .save(pWriter, prefix(itemsFolder + "etheric_block_from_ingot"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TicEXRegistry.ETHERIC_INGOT.get(), 9)
            .requires(TicEXRegistry.ETHERIC_BLOCK.get())
            .unlockedBy("has_item", TicEXRecipeProvider.has(TicEXRegistry.ETHERIC_BLOCK.get()))
            .save(pWriter, prefix(itemsFolder + "etheric_ingot_from_block"));
    }

    @Override
    public @NotNull String getModId() {
        return TicEX.MODID;
    }
}
