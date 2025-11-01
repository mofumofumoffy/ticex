package moffy.ticex.datagen.general.recipes.tacz;

/*
 * This file is part of the TicEXTaczModule.
 *
 * Licensed under the GNU General Public License v3.0.
 * See the LICENSES/GPL-3.0.md file for details.
 * 2025 Moffy
 */

import com.tacz.guns.init.ModItems;
import moffy.ticex.TicEX;
import moffy.ticex.datagen.general.recipes.ITicEXRecipeHelper;
import moffy.ticex.datagen.general.recipes.ticex.IEmbossmentToolRecipeHelper;
import moffy.ticex.datagen.general.recipes.ticex.builder.EmbossmentBuildingRecipeBuilder;
import moffy.ticex.datagen.general.recipes.ticex.builder.EmbossmentCastingRecipeBuilder;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.tools.item.IModifiable;

import java.util.function.Consumer;

public class TaczRecipeProvider implements ITicEXRecipeHelper, IEmbossmentToolRecipeHelper {
    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> topConsumer = withCondition(
                pWriter,
                modsAvailable(new ResourceLocation(TicEX.MODID, "tacz_compat"))
        );

        if(TicEXRegistry.BLITZ_GUN != null) {
            EmbossmentBuildingRecipeBuilder.buildingRecipe((IModifiable) TicEXRegistry.BLITZ_GUN.asItem())
                    .outputSize(1)
                    .save(topConsumer, prefix(TicEXRegistry.BLITZ_GUN, buildingFolder));
        }

        if(TicEXRegistry.CATALYST_KINETIC_GUN != null) {
            EmbossmentCastingRecipeBuilder.castingRecipe(TicEXRegistry.CATALYST_KINETIC_GUN.get())
                    .setItemCost(1)
                    .setCast(ModItems.MODERN_KINETIC_GUN.get(), true)
                    .save(topConsumer, prefix(TicEXRegistry.CATALYST_KINETIC_GUN, partsCastingFolder));
        }
    }
}
