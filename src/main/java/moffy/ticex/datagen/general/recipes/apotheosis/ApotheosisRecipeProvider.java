package moffy.ticex.datagen.general.recipes.apotheosis;

import moffy.ticex.TicEX;
import moffy.ticex.datagen.general.recipes.ITicEXRecipeHelper;
import moffy.ticex.datagen.general.recipes.ticex.embossment.EmbossmentModifierRecipeBuilder;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.CraftingHelper;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.SlotType;

import java.util.function.Consumer;

public class ApotheosisRecipeProvider implements ITicEXRecipeHelper {
    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        CraftingHelper.register(new FixedModuleCondition.Serializer());

        Consumer<FinishedRecipe> topConsumer = withCondition(
                pWriter,
                modsAvailable(new ResourceLocation(TicEX.MODID, "apotheosis_compat"))
        );

        if(TicEXRegistry.OVERLOAD_MODIFIER != null) {
            EmbossmentModifierRecipeBuilder.modifier(TicEXRegistry.OVERLOAD_MODIFIER.getId())
                    .addInput(SizedIngredient.fromItems(TicEXRegistry.OVERLOAD_CORE.get()))
                    .addEmbossItem(SizedIngredient.fromItems(Items.ENCHANTED_BOOK))
                    .setTools(TinkerTags.Items.MODIFIABLE)
                    .save(topConsumer, prefix(TicEXRegistry.OVERLOAD_MODIFIER, upgradeFolder));
        }

        if(TicEXRegistry.OVERRIDE_MODIFIER != null) {
            EmbossmentModifierRecipeBuilder.modifier(TicEXRegistry.OVERRIDE_MODIFIER.getId())
                    .addInput(SizedIngredient.fromItems(TicEXRegistry.OVERRIDE_CORE.get()))
                    .addEmbossItem(SizedIngredient.fromItems(Items.ENCHANTED_BOOK))
                    .setTools(TinkerTags.Items.MODIFIABLE)
                    .save(topConsumer, prefix(TicEXRegistry.OVERRIDE_MODIFIER, upgradeFolder));
        }

        Consumer<FinishedRecipe> enchantmentConsumer = withCondition(
                topConsumer,
                new FixedModuleCondition("enchantment")
        );

        if(TicEXRegistry.OVERLOAD_CORE != null) {
            ApotheosisEnchantingRecipeBuilder.builder(TicEXRegistry.OVERLOAD_CORE.get())
                    .setInput(TicEXRegistry.RECONSTRUCTION_CORE.get())
                    .setEterna(50).setQuanta(100).setArcana(100)
                    .setEternaMax(50).setQuantaMax(100).setArcanaMax(100)
                    .save(enchantmentConsumer, prefix(TicEXRegistry.OVERLOAD_CORE, coresFolder));
        }

        if(TicEXRegistry.OVERRIDE_CORE != null) {
            ApotheosisEnchantingRecipeBuilder.builder(TicEXRegistry.OVERRIDE_CORE.get())
                    .setInput(TicEXRegistry.OVERLOAD_CORE.get())
                    .setEterna(50).setQuanta(0).setArcana(23)
                    .setEternaMax(50).setQuantaMax(4).setArcanaMax(26)
                    .save(enchantmentConsumer, prefix(TicEXRegistry.OVERRIDE_CORE, coresFolder));
        }
    }
}
