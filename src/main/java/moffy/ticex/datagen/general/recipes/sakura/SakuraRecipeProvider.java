package moffy.ticex.datagen.general.recipes.sakura;

import moffy.ticex.TicEX;
import moffy.ticex.datagen.general.recipes.ITicEXRecipeHelper;
import moffy.ticex.datagen.general.recipes.ticex.embossment.EmbossmentModifierRecipeBuilder;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.common.TinkerTags;

import java.util.function.Consumer;

public class SakuraRecipeProvider implements ITicEXRecipeHelper {
    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> topConsumer = withCondition(
                pWriter,
                modsAvailable(new ResourceLocation(TicEX.MODID, "sakura_compat"))
        );

        Item soulSakura = item(new ResourceLocation("sakuratinker", "soul_sakura"));
        if(soulSakura != null) {
            EmbossmentModifierRecipeBuilder.modifier(TicEXRegistry.FLOWERSTORM_MODIFIER.getId())
                    .addInput(SizedIngredient.fromItems(soulSakura))
                    .addEmbossItem(SizedIngredient.fromItems(TicEXRegistry.RECONSTRUCTION_CORE.get()))
                    .setTools(TinkerTags.Items.MODIFIABLE)
                    .save(topConsumer, prefix(TicEXRegistry.FLOWERSTORM_MODIFIER, slotlessFolder));
        }
    }
}
