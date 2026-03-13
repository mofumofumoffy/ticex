package moffy.ticex.datagen.general.recipes.cc;

import dan200.computercraft.shared.ModRegistry;
import moffy.ticex.TicEX;
import moffy.ticex.datagen.general.recipes.ITicEXRecipeHelper;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.recipes.FinishedRecipe;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;

import java.util.function.Consumer;

public class CCRecipeProvider implements ITicEXRecipeHelper {

    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> topConsumer = withCondition(
            pWriter,
            modsAvailable(TicEX.getResource("computercraft_compat"))
        );

        if (TicEXRegistry.MODEM_MODIFIER != null) {
            ModifierRecipeBuilder.modifier(TicEXRegistry.MODEM_MODIFIER)
                .setTools(TinkerTags.Items.CHESTPLATES)
                .addInput(ModRegistry.Items.WIRELESS_MODEM_ADVANCED.get())
                .setSlots(SlotType.UPGRADE, 1)
                .setMaxLevel(1)
                .checkTraitLevel()
                .saveSalvage(topConsumer, prefix(TicEXRegistry.MODEM_MODIFIER.getId(), upgradeSalvage))
                .save(topConsumer, prefix(TicEXRegistry.MODEM_MODIFIER.getId(), upgradeFolder));
        }
    }
}
