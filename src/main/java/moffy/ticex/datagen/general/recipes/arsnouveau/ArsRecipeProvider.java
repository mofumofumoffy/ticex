package moffy.ticex.datagen.general.recipes.arsnouveau;

import moffy.ticex.TicEX;
import moffy.ticex.datagen.general.recipes.ITicEXRecipeHelper;
import moffy.ticex.datagen.general.recipes.ticex.builder.SingleEmbossmentModifierRecipeBuilder;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;

import java.util.function.Consumer;

public class ArsRecipeProvider implements ITicEXRecipeHelper {
    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> topConsumer = withCondition(
                pWriter,
                modsAvailable(new ResourceLocation(TicEX.MODID, "ars_compat"))
        );

        SingleEmbossmentModifierRecipeBuilder.modifier(TicEXRegistry.REACTIVE_MODIFIER.getId(), Ingredient.of(TicEXRegistry.REACTIVE_CORE.get()))
                .setTools(TinkerTags.Items.DURABILITY)
                .save(topConsumer, prefix(TicEXRegistry.REACTIVE_MODIFIER, slotlessFolder));

        ModifierRecipeBuilder.modifier(TicEXRegistry.ALTERATIVE_MODIFIER)
                .addInput(TicEXRegistry.ALTERATIVE_CORE.get())
                .setTools(TinkerTags.Items.WORN_ARMOR)
                .save(topConsumer, prefix(TicEXRegistry.ALTERATIVE_MODIFIER, defenseFolder));
    }
}
