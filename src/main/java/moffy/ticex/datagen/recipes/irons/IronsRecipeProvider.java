package moffy.ticex.datagen.recipes.irons;

import io.redspace.ironsspellbooks.compat.Curios;
import moffy.ticex.TicEX;
import moffy.ticex.datagen.recipes.ITicEXRecipeHelper;
import moffy.ticex.datagen.recipes.ticex.builder.EmbossmentBuildingRecipeBuilder;
import moffy.ticex.datagen.recipes.ticex.builder.EmbossmentCastingRecipeBuilder;
import moffy.ticex.registry.TicEXItems;
import moffy.ticex.registry.TicEXModifiers;
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
                modsAvailable(TicEX.getResource("irons_spellbooks_compat"))
        );

        if(TicEXModifiers.OVERCASTING_MODIFIER != null) {
            ModifierRecipeBuilder.modifier(TicEXModifiers.OVERCASTING_MODIFIER.getId())
                    .allowCrystal()
                    .addInput(SizedIngredient.fromItems(TicEXItems.CATALYST_IRONS_SPELLBOOK.get()))
                    .setTools(DifferenceIngredient.of(
                            Ingredient.of(TinkerTags.Items.DURABILITY),
                            Ingredient.of(TicEXItems.CATALYST_IRONS_SPELLBOOK)
                    ))
                    .setSlots(SlotType.UPGRADE, 1)
                    .save(topConsumer, prefix(TicEXModifiers.OVERCASTING_MODIFIER, upgradeFolder));
        }

        if(TicEXItems.REVIVAL_SPELLBOOK_IRONS != null) {
            EmbossmentBuildingRecipeBuilder.buildingRecipe((IModifiable) TicEXItems.REVIVAL_SPELLBOOK_IRONS.asItem())
                    .outputSize(1)
                    .save(topConsumer, prefix(TicEXItems.REVIVAL_SPELLBOOK_IRONS, buildingFolder));
        }

        if(TicEXItems.CATALYST_IRONS_SPELLBOOK != null) {
            TagKey<Item> spellbookTags = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(CuriosApi.MODID, Curios.SPELLBOOK_SLOT));
            EmbossmentCastingRecipeBuilder.castingRecipe(TicEXItems.CATALYST_IRONS_SPELLBOOK.get())
                    .setItemCost(1)
                    .setCast(DifferenceIngredient.of(Ingredient.of(spellbookTags), Ingredient.of(TinkerTags.Items.MODIFIABLE)), true)
                    .save(topConsumer, prefix(TicEXItems.CATALYST_IRONS_SPELLBOOK, partsCastingFolder));
        }
    }
}
