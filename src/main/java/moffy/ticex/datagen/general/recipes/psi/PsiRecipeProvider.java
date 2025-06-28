package moffy.ticex.datagen.general.recipes.psi;

import moffy.ticex.TicEX;
import moffy.ticex.datagen.general.recipes.ITicEXRecipeHelper;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import vazkii.psi.api.recipe.TrickRecipeBuilder;
import vazkii.psi.common.Psi;
import vazkii.psi.common.item.base.ModItems;
import vazkii.psi.common.lib.LibPieceNames;

import java.util.function.Consumer;

public class PsiRecipeProvider implements ITicEXRecipeHelper {

    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> topConsumer = withCondition(
            pWriter,
            modsAvailable(new ResourceLocation(TicEX.MODID, "psi_compat"))
        );

        TrickRecipeBuilder.of(TicEXRegistry.PSIONIZING_RADIATION_CORE.get())
                .cad(ModItems.cadAssemblyPsimetal)
                .input(TicEXRegistry.RECONSTRUCTION_CORE.get())
                .trick(Psi.location(LibPieceNames.TRICK_GREATER_INFUSION))
                .build(topConsumer, prefix(TicEXRegistry.PSIONIZING_RADIATION_CORE, coresFolder));
    }
}
