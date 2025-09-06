package moffy.ticex.datagen.general.recipes.ticex;

import moffy.ticex.TicEX;
import moffy.ticex.datagen.general.recipes.ITicEXSmelteryRecipeHelper;
import moffy.ticex.datagen.general.recipes.TicEXRecipeProvider;
import moffy.ticex.datagen.general.recipes.ticex.embossment.EmbossmentModifierRecipeBuilder;
import moffy.ticex.lib.TicEXMaterials;
import moffy.ticex.lib.TicEXTags;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
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
import slimeknights.tconstruct.library.recipe.melting.MaterialMeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipeBuilder;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.function.Consumer;

public class CommonRecipeProvider implements ITicEXSmelteryRecipeHelper, IMaterialRecipeHelper {

    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        metalMaterialRecipe(pWriter, TicEXMaterials.ETHERIC, materialFolder, "etheric", false);

        buildShapedRecipes(pWriter);
        buildMaterialRecipes(pWriter);
        buildSmelteryRecipes(pWriter);

        // other recipes

        AlloyRecipeBuilder.alloy(FluidOutput.fromTag(TicEXTags.Fluids.ETHERIC, 270), 2500)
                .addInput(TinkerFluids.moltenSlimesteel.get(), FluidValues.INGOT)
                .addInput(TicEXTags.Fluids.HEPATIZON, FluidValues.INGOT)
                .addInput(TicEXTags.Fluids.GOLD, FluidValues.INGOT)
                .addInput(TicEXRegistry.MOLTEN_RECONSTRUCTION_CORE.get(), 250)
                .save(pWriter, prefix(TicEXTags.Fluids.ETHERIC.location(), alloysFolder));

        metalIngotOptional(pWriter, TicEXTags.Fluids.ETHERIC, TicEXTags.Items.ETHERIC_BLOCK, 5000, TicEXRegistry.MOLTEN_ETHERIC.getId());
    }

    public void buildShapedRecipes(Consumer<FinishedRecipe> pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TicEXRegistry.RECONSTRUCTION_CORE.get())
                .define('c', TinkerCommons.slimeball.get(SlimeType.SKY))
                .define('a', Items.AMETHYST_SHARD)
                .define('s', Items.SHULKER_SHELL)
                .define('p', Items.BLAZE_POWDER)
                .pattern("asa")
                .pattern("pcp")
                .pattern("asa")
                .unlockedBy("has_item", TicEXRecipeProvider.has(TinkerCommons.slimeball.get(SlimeType.SKY)))
                .save(pWriter, prefix(TicEXRegistry.RECONSTRUCTION_CORE, coresFolder));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TicEXRegistry.FLICKERING_RECONSTRUCTION_CORE.get())
                .define('c', TicEXRegistry.RECONSTRUCTION_CORE.get())
                .define('s', Items.NETHER_STAR)
                .pattern("ccc")
                .pattern("csc")
                .pattern("ccc")
                .unlockedBy("has_item", TicEXRecipeProvider.has(TicEXRegistry.RECONSTRUCTION_CORE.get()))
                .save(pWriter, prefix(TicEXRegistry.FLICKERING_RECONSTRUCTION_CORE, coresFolder));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TicEXRegistry.ETHERIC_BLOCK.get())
                .showNotification(true)
                .define('#', TicEXTags.Items.ETHERIC_INGOT)
                .define('*', TicEXRegistry.ETHERIC_INGOT.get())
                .pattern("###")
                .pattern("#*#")
                .pattern("###")
                .unlockedBy("has_item", TicEXRecipeProvider.has(TicEXRegistry.ETHERIC_INGOT.get()))
                .save(pWriter, prefix(itemsFolder + "etheric_block_from_ingot"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TicEXRegistry.ETHERIC_INGOT.get(), FluidValues.METAL_BLOCK / FluidValues.INGOT)
                .requires(TicEXRegistry.ETHERIC_BLOCK.get())
                .unlockedBy("has_item", TicEXRecipeProvider.has(TicEXRegistry.ETHERIC_BLOCK.get()))
                .save(pWriter, prefix(itemsFolder + "etheric_ingot_from_block"));
    }

    public void buildMaterialRecipes(Consumer<FinishedRecipe> pWriter) {
        MaterialFluidRecipeBuilder.material(TicEXMaterials.ETHERIC)
                .setTemperature(2500)
                .setFluid(TicEXTags.Fluids.ETHERIC, FluidValues.INGOT)
                .save(pWriter, prefix(TicEXMaterials.ETHERIC, materialCastingFolder));

        MaterialMeltingRecipeBuilder.material(TicEXMaterials.ETHERIC, 2500, new FluidStack(TicEXRegistry.MOLTEN_ETHERIC.get().getSource(), FluidValues.INGOT))
                .save(pWriter, prefix(TicEXMaterials.ETHERIC, materialMeltingFolder));
    }

    public void buildSmelteryRecipes(Consumer<FinishedRecipe> pWriter) {
        ItemCastingRecipeBuilder.retexturedBasinRecipe(ItemOutput.fromItem(TicEXRegistry.SCORCHED_RF_FURNACE.get()))
                .setFluidAndTime(TicEXRegistry.MOLTEN_RECONSTRUCTION_CORE, 2000)
                .setCast(TinkerTags.Items.FOUNDRY_BRICKS, true)
                .save(pWriter, prefix(TicEXRegistry.SCORCHED_RF_FURNACE, smelteryCastingFolder + "scorched/"));

        ItemCastingRecipeBuilder.retexturedBasinRecipe(ItemOutput.fromItem(TicEXRegistry.FLUID_TRANSMUTER.get()))
                .setFluidAndTime(TicEXRegistry.MOLTEN_RECONSTRUCTION_CORE, 2000)
                .setCast(TinkerSmeltery.scorchedAlloyer.get(), true)
                .save(pWriter, prefix(TicEXRegistry.FLUID_TRANSMUTER, smelteryCastingFolder + "scorched"));

        ItemCastingRecipeBuilder.retexturedBasinRecipe(ItemOutput.fromItem(TicEXRegistry.SEARED_RF_FURNACE.get()))
                .setFluidAndTime(TicEXRegistry.MOLTEN_RECONSTRUCTION_CORE, 2000)
                .setCast(TinkerTags.Items.SMELTERY_BRICKS, true)
                .save(pWriter, prefix(TicEXRegistry.SEARED_RF_FURNACE, smelteryCastingFolder + "seared/"));

        ItemCastingRecipeBuilder.tableRecipe(TicEXRegistry.FLICKERING_RECONSTRUCTION_CORE.get())
                .setFluid(TicEXTags.Fluids.RECONSTRUCTION_CORE, 2000)
                .setCoolingTime(60)
                .save(pWriter, prefix(TicEXRegistry.FLICKERING_RECONSTRUCTION_CORE, smelteryCastingFolder + "slime/"));

        MaterialFluidRecipeBuilder.material(TicEXMaterials.RECONSTRUCTION)
                .setTemperature(1000)
                .setFluid(TicEXTags.Fluids.RECONSTRUCTION_CORE, 2000)
                .save(pWriter, prefix(TicEXMaterials.RECONSTRUCTION, materialCastingFolder));

        EmbossmentModifierRecipeBuilder.modifier(TicEXRegistry.EMBOSSMENT_MODIFIER.getId())
                .addInput(SizedIngredient.fromItems(TinkerWorld.earthGeode.get()))
                .addInput(SizedIngredient.fromItems(TinkerWorld.skyGeode.get()))
                .addInput(SizedIngredient.fromItems(TinkerWorld.ichorGeode.get()))
                .addInput(SizedIngredient.fromItems(TinkerWorld.enderGeode.get()))
                .addEmbossItem(SizedIngredient.fromTag(TinkerTags.Items.TOOL_PARTS))
                .setTools(TinkerTags.Items.DURABILITY)
                .save(pWriter, prefix(TicEXRegistry.EMBOSSMENT_MODIFIER, slotlessFolder));

        MeltingRecipeBuilder.melting(Ingredient.of(TicEXRegistry.FLICKERING_RECONSTRUCTION_CORE.get()),
                        FluidOutput.fromFluid(TicEXRegistry.MOLTEN_RECONSTRUCTION_CORE.get(), 2000), 1000, (int) 32)
                .save(pWriter, prefix(TicEXRegistry.FLICKERING_RECONSTRUCTION_CORE, smelteryMeltingFolder));

        for (int i = 0; i < TicEXRegistry.RF_FURNACE_FUELS.size(); i++) {
            FluidObject<UnplaceableFluid> fuel = TicEXRegistry.RF_FURNACE_FUELS.get(i);
            MeltingFuelBuilder.fuel(fuel.ingredient(50), 150, calculateRfFuelTemperature(i))
                    .rate(5 * i + 5)
                    .save(pWriter, prefix(fuel, smelteryMeltingFolder + "fuel/"));
        }
    }

    public int calculateRfFuelTemperature(int n) {
        // 23x^w + 5x/19 + 20
        return 25*n*n + (5*n)/19 + 20;
    }

    @Override
    public @NotNull String getModId() {
        return TicEX.MODID;
    }
}
