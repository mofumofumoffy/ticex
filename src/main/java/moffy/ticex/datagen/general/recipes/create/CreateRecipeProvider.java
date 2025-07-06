package moffy.ticex.datagen.general.recipes.create;

import com.simibubi.create.foundation.data.recipe.MechanicalCraftingRecipeBuilder;
import moffy.ticex.TicEX;
import moffy.ticex.datagen.general.recipes.ITicEXRecipeHelper;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class CreateRecipeProvider implements ITicEXRecipeHelper {
    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> topConsumer = withCondition(
                pWriter,
                modsAvailable(new ResourceLocation(TicEX.MODID, "create_compat"))
        );

        if (TicEXRegistry.CARDBOARD_CORE != null) {
            MechanicalCraftingRecipeBuilder.shapedRecipe(TicEXRegistry.CARDBOARD_CORE.get())
                    .key('C', item(new ResourceLocation("create", "cardboard")))
                    .key('R', TicEXRegistry.RECONSTRUCTION_CORE.get())
                    .patternLine("CCCCC")
                    .patternLine("CCCCC")
                    .patternLine("CCRCC")
                    .patternLine("CCCCC")
                    .patternLine("CCCCC")
                    .build(topConsumer, prefix(TicEXRegistry.CARDBOARD_CORE, coresFolder));
        }
    }
}
