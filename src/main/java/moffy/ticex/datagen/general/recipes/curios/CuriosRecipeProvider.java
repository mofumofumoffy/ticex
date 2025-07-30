package moffy.ticex.datagen.general.recipes.curios;

import java.util.function.Consumer;

import moffy.ticex.TicEX;
import moffy.ticex.datagen.general.recipes.ITicEXRecipeHelper;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingRecipeBuilder;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;

public class CuriosRecipeProvider implements ITicEXRecipeHelper{
    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> topConsumer = withCondition(
            pWriter,
            modsAvailable(new ResourceLocation(TicEX.MODID, "curios_compat"))
        );

        if(TicEXRegistry.RESONANCE_GAUNTLET != null){
            MaterialCastingRecipeBuilder.tableRecipe((ModifiableItem)TicEXRegistry.RESONANCE_GAUNTLET.get())
                                .setCast(Ingredient.of(TicEXRegistry.EXHAUSTED_GLOVE.get()), true)
                                .setItemCost(8)
                                .save(topConsumer, location(buildingFolder+"resonance_gauntlet"));
        }
    }

    @Override
    public String getModId() {
        return TicEX.MODID;
    }
}
