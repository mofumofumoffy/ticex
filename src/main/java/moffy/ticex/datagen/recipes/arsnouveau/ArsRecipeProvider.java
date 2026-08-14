package moffy.ticex.datagen.recipes.arsnouveau;

import moffy.ticex.TicEX;
import moffy.ticex.datagen.recipes.ITicEXRecipeHelper;
import moffy.ticex.datagen.recipes.ticex.builder.SingleEmbossmentModifierRecipeBuilder;
import moffy.ticex.registry.TicEXItems;
import moffy.ticex.registry.TicEXModifiers;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;

import java.util.function.Consumer;

public class ArsRecipeProvider implements ITicEXRecipeHelper {
    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> topConsumer = withCondition(
                pWriter,
                modsAvailable(TicEX.getResource("ars_compat"))
        );

        SingleEmbossmentModifierRecipeBuilder.modifier(TicEXModifiers.REACTIVE_MODIFIER.getId(), Ingredient.of(TicEXItems.REACTIVE_CORE.get()))
                .setTools(TinkerTags.Items.DURABILITY)
                .save(topConsumer, prefix(TicEXModifiers.REACTIVE_MODIFIER, slotlessFolder));

        ModifierRecipeBuilder.modifier(TicEXModifiers.ALTERATIVE_MODIFIER)
                .addInput(TicEXItems.ALTERATIVE_CORE.get())
                .setTools(TinkerTags.Items.WORN_ARMOR)
                .save(topConsumer, prefix(TicEXModifiers.ALTERATIVE_MODIFIER, defenseFolder));
    }
}
