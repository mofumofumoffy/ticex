package moffy.ticex.datagen.general.recipes.slashblade;

import mods.flammpfeil.slashblade.init.SBItems;
import moffy.ticex.TicEX;
import moffy.ticex.datagen.general.recipes.ITicEXRecipeHelper;
import moffy.ticex.datagen.general.recipes.TicEXRecipeProvider;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class SlashbladeRecipeProvider  implements ITicEXRecipeHelper {
    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> topConsumer = withCondition(
                pWriter,
                modsAvailable(new ResourceLocation(TicEX.MODID, "slashblade_compat"))
        );

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TicEXRegistry.KONPAKU_CORE.get())
                .define('A', SBItems.proudsoul_tiny)
                .define('C', TicEXRegistry.RECONSTRUCTION_CORE.get())
                .pattern(" A ")
                .pattern("ACA")
                .pattern(" A ")
                .unlockedBy("has_item", TicEXRecipeProvider.has(TicEXRegistry.RECONSTRUCTION_CORE.get()))
                .save(topConsumer, prefix(TicEXRegistry.KONPAKU_CORE, coresFolder));
    }
}
