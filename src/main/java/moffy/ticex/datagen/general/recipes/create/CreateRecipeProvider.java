package moffy.ticex.datagen.general.recipes.create;

import com.simibubi.create.api.data.recipe.MechanicalCraftingRecipeBuilder;
import moffy.ticex.TicEX;
import moffy.ticex.datagen.general.recipes.ITicEXRecipeHelper;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;

import java.util.function.Consumer;

public class CreateRecipeProvider implements ITicEXRecipeHelper {
    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> topConsumer = withCondition(
                pWriter,
                modsAvailable(TicEX.getResource("create_compat"))
        );

        if (TicEXRegistry.CARDBOARD_MODIFIER != null) {
            ModifierRecipeBuilder.modifier(TicEXRegistry.CARDBOARD_MODIFIER)
                    .allowCrystal()
                    .addInput(TicEXRegistry.CARDBOARD_CORE.get())
                    .setTools(TinkerTags.Items.WORN_ARMOR)
                    .setSlots(SlotType.UPGRADE, 1)
                    .save(topConsumer, prefix(TicEXRegistry.CARDBOARD_MODIFIER, upgradeFolder));
        }

        if (TicEXRegistry.CARDBOARD_CORE != null) {
            MechanicalCraftingRecipeBuilder.shapedRecipe(TicEXRegistry.CARDBOARD_CORE.get())
                    .key('C', item(ResourceLocation.fromNamespaceAndPath("create", "cardboard")))
                    .key('R', TicEXRegistry.RECONSTRUCTION_CORE.get())
                    .patternLine("CCCCC")
                    .patternLine("CCCCC")
                    .patternLine("CCRCC")
                    .patternLine("CCCCC")
                    .patternLine("CCCCC")
                    .build(topConsumer, prefix(TicEXRegistry.CARDBOARD_CORE, coresFolder));
        }
    }
}
