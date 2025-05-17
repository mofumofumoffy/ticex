package moffy.ticex.datagen.general.recipes;

import java.util.function.Consumer;

import dan200.computercraft.shared.ModRegistry;
import moffy.ticex.TicEX;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;

public class CCRecipeProvider implements ITicEXRecipeHelper{

    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> topConsumer = withCondition(pWriter, modsAvailable(new ResourceLocation(TicEX.MODID, "computercraft_compat")));
        
        if(TicEXRegistry.MODEM_MODIFIER != null){
            ModifierRecipeBuilder.modifier(TicEXRegistry.MODEM_MODIFIER)
                .setTools(TinkerTags.Items.CHESTPLATES)
                .addInput(ModRegistry.Items.WIRELESS_MODEM_ADVANCED.get())
                .setSlots(SlotType.UPGRADE, 1)
                .setMaxLevel(1).checkTraitLevel()
                .saveSalvage(topConsumer, prefix(TicEXRegistry.MODEM_MODIFIER.getId(), upgradeSalvage))
                .save(topConsumer, prefix(TicEXRegistry.MODEM_MODIFIER.getId(), upgradeFolder));
        }
    }
    
}
