package moffy.ticex.datagen.general.recipes.curios;

import java.util.function.Consumer;

import moffy.ticex.TicEX;
import moffy.ticex.datagen.general.recipes.ITicEXRecipeHelper;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;

public class CuriosRecipeProvider implements ITicEXRecipeHelper{
    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> topConsumer = withCondition(
            pWriter,
            modsAvailable(new ResourceLocation(TicEX.MODID, "avaritia_compat"))
        );

        if(TicEXRegistry.RESONANCE_GAUNTLET != null){
            MaterialCastingRecipeBuilder.tableRecipe((ModifiableItem)TicEXRegistry.RESONANCE_GAUNTLET.get())
                                .setCast(Ingredient.of(TicEXRegistry.EXHAUSTED_MITTEN.get()), true)
                                .setItemCost(8)
                                .save(topConsumer, location(buildingFolder+"resonance_gauntlet"));
        }

        if (TicEXRegistry.INCOMPARABLE_MODIFIER != null) {
            ModifierRecipeBuilder.modifier(TicEXRegistry.INCOMPARABLE_MODIFIER)
                .setTools(Ingredient.of(TicEXRegistry.RESONANCE_GAUNTLET.get()))
                .addInput(TicEXRegistry.INCOMPARABLE_CORE.get())
                .setSlots(SlotType.ABILITY, 1)
                .setMaxLevel(2)
                .checkTraitLevel()
                .saveSalvage(topConsumer, prefix(TicEXRegistry.INCOMPARABLE_MODIFIER.getId(), abilitySalvage))
                .save(topConsumer, prefix(TicEXRegistry.INCOMPARABLE_MODIFIER.getId(), abilityFolder));
        }
    }

    @Override
    public String getModId() {
        return TicEX.MODID;
    }
}
