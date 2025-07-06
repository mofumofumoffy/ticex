package moffy.ticex.datagen.general.recipes.tinkersthings;

import moffy.ticex.TicEX;
import moffy.ticex.datagen.general.recipes.ITicEXRecipeHelper;
import moffy.ticex.datagen.general.recipes.TicEXRecipeProvider;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.Consumer;

public class ThingsRecipeProvider implements ITicEXRecipeHelper {
    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> topConsumer = withCondition(
                pWriter,
                modsAvailable(new ResourceLocation(TicEX.MODID, "things_compat"))
        );

        Item hematile = item(new ResourceLocation("tinkers_things", "hematite"));

        if(hematile != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TicEXRegistry.LAMELLAR_CORE.get())
                    .define('H', hematile)
                    .define('C', TicEXRegistry.RECONSTRUCTION_CORE.get())
                    .pattern("HHH")
                    .pattern("HCH")
                    .pattern("HHH")
                    .unlockedBy("has_item", TicEXRecipeProvider.has(TicEXRegistry.RECONSTRUCTION_CORE.get()))
                    .save(topConsumer, prefix(TicEXRegistry.LAMELLAR_CORE, coresFolder));
        }
    }
}
