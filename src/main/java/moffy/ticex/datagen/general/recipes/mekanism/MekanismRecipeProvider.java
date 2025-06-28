package moffy.ticex.datagen.general.recipes.mekanism;

import mekanism.api.datagen.recipe.builder.PressurizedReactionRecipeBuilder;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.registries.MekanismGases;
import moffy.ticex.TicEX;
import moffy.ticex.datagen.general.recipes.ITicEXRecipeHelper;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;

import java.util.function.Consumer;

public class MekanismRecipeProvider implements ITicEXRecipeHelper {
    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> topConsumer = withCondition(
                pWriter,
                modsAvailable(new ResourceLocation(TicEX.MODID, "mekanism_compat"))
        );

        if(TicEXRegistry.RADIATION_SHELDING_CORE != null) {
            PressurizedReactionRecipeBuilder.reaction(
                            IngredientCreatorAccess.item().from(TicEXRegistry.RECONSTRUCTION_CORE.get()),
                            IngredientCreatorAccess.fluid().from(Fluids.WATER, 1000),
                            IngredientCreatorAccess.gas().from(MekanismGases.POLONIUM, 1000),
                            100,
                            new ItemStack(TicEXRegistry.RADIATION_SHELDING_CORE.get()),
                            MekanismGases.SPENT_NUCLEAR_WASTE.getStack(1000))
                    .build(topConsumer, prefix(TicEXRegistry.RADIATION_SHELDING_CORE, coresFolder));
        }
    }
}
