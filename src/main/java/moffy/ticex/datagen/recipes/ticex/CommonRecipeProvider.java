package moffy.ticex.datagen.recipes.ticex;

import moffy.ticex.TicEX;
import moffy.ticex.datagen.recipes.ITicEXSmelteryRecipeHelper;
import moffy.ticex.datagen.recipes.TicEXRecipeProvider;
import moffy.ticex.datagen.recipes.ticex.builder.EmbossmentModifierRecipeBuilder;
import moffy.ticex.lib.TicEXMaterials;
import moffy.ticex.lib.TicEXTags;
import moffy.ticex.registry.TicEXBlocks;
import moffy.ticex.registry.TicEXFluids;
import moffy.ticex.registry.TicEXItems;
import moffy.ticex.registry.TicEXModifiers;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.fluid.UnplaceableFluid;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.data.recipe.IMaterialRecipeHelper;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialFluidRecipeBuilder;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelBuilder;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipeBuilder;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.Objects;
import java.util.function.Consumer;

public class CommonRecipeProvider implements ITicEXSmelteryRecipeHelper, IMaterialRecipeHelper {

    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> materialConsumer = withCondition(
                pWriter,
                modsAvailable(TicEX.getResource("default_material"))
        );

        metalMaterialItemOptional(pWriter, TicEXTags.Fluids.ETHERIC, TicEXMaterials.ETHERIC, 2500, true);
        metalMaterialItemOptional(pWriter, TicEXTags.Fluids.OD, TicEXMaterials.OD, 2500, true);
        metalMaterialItemOptional(pWriter, TicEXTags.Fluids.ASTRAL, TicEXMaterials.ASTRAL, 1500, true);

        buildShapedRecipes(pWriter, materialConsumer);
        buildSmelteryRecipes(pWriter, materialConsumer);

        // other recipes
        AlloyRecipeBuilder.alloy(FluidOutput.fromTag(TicEXTags.Fluids.ETHERIC, 270), 2500)
                .addInput(TinkerFluids.moltenSlimesteel.getTag(), FluidValues.INGOT)
                .addInput(TinkerFluids.moltenHepatizon.getTag(), FluidValues.INGOT)
                .addInput(TinkerFluids.moltenGold.getTag(), FluidValues.INGOT)
                .addInput(TicEXFluids.MOLTEN_RECONSTRUCTION_CORE.get(), 250)
                .save(materialConsumer, prefix(TicEXTags.Fluids.ETHERIC.location(), alloysFolder));

        AlloyRecipeBuilder.alloy(FluidOutput.fromTag(TicEXTags.Fluids.OD, 270), 2500)
                .addInput(TinkerFluids.blazingBlood.getTag(), FluidValues.SLIMEBALL)
                .addInput(TinkerFluids.moltenSlimesteel.getTag(), FluidValues.INGOT)
                .addInput(TinkerFluids.moltenAmethyst.getTag(), FluidValues.GEM)
                .addInput(TicEXFluids.MOLTEN_RECONSTRUCTION_CORE.get(), 250)
                .save(materialConsumer, prefix(TicEXTags.Fluids.OD.location(), alloysFolder));

        AlloyRecipeBuilder.alloy(FluidOutput.fromTag(TicEXTags.Fluids.ASTRAL, 270), 1500)
                .addInput(TinkerFluids.magma.getTag(), FluidValues.SLIMEBALL)
                .addInput(TinkerFluids.moltenIron.getTag(), FluidValues.INGOT)
                .addInput(TinkerFluids.moltenCobalt.getTag(), FluidValues.INGOT)
                .addInput(TicEXFluids.MOLTEN_RECONSTRUCTION_CORE.get(), 250)
                .save(materialConsumer, prefix(TicEXTags.Fluids.ASTRAL.location(), alloysFolder));
    }

    public void buildShapedRecipes(Consumer<FinishedRecipe> pWriter, Consumer<FinishedRecipe> materialConsumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TicEXItems.RECONSTRUCTION_CORE.get())
                .define('c', ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge","ingots/cobalt")))
                .define('a', Items.AMETHYST_SHARD)
                .define('s', Items.SHULKER_SHELL)
                .define('p', Items.BLAZE_POWDER)
                .pattern("asa")
                .pattern("pcp")
                .pattern("asa")
                .unlockedBy("has_item", TicEXRecipeProvider.has(TinkerCommons.slimeball.get(SlimeType.SKY)))
                .save(pWriter, prefix(TicEXItems.RECONSTRUCTION_CORE, coresFolder));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TicEXItems.FLICKERING_RECONSTRUCTION_CORE.get())
                .define('c', TicEXItems.RECONSTRUCTION_CORE.get())
                .define('s', Items.NETHER_STAR)
                .pattern("ccc")
                .pattern("csc")
                .pattern("ccc")
                .unlockedBy("has_item", TicEXRecipeProvider.has(TicEXItems.RECONSTRUCTION_CORE.get()))
                .save(pWriter, prefix(TicEXItems.FLICKERING_RECONSTRUCTION_CORE, coresFolder));

        metalRecipes(TicEXTags.Items.ETHERIC, TicEXBlocks.ETHERIC_BLOCK.get(), TicEXItems.ETHERIC_INGOT.get(), TicEXItems.ETHERIC_NUGGET.get(), materialConsumer);
        metalRecipes(TicEXTags.Items.OD, TicEXBlocks.OD_BLOCK.get(), TicEXItems.OD_INGOT.get(), TicEXItems.OD_NUGGET.get(), materialConsumer);
        metalRecipes(TicEXTags.Items.ASTRAL, TicEXBlocks.ASTRAL_BLOCK.get(), TicEXItems.ASTRAL_INGOT.get(), TicEXItems.ASTRAL_NUGGET.get(), materialConsumer);
    }

    public void buildSmelteryRecipes(Consumer<FinishedRecipe> pWriter, Consumer<FinishedRecipe> materialConsumer) {
        Consumer<FinishedRecipe> utilityConsumer = withCondition(
                pWriter,
                modsAvailable(TicEX.getResource("default_utility"))
        );

        ItemCastingRecipeBuilder.retexturedBasinRecipe(ItemOutput.fromItem(TicEXBlocks.SCORCHED_RF_FURNACE.get()))
                .setFluidAndTime(TicEXFluids.MOLTEN_RECONSTRUCTION_CORE, 2000)
                .setCast(TinkerTags.Items.FOUNDRY_BRICKS, true)
                .save(utilityConsumer, prefix(TicEXBlocks.SCORCHED_RF_FURNACE, smelteryCastingFolder + "scorched/"));

        ItemCastingRecipeBuilder.retexturedBasinRecipe(ItemOutput.fromItem(TicEXBlocks.FLUID_TRANSMUTER.get()))
                .setFluidAndTime(TicEXFluids.MOLTEN_RECONSTRUCTION_CORE, 2000)
                .setCast(TinkerSmeltery.scorchedAlloyer.get(), true)
                .save(utilityConsumer, prefix(TicEXBlocks.FLUID_TRANSMUTER, smelteryCastingFolder + "scorched_"));

        ItemCastingRecipeBuilder.retexturedBasinRecipe(ItemOutput.fromItem(TicEXBlocks.SEARED_RF_FURNACE.get()))
                .setFluidAndTime(TicEXFluids.MOLTEN_RECONSTRUCTION_CORE, 2000)
                .setCast(TinkerTags.Items.SMELTERY_BRICKS, true)
                .save(utilityConsumer, prefix(TicEXBlocks.SEARED_RF_FURNACE, smelteryCastingFolder + "seared/"));

        ItemCastingRecipeBuilder.tableRecipe(TicEXItems.FLICKERING_RECONSTRUCTION_CORE.get())
                .setFluid(TicEXTags.Fluids.RECONSTRUCTION_CORE, 2000)
                .setCoolingTime(60)
                .save(pWriter, prefix(TicEXItems.FLICKERING_RECONSTRUCTION_CORE, smelteryCastingFolder + "slime/"));

        MaterialFluidRecipeBuilder.material(TicEXMaterials.RECONSTRUCTION)
                .setTemperature(1000)
                .setFluid(TicEXTags.Fluids.RECONSTRUCTION_CORE, 2000)
                .save(pWriter, prefix(TicEXMaterials.RECONSTRUCTION, materialCastingFolder));

        EmbossmentModifierRecipeBuilder.modifier(TicEXModifiers.EMBOSSMENT_MODIFIER.getId())
                .addInput(SizedIngredient.fromItems(TinkerWorld.earthGeode.get()))
                .addInput(SizedIngredient.fromItems(TinkerWorld.skyGeode.get()))
                .addInput(SizedIngredient.fromItems(TinkerWorld.ichorGeode.get()))
                .addInput(SizedIngredient.fromItems(TinkerWorld.enderGeode.get()))
                .addEmbossItem(SizedIngredient.fromTag(TinkerTags.Items.TOOL_PARTS))
                .setTools(TinkerTags.Items.DURABILITY)
                .save(materialConsumer, prefix(TicEXModifiers.EMBOSSMENT_MODIFIER, slotlessFolder));

        MeltingRecipeBuilder.melting(Ingredient.of(TicEXItems.FLICKERING_RECONSTRUCTION_CORE.get()),
                        FluidOutput.fromFluid(TicEXFluids.MOLTEN_RECONSTRUCTION_CORE.get(), 2000), 1000, (int) 32)
                .save(pWriter, prefix(TicEXItems.FLICKERING_RECONSTRUCTION_CORE, smelteryMeltingFolder));

        for (int i = 0; i < TicEXFluids.RF_FURNACE_FUELS.size(); i++) {
            FluidObject<UnplaceableFluid> fuel = TicEXFluids.RF_FURNACE_FUELS.get(i);
            MeltingFuelBuilder.fuel(fuel.ingredient(50), 150, calculateRfFuelTemperature(i))
                    .rate(5 * i + 5)
                    .save(pWriter, prefix(fuel, smelteryMeltingFolder + "fuel/"));
        }
    }

    public int calculateRfFuelTemperature(int n) {
        int[] temps = new int[]{20, 90, 225, 402, 625, 902, 1230, 1603, 2026, 2494, 3036, 3594, 4240, 5095,
                5647, 6397, 7242, 8101, 9039, 10000};
        return temps[n];
    }

    @Override
    public @NotNull String getModId() {
        return TicEX.MODID;
    }
}
