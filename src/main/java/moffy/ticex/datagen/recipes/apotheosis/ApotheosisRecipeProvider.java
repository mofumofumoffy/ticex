package moffy.ticex.datagen.recipes.apotheosis;

import moffy.ticex.TicEX;
import moffy.ticex.datagen.recipes.ITicEXRecipeHelper;
import moffy.ticex.datagen.recipes.ticex.builder.EmbossmentModifierRecipeBuilder;
import moffy.ticex.registry.TicEXItems;
import moffy.ticex.registry.TicEXModifiers;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.CraftingHelper;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.common.TinkerTags;

import java.util.function.Consumer;

public class ApotheosisRecipeProvider implements ITicEXRecipeHelper {
    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        CraftingHelper.register(new FixedModuleCondition.Serializer());

        Consumer<FinishedRecipe> topConsumer = withCondition(
                pWriter,
                modsAvailable(TicEX.getResource("apotheosis_compat"))
        );

        if(TicEXModifiers.OVERLOAD_MODIFIER != null) {
            EmbossmentModifierRecipeBuilder.modifier(TicEXModifiers.OVERLOAD_MODIFIER.getId())
                    .addInput(SizedIngredient.fromItems(TicEXItems.OVERLOAD_CORE.get()))
                    .addEmbossItem(SizedIngredient.fromItems(Items.ENCHANTED_BOOK))
                    .setTools(TinkerTags.Items.MODIFIABLE)
                    .save(topConsumer, prefix(TicEXModifiers.OVERLOAD_MODIFIER, upgradeFolder));
        }

        if(TicEXModifiers.OVERRIDE_MODIFIER != null) {
            EmbossmentModifierRecipeBuilder.modifier(TicEXModifiers.OVERRIDE_MODIFIER.getId())
                    .addInput(SizedIngredient.fromItems(TicEXItems.OVERRIDE_CORE.get()))
                    .addEmbossItem(SizedIngredient.fromItems(Items.ENCHANTED_BOOK))
                    .setTools(TinkerTags.Items.MODIFIABLE)
                    .save(topConsumer, prefix(TicEXModifiers.OVERRIDE_MODIFIER, upgradeFolder));
        }

        Consumer<FinishedRecipe> enchantmentConsumer = withCondition(
                topConsumer,
                new FixedModuleCondition("enchantment")
        );

        if(TicEXItems.OVERLOAD_CORE != null) {
            ApotheosisEnchantingRecipeBuilder.builder(TicEXItems.OVERLOAD_CORE.get())
                    .setInput(TicEXItems.RECONSTRUCTION_CORE.get())
                    .setEterna(50).setQuanta(100).setArcana(100)
                    .setEternaMax(50).setQuantaMax(100).setArcanaMax(100)
                    .save(enchantmentConsumer, prefix(TicEXItems.OVERLOAD_CORE, coresFolder));
        }

        if(TicEXItems.OVERRIDE_CORE != null) {
            ApotheosisEnchantingRecipeBuilder.builder(TicEXItems.OVERRIDE_CORE.get())
                    .setInput(TicEXItems.OVERLOAD_CORE.get())
                    .setEterna(50).setQuanta(0).setArcana(23)
                    .setEternaMax(50).setQuantaMax(4).setArcanaMax(26)
                    .save(enchantmentConsumer, prefix(TicEXItems.OVERRIDE_CORE, coresFolder));
        }
    }
}
