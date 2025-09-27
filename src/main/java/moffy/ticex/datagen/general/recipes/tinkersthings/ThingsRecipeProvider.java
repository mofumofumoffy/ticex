package moffy.ticex.datagen.general.recipes.tinkersthings;

import moffy.ticex.TicEX;
import moffy.ticex.datagen.general.recipes.ITicEXRecipeHelper;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class ThingsRecipeProvider implements ITicEXRecipeHelper {
    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> topConsumer = withCondition(
                pWriter,
                modsAvailable(new ResourceLocation(TicEX.MODID, "things_compat"))
        );
    }
}
