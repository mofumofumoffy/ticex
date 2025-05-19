package moffy.ticex.datagen.general.recipes;

import java.util.function.Consumer;

import moffy.ticex.TicEX;
import moffy.ticex.lib.TicEXMaterials;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import slimeknights.tconstruct.library.data.recipe.IMaterialRecipeHelper;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.SlimeType;

public class CommonRecipeProvider implements ITicEXRecipeHelper, IMaterialRecipeHelper{
    public void buildRecipes(Consumer<FinishedRecipe> pWriter){

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
            .save(pWriter,"cores/reconstruction_core");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TicEXRegistry.FLICKERING_RECONSTRUCTION_CORE.get())
            .define('c', TicEXRegistry.RECONSTRUCTION_CORE.get())
            .define('s', Items.NETHER_STAR)
            .pattern("ccc")
            .pattern("csc")
            .pattern("ccc")
            .unlockedBy("has_item", TicEXRecipeProvider.has(TicEXRegistry.RECONSTRUCTION_CORE.get()))
            .save(pWriter,"cores/flickering_reconstruction_core");
    }

    @Override
    public String getModId() {
        return TicEX.MODID;
    }
}
