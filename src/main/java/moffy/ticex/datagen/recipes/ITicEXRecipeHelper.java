package moffy.ticex.datagen.recipes;

import moffy.addonapi.AddonAPI;
import moffy.addonapi.ModsAvailableCondition;
import moffy.ticex.TicEX;
import moffy.ticex.lib.TicEXTags;
import moffy.ticex.registry.TicEXBlocks;
import moffy.ticex.registry.TicEXItems;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.recipe.data.IRecipeHelper;
import slimeknights.tconstruct.library.recipe.FluidValues;

import java.util.function.Consumer;

public interface ITicEXRecipeHelper extends IRecipeHelper, IConditionBuilder {
    // tools/modifiers/
    String upgradeFolder    = "tools/modifiers/upgrade/";
    String abilityFolder    = "tools/modifiers/ability/";
    String slotlessFolder   = "tools/modifiers/slotless/";
    String upgradeSalvage   = "tools/modifiers/salvage/upgrade/";
    String abilitySalvage   = "tools/modifiers/salvage/ability/";
    String defenseFolder    = "tools/modifiers/defense/";
    String defenseSalvage   = "tools/modifiers/salvage/defense/";
    String compatFolder     = "tools/modifiers/compat/";
    String compatSalvage    = "tools/modifiers/salvage/compat/";
    String worktableFolder  = "tools/modifiers/worktable/";

    // tools/parts/
    String partsFolder = "tools/parts/";
    String partsBuilderFolder      = "tools/parts/builder/";
    String partsCastingFolder = "tools/parts/casting/";

    // tools/armor/
    String armorFolder   = "tools/armor/";

    // tools/materials/
    String materialFolder   = "tools/materials/";
    String materialCastingFolder = "tools/materials/casting/";
    String materialMeltingFolder = "tools/materials/melting/";

    // tools/building/
    String buildingFolder   = "tools/building/";

    // smeltery/
    String alloysFolder   = "smeltery/alloys/";
    String smelteryCastingFolder   = "smeltery/casting/";
    String smelteryCastsFolder   = "smeltery/casts/";
    String smelteryMeltingFolder   = "smeltery/melting/";

    // items/
    String coresFolder      = "items/cores/";
    String itemsFolder      = "items/";

    default ICondition modsAvailable(ResourceLocation rl) {
        return new ModsAvailableCondition(rl);
    }

    default Item item(ResourceLocation resourceLocation) {
        return ForgeRegistries.ITEMS.getValue(resourceLocation);
    }

    default void metalRecipes(TicEXTags.Items.MetalItemTagSet tagSet, Block block, Item ingot, Item nugget,Consumer<FinishedRecipe> pWriter){
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, block)
                .showNotification(true)
                .define('#', tagSet.ingot())
                .define('*', ingot)
                .pattern("###")
                .pattern("#*#")
                .pattern("###")
                .unlockedBy("has_item", TicEXRecipeProvider.has(ingot))
                .save(pWriter, prefix(itemsFolder + tagSet.getName() +"_block_from_ingot"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ingot, FluidValues.METAL_BLOCK / FluidValues.INGOT)
                .requires(tagSet.block())
                .unlockedBy("has_item", TicEXRecipeProvider.has(block))
                .save(pWriter, prefix(itemsFolder + tagSet.getName() + "_ingot_from_block"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ingot)
                .showNotification(true)
                .define('#', tagSet.nugget())
                .define('*', nugget)
                .pattern("###")
                .pattern("#*#")
                .pattern("###")
                .unlockedBy("has_item", TicEXRecipeProvider.has(nugget))
                .save(pWriter, prefix(itemsFolder + tagSet.getName() +"_ingot_from_nugget"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, nugget, FluidValues.INGOT / FluidValues.NUGGET)
                .requires(tagSet.ingot())
                .unlockedBy("has_item", TicEXRecipeProvider.has(ingot))
                .save(pWriter, prefix(itemsFolder + tagSet.getName() + "_nugget_from_ingot"));
    }

    @Override
    default @NotNull String getModId() {
        return TicEX.MODID;
    }
}
