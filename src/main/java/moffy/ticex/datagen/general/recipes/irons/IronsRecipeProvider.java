package moffy.ticex.datagen.general.recipes.irons;

import io.redspace.ironsspellbooks.compat.Curios;
import moffy.ticex.TicEX;
import moffy.ticex.datagen.general.recipes.ITicEXRecipeHelper;
import moffy.ticex.datagen.general.recipes.ticex.embossment.EmbossmentBuildingRecipeBuilder;
import moffy.ticex.datagen.general.recipes.ticex.embossment.EmbossmentCastingRecipeBuilder;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.DifferenceIngredient;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.function.Consumer;

public class IronsRecipeProvider implements ITicEXRecipeHelper {
    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> topConsumer = withCondition(
                pWriter,
                modsAvailable(new ResourceLocation(TicEX.MODID, "irons_spellbooks_compat"))
        );

        if(TicEXRegistry.OVERCASTING_MODIFIER != null) {
            ModifierRecipeBuilder.modifier(TicEXRegistry.OVERCASTING_MODIFIER.getId())
                    .allowCrystal()
                    .addInput(SizedIngredient.fromItems(TicEXRegistry.CATALYST_IRONS_SPELLBOOK.get()))
                    .setTools(DifferenceIngredient.of(
                            Ingredient.of(TinkerTags.Items.DURABILITY),
                            Ingredient.of(TicEXRegistry.CATALYST_IRONS_SPELLBOOK)
                    ))
                    .setSlots(SlotType.UPGRADE, 1)
                    .save(topConsumer, prefix(TicEXRegistry.OVERCASTING_MODIFIER, upgradeFolder));
        }

        if(TicEXRegistry.REVIVAL_SPELLBOOK_IRONS != null) {
            EmbossmentBuildingRecipeBuilder.buildingRecipe((IModifiable) TicEXRegistry.REVIVAL_SPELLBOOK_IRONS.asItem())
                    .outputSize(1)
                    .save(topConsumer, prefix(TicEXRegistry.REVIVAL_SPELLBOOK_IRONS, buildingFolder));
        }

        if(TicEXRegistry.CATALYST_IRONS_SPELLBOOK != null) {
            TagKey<Item> spellbookTags = TagKey.create(Registries.ITEM, new ResourceLocation(CuriosApi.MODID, Curios.SPELLBOOK_SLOT));
            EmbossmentCastingRecipeBuilder.castingRecipe(TicEXRegistry.CATALYST_IRONS_SPELLBOOK.get())
                    .setItemCost(1)
                    .setCast(DifferenceIngredient.of(Ingredient.of(spellbookTags), Ingredient.of(TinkerTags.Items.MODIFIABLE)), true)
                    .save(topConsumer, prefix(TicEXRegistry.CATALYST_IRONS_SPELLBOOK, partsCastingFolder));
        }
    }
}
