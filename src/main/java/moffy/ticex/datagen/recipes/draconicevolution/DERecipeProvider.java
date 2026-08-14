package moffy.ticex.datagen.recipes.draconicevolution;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.datagen.FusionRecipeBuilder;
import com.brandon3055.draconicevolution.init.DEContent;
import moffy.ticex.TicEX;
import moffy.ticex.datagen.recipes.ITicEXSmelteryRecipeHelper;
import moffy.ticex.lib.TicEXMaterials;
import moffy.ticex.lib.TicEXTags;
import moffy.ticex.registry.TicEXItems;
import moffy.ticex.registry.TicEXModifiers;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.recipe.ISmelteryRecipeHelper;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class DERecipeProvider implements ITicEXSmelteryRecipeHelper, ISmelteryRecipeHelper {
    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> topConsumer = withCondition(
                pWriter,
                modsAvailable(TicEX.getResource("draconicevolution_compat"))
        );

        if(TicEXModifiers.INJECT_MODIFIER != null) {
            ModifierRecipeBuilder.modifier(TicEXModifiers.INJECT_MODIFIER.getId())
                    .allowCrystal()
                    .checkTraitLevel()
                    .addInput(TicEXItems.INJECT_CORE.get())
                    .setMaxLevel(1)
                    .setTools(Ingredient.fromValues(Stream.of(
                            new Ingredient.TagValue(TinkerTags.Items.MELEE),
                            new Ingredient.TagValue(TinkerTags.Items.MODIFIABLE)
                    )))
                    .save(topConsumer, prefix(TicEXModifiers.INJECT_MODIFIER, slotlessFolder));
        }

        if(TicEXModifiers.EVOLVED_MODIFIER != null) {
            evolvedModifier(topConsumer, "draconium_", TicEXItems.DRACONIUM_EVOLVED_CORE.get(), 1);
            evolvedModifier(topConsumer, "wyvern_", TicEXItems.WYVERN_EVOLVED_CORE.get(), 2);
            evolvedModifier(topConsumer, "draconic_", TicEXItems.DRACONIC_EVOLVED_CORE.get(), 3);
            evolvedModifier(topConsumer, "chaotic_", TicEXItems.CHAOTIC_EVOLVED_CORE.get(), 4);
        }

        buildCoresFusionRecipes(topConsumer);
        buildMaterialRecipes(topConsumer);
        buildSmelteryRecipes(topConsumer);
    }

    public void buildCoresFusionRecipes(Consumer<FinishedRecipe> topConsumer) {
        if(TicEXItems.DRACONIUM_EVOLVED_CORE != null) {
            FusionRecipeBuilder.builder(TicEXItems.DRACONIUM_EVOLVED_CORE.get(), 1, prefix(TicEXItems.DRACONIUM_EVOLVED_CORE, coresFolder))
                    .techLevel(TechLevel.DRACONIUM)
                    .energy(16000000)
                    .catalyst(TicEXItems.RECONSTRUCTION_CORE.get())
                    .ingredient(DEContent.CORE_DRACONIUM)
                    .ingredient(Items.DRAGON_BREATH)
                    .ingredient(Items.GOLDEN_APPLE)
                    .ingredient(Items.GOLDEN_APPLE)
                    .ingredient(Items.ENDER_EYE)
                    .ingredient(Items.ENDER_EYE)
                    .ingredient(Tags.Items.INGOTS_NETHERITE)
                    .ingredient(Tags.Items.INGOTS_NETHERITE)
                    .ingredient(Items.SHULKER_SHELL)
                    .ingredient(Items.SHULKER_SHELL)
                    .ingredient(DEContent.MODULE_CORE)
                    .build(topConsumer);
        }

        if(TicEXItems.WYVERN_EVOLVED_CORE != null) {
            FusionRecipeBuilder.builder(TicEXItems.WYVERN_EVOLVED_CORE.get(), 1, prefix(TicEXItems.WYVERN_EVOLVED_CORE, coresFolder))
                    .techLevel(TechLevel.WYVERN)
                    .energy(64000000)
                    .catalyst(TicEXItems.RECONSTRUCTION_CORE.get())
                    .ingredient(DEContent.CORE_WYVERN)
                    .ingredient(DEContent.CORE_DRACONIUM)
                    .ingredient(DEContent.CORE_DRACONIUM)
                    .ingredient(Items.NETHER_STAR)
                    .ingredient(Items.NETHER_STAR)
                    .ingredient(DEContent.MODULE_CORE)
                    .build(topConsumer);
        }

        if (TicEXItems.DRACONIC_EVOLVED_CORE != null) {
            FusionRecipeBuilder.builder(TicEXItems.DRACONIC_EVOLVED_CORE.get(), 1, prefix(TicEXItems.DRACONIC_EVOLVED_CORE, coresFolder))
                    .techLevel(TechLevel.DRACONIC)
                    .energy(256000000)
                    .catalyst(TicEXItems.RECONSTRUCTION_CORE.get())
                    .ingredient(DEContent.CORE_WYVERN)
                    .ingredient(DEContent.CORE_AWAKENED)
                    .ingredient(Items.NETHER_STAR)
                    .ingredient(Items.NETHER_STAR)
                    .ingredient(Tags.Items.STORAGE_BLOCKS_NETHERITE)
                    .ingredient(Tags.Items.STORAGE_BLOCKS_NETHERITE)
                    .ingredient(DEContent.CORE_WYVERN)
                    .ingredient(DEContent.MODULE_CORE)
                    .build(topConsumer);
        }

        if (TicEXItems.CHAOTIC_EVOLVED_CORE != null) {
            FusionRecipeBuilder.builder(TicEXItems.CHAOTIC_EVOLVED_CORE.get(), 1, prefix(TicEXItems.CHAOTIC_EVOLVED_CORE, coresFolder))
                    .techLevel(TechLevel.CHAOTIC)
                    .energy(1024000000)
                    .catalyst(TicEXItems.RECONSTRUCTION_CORE.get())
                    .ingredient(DEContent.CORE_WYVERN)
                    .ingredient(DEContent.CORE_WYVERN)
                    .ingredient(DEContent.CORE_AWAKENED)
                    .ingredient(DEContent.CORE_AWAKENED)
                    .ingredient(DEContent.CORE_CHAOTIC)
                    .ingredient(Items.DRAGON_EGG)
                    .ingredient(Items.DRAGON_EGG)
                    .ingredient(DEContent.MODULE_CORE)
                    .build(topConsumer);
        }

        if (TicEXItems.INJECT_CORE != null) {
            FusionRecipeBuilder.builder(TicEXItems.INJECT_CORE.get(), 1, prefix(TicEXItems.INJECT_CORE, coresFolder))
                    .techLevel(TechLevel.CHAOTIC)
                    .energy(256000000)
                    .catalyst(TicEXItems.RECONSTRUCTION_CORE.get())
                    .ingredient(DEContent.CORE_DRACONIUM)
                    .ingredient(DEContent.CORE_WYVERN)
                    .ingredient(DEContent.CORE_AWAKENED)
                    .ingredient(DEContent.CORE_CHAOTIC)
                    .build(topConsumer);
        }
    }

    public void buildMaterialRecipes(Consumer<FinishedRecipe> topConsumer) {
        MaterialRecipeBuilder.materialRecipe(TicEXMaterials.DRACONIUM)
                .setNeeded(1)
                .setValue(1)
                .setIngredient(TicEXItems.DRACONIUM_CRYSTAL.get())
                .save(topConsumer, prefix(TicEXMaterials.DRACONIUM, materialFolder + "chaotic/"));

        MaterialRecipeBuilder.materialRecipe(TicEXMaterials.WYVERN)
                .setNeeded(1)
                .setValue(1)
                .setIngredient(TicEXItems.WYVERN_CRYSTAL.get())
                .save(topConsumer, prefix(TicEXMaterials.WYVERN, materialFolder + "wyvern/"));

        MaterialRecipeBuilder.materialRecipe(TicEXMaterials.DRACONIC)
                .setNeeded(1)
                .setValue(1)
                .setIngredient(TicEXItems.DRACONIC_CRYSTAL.get())
                .save(topConsumer, prefix(TicEXMaterials.DRACONIC, materialFolder + "draconic/"));

        MaterialRecipeBuilder.materialRecipe(TicEXMaterials.CHAOTIC)
                .setNeeded(1)
                .setValue(1)
                .setIngredient(TicEXItems.CHAOTIC_CRYSTAL.get())
                .save(topConsumer, prefix(TicEXMaterials.CHAOTIC, materialFolder + "chaotic/"));
    }

    public void buildSmelteryRecipes(Consumer<FinishedRecipe> topConsumer) {
        ItemCastingRecipeBuilder.tableRecipe(TicEXItems.DRACONIUM_CRYSTAL.get())
                .setFluid(TicEXTags.Fluids.RECONSTRUCTION_CORE, 500)
                .setCoolingTime(83)
                .setCast(DEContent.CORE_DRACONIUM.get(), true)
                .save(topConsumer, prefix(TicEXItems.DRACONIUM_CRYSTAL, smelteryCastingFolder));

        ItemCastingRecipeBuilder.tableRecipe(TicEXItems.WYVERN_CRYSTAL.get())
                .setFluid(TicEXTags.Fluids.RECONSTRUCTION_CORE, 500)
                .setCoolingTime(83)
                .setCast(DEContent.CORE_WYVERN.get(), true)
                .save(topConsumer, prefix(TicEXItems.WYVERN_CRYSTAL, smelteryCastingFolder));

        ItemCastingRecipeBuilder.tableRecipe(TicEXItems.DRACONIC_CRYSTAL.get())
                .setFluid(TicEXTags.Fluids.RECONSTRUCTION_CORE, 500)
                .setCoolingTime(83)
                .setCast(DEContent.CORE_AWAKENED.get(), true)
                .save(topConsumer, prefix(TicEXItems.DRACONIC_CRYSTAL, smelteryCastingFolder));

        ItemCastingRecipeBuilder.tableRecipe(TicEXItems.CHAOTIC_CRYSTAL.get())
                .setFluid(TicEXTags.Fluids.RECONSTRUCTION_CORE, 500)
                .setCoolingTime(83)
                .setCast(DEContent.CORE_CHAOTIC.get(), true)
                .save(topConsumer, prefix(TicEXItems.CHAOTIC_CRYSTAL, smelteryCastingFolder));
    }

    public void evolvedModifier(Consumer<FinishedRecipe> consumer, String prefix, Item core, int level) {
        ResourceLocation rl = TicEXModifiers.EVOLVED_MODIFIER.getId().withPrefix(prefix);
        ModifierRecipeBuilder.modifier(TicEXModifiers.EVOLVED_MODIFIER)
                .setTools(Ingredient.fromValues(Stream.of(
                        new Ingredient.TagValue(TinkerTags.Items.MELEE_WEAPON),
                        new Ingredient.TagValue(TinkerTags.Items.HARVEST),
                        new Ingredient.TagValue(TinkerTags.Items.RANGED)
                )))
                .addInput(core)
                .setSlots(SlotType.ABILITY, 1)
                .exactLevel(level)
                .checkTraitLevel()
                .allowCrystal()
                .save(consumer, prefix(rl, abilityFolder));
    }

}
