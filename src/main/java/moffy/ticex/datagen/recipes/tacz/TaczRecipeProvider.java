package moffy.ticex.datagen.recipes.tacz;

/*
 * This file is part of the TicEXTaczModule.
 *
 * Licensed under the GNU General Public License v3.0.
 * See the LICENSES/GPL-3.0.md file for details.
 * 2025 Moffy
 */

import com.tacz.guns.init.ModItems;
import moffy.ticex.TicEX;
import moffy.ticex.datagen.recipes.ITicEXRecipeHelper;
import moffy.ticex.datagen.recipes.ticex.IEmbossmentToolRecipeHelper;
import moffy.ticex.datagen.recipes.ticex.builder.EmbossmentBuildingRecipeBuilder;
import moffy.ticex.datagen.recipes.ticex.builder.EmbossmentCastingRecipeBuilder;
import moffy.ticex.registry.TicEXItems;
import net.minecraft.data.recipes.FinishedRecipe;
import slimeknights.tconstruct.library.tools.item.IModifiable;

import java.util.function.Consumer;

public class TaczRecipeProvider implements ITicEXRecipeHelper, IEmbossmentToolRecipeHelper {
    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> topConsumer = withCondition(
                pWriter,
                modsAvailable(TicEX.getResource("tacz_compat"))
        );

        if(TicEXItems.BLITZ_GUN != null) {
            EmbossmentBuildingRecipeBuilder.buildingRecipe((IModifiable) TicEXItems.BLITZ_GUN.asItem())
                    .outputSize(1)
                    .save(topConsumer, prefix(TicEXItems.BLITZ_GUN, buildingFolder));
        }

        if(TicEXItems.CATALYST_KINETIC_GUN != null) {
            EmbossmentCastingRecipeBuilder.castingRecipe(TicEXItems.CATALYST_KINETIC_GUN.get())
                    .setItemCost(1)
                    .setCast(ModItems.MODERN_KINETIC_GUN.get(), true)
                    .save(topConsumer, prefix(TicEXItems.CATALYST_KINETIC_GUN, partsCastingFolder));
        }
    }
}
