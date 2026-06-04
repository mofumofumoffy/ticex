package moffy.ticex.datagen.general.recipes.projecte;

import moffy.ticex.TicEX;
import moffy.ticex.datagen.general.recipes.ITicEXRecipeHelper;
import moffy.ticex.lib.TicEXTags;
import moffy.ticex.modules.general.TicEXRegistry;
import moze_intel.projecte.gameObjs.registries.PEItems;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.DifferenceIngredient;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.recipe.IToolRecipeHelper;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;

import java.util.function.Consumer;

public class PERecipeProvider implements ITicEXRecipeHelper, IToolRecipeHelper {
    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> topConsumer = withCondition(
                pWriter,
                modsAvailable(TicEX.getResource("projecte_compat"))
        );

        /*if(TicEXRegistry.CLUSTER_MODIFIER != null) {
            ModifierRecipeBuilder.modifier(TicEXRegistry.CLUSTER_MODIFIER)
                    .setTools(TinkerTags.Items.HARVEST)
                    .addInput(PEItems.DARK_MATTER.get())
                    .addInput(PEItems.DARK_MATTER.get())
                    .addInput(PEItems.DARK_MATTER.get())
                    .addInput(Tags.Items.GEMS_DIAMOND)
                    .addInput(Tags.Items.GEMS_DIAMOND)
                    .exactLevel(1)
                    .setSlots(SlotType.UPGRADE, 1)
                    .checkTraitLevel()
                    .allowCrystal()
                    .save(topConsumer, prefix(TicEXRegistry.CLUSTER_MODIFIER.getId().withSuffix("_1"), upgradeFolder));
            ModifierRecipeBuilder.modifier(TicEXRegistry.CLUSTER_MODIFIER)
                    .setTools(TinkerTags.Items.HARVEST)
                    .addInput(PEItems.RED_MATTER.get())
                    .addInput(PEItems.RED_MATTER.get())
                    .addInput(PEItems.RED_MATTER.get())
                    .addInput(PEItems.DARK_MATTER.get())
                    .exactLevel(2)
                    .setSlots(SlotType.UPGRADE, 1)
                    .checkTraitLevel()
                    .allowCrystal()
                    .save(topConsumer, prefix(TicEXRegistry.CLUSTER_MODIFIER.getId().withSuffix("_2"), upgradeFolder));
        }*/

        if(TicEXRegistry.ABYSSAL_MODIFIER != null) {
            ModifierRecipeBuilder.modifier(TicEXRegistry.ABYSSAL_MODIFIER)
                    .setTools(DifferenceIngredient.of(Ingredient.of(TinkerTags.Items.HELMETS), Ingredient.of(TicEXTags.Items.GEM_ARMOR)))
                    .addInput(item(TicEX.getResource("catalyst_gem_helmet")))
                    .setSlots(SlotType.DEFENSE, 1)
                    .save(topConsumer, prefix(TicEXRegistry.ABYSSAL_MODIFIER.getId(), defenseFolder));
        }

        if(TicEXRegistry.GRAVITY_MODIFIER != null) {
            ModifierRecipeBuilder.modifier(TicEXRegistry.GRAVITY_MODIFIER)
                    .setTools(DifferenceIngredient.of(Ingredient.of(TinkerTags.Items.LEGGINGS), Ingredient.of(TicEXTags.Items.GEM_ARMOR)))
                    .addInput(item(TicEX.getResource("catalyst_gem_leggings")))
                    .setSlots(SlotType.DEFENSE, 1)
                    .save(topConsumer, prefix(TicEXRegistry.GRAVITY_MODIFIER.getId(), defenseFolder));
        }

        if(TicEXRegistry.HURRICANE_MODIFIER != null) {
            ModifierRecipeBuilder.modifier(TicEXRegistry.HURRICANE_MODIFIER)
                    .setTools(DifferenceIngredient.of(Ingredient.of(TinkerTags.Items.BOOTS), Ingredient.of(TicEXTags.Items.GEM_ARMOR)))
                    .addInput(item(TicEX.getResource("catalyst_gem_boots")))
                    .setSlots(SlotType.DEFENSE, 1)
                    .save(topConsumer, prefix(TicEXRegistry.HURRICANE_MODIFIER.getId(), defenseFolder));
        }

        if(TicEXRegistry.INFERNAL_MODIFIER != null) {
            ModifierRecipeBuilder.modifier(TicEXRegistry.INFERNAL_MODIFIER)
                    .setTools(DifferenceIngredient.of(Ingredient.of(TinkerTags.Items.CHESTPLATES), Ingredient.of(TicEXTags.Items.GEM_ARMOR)))
                    .addInput(item(TicEX.getResource("catalyst_gem_chestplate")))
                    .setSlots(SlotType.DEFENSE, 1)
                    .save(topConsumer, prefix(TicEXRegistry.INFERNAL_MODIFIER.getId(), defenseFolder));
        }

        if(TicEXRegistry.SINGULAR_GEM_ARMOR != null) {
            ResourceLocation seramGear = TicEX.getResource("seram_gear");
        }

        if(TicEXRegistry.CATALYST_GEM != null) {
            armorTableCasting(topConsumer, TicEXRegistry.CATALYST_GEM.get(ArmorItem.Type.HELMET), PEItems.GEM_HELMET.asItem());
            armorTableCasting(topConsumer, TicEXRegistry.CATALYST_GEM.get(ArmorItem.Type.CHESTPLATE), PEItems.GEM_CHESTPLATE.asItem());
            armorTableCasting(topConsumer, TicEXRegistry.CATALYST_GEM.get(ArmorItem.Type.LEGGINGS), PEItems.GEM_LEGGINGS.asItem());
            armorTableCasting(topConsumer, TicEXRegistry.CATALYST_GEM.get(ArmorItem.Type.BOOTS), PEItems.GEM_BOOTS.asItem());
        }
    }

    public void armorTableCasting(Consumer<FinishedRecipe> topConsumer, ToolPartItem gemItem, Item castItem) {
        MaterialCastingRecipeBuilder.tableRecipe(gemItem)
                .setCast(castItem, true)
                .setItemCost(1)
                .save(topConsumer, prefix(this.id(gemItem), armorFolder));

        MaterialCastingRecipeBuilder.tableRecipe(gemItem)
                .setCast(castItem, true)
                .setItemCost(1)
                .save(topConsumer, prefix(this.id(gemItem), materialCastingFolder));
    }
}
