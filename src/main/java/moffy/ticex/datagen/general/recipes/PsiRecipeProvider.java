package moffy.ticex.datagen.general.recipes;

import java.util.function.Consumer;
import moffy.ticex.TicEX;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;

public class PsiRecipeProvider implements ITicEXRecipeHelper {

    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> topConsumer = withCondition(
            pWriter,
            modsAvailable(new ResourceLocation(TicEX.MODID, "psi_compat"))
        );
    }
}
