package moffy.ticex.datagen.general.recipes.avaritia;

import committee.nova.mods.avaritia.init.data.provider.recipe.ModShapedRecipeBuilder;
import committee.nova.mods.avaritia.init.registry.ModItems;
import moffy.ticex.TicEX;
import moffy.ticex.datagen.general.recipes.ITicEXSmelteryRecipeHelper;
import moffy.ticex.datagen.general.recipes.TicEXRecipeProvider;
import moffy.ticex.lib.TicEXMaterials;
import moffy.ticex.lib.TicEXTags;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.recipe.IMaterialRecipeHelper;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialFluidRecipeBuilder;
import slimeknights.tconstruct.library.recipe.melting.MaterialMeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;

import java.util.function.Consumer;

public class AvaritiaRecipeProvider implements ITicEXSmelteryRecipeHelper, IMaterialRecipeHelper {

    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> topConsumer = withCondition(
            pWriter,
            modsAvailable(new ResourceLocation(TicEX.MODID, "avaritia_compat"))
        );

        metalMaterialRecipe(topConsumer, TicEXMaterials.INFINITY, materialFolder, "infinity", true);
        metalMaterialRecipe(topConsumer, TicEXMaterials.NEUTRON, materialFolder, "neutron", true);
        metalMaterialRecipe(topConsumer, TicEXMaterials.CRYSTAL_MATRIX, materialFolder, "crystal_matrix", true);
        metalMaterialRecipe(topConsumer, TicEXMaterials.INFINITY, materialFolder, "blazing", true);

        // modifier

        if (TicEXRegistry.CELESTIAL_MODIFIER != null) {
            ModifierRecipeBuilder.modifier(TicEXRegistry.CELESTIAL_MODIFIER)
                .setTools(TinkerTags.Items.BOOTS)
                .addInput(TicEXRegistry.CELESTIAL_CORE.get())
                .setSlots(SlotType.DEFENSE, 2)
                .setMaxLevel(1)
                .checkTraitLevel()
                .saveSalvage(topConsumer, prefix(TicEXRegistry.CELESTIAL_MODIFIER.getId(), defenseSalvage))
                .save(topConsumer, prefix(TicEXRegistry.CELESTIAL_MODIFIER.getId(), defenseFolder));
        }

        if (TicEXRegistry.ENDESTSHOT_MODIFIER != null) {
            ModifierRecipeBuilder.modifier(TicEXRegistry.ENDESTSHOT_MODIFIER)
                .setTools(TinkerTags.Items.RANGED)
                .addInput(ModItems.endest_pearl.get())
                .setSlots(SlotType.ABILITY, 2)
                .setMaxLevel(1)
                .checkTraitLevel()
                .saveSalvage(topConsumer, prefix(TicEXRegistry.ENDESTSHOT_MODIFIER.getId(), abilitySalvage))
                .save(topConsumer, prefix(TicEXRegistry.ENDESTSHOT_MODIFIER.getId(), abilityFolder));
        }

        // core

        if (TicEXRegistry.CELESTIAL_CORE != null) {
            ModShapedRecipeBuilder.shaped(RecipeCategory.MISC, TicEXRegistry.CELESTIAL_CORE.get(),1)
                    .showNotification(true)
                    .define('c', TicEXRegistry.RECONSTRUCTION_CORE.get())
                    .define('d', Items.DRAGON_HEAD)
                    .define('e', Items.ELYTRA)
                    .define('i', ModItems.infinity_catalyst.get())
                    .define('p', ModItems.endest_pearl.get())
                    .pattern(" d ")
                    .pattern("ice")
                    .pattern(" p ")
                    .unlockedBy("has_item", TicEXRecipeProvider.has(TicEXRegistry.RECONSTRUCTION_CORE.get()))
                    .save(topConsumer, prefix(TicEXRegistry.CELESTIAL_CORE.getId(), coresFolder));
        }

        // material fluid

        MaterialFluidRecipeBuilder.material(TicEXMaterials.CRYSTAL_MATRIX)
                .setTemperature(1050)
                .setFluid(TicEXTags.Fluids.CRYSTAL_MATRIX, FluidValues.INGOT)
                .save(topConsumer, prefix(TicEXMaterials.CRYSTAL_MATRIX, materialCastingFolder));
        MaterialFluidRecipeBuilder.material(TicEXMaterials.INFINITY)
                .setTemperature(3180)
                .setFluid(TicEXTags.Fluids.INFINITY, FluidValues.INGOT)
                .save(topConsumer, prefix(TicEXMaterials.INFINITY, materialCastingFolder));

        MaterialFluidRecipeBuilder.material(TicEXMaterials.NEUTRON)
                .setTemperature(1400)
                .setFluid(TicEXTags.Fluids.NEUTRON, FluidValues.INGOT)
                .save(topConsumer, prefix(TicEXMaterials.NEUTRON, materialCastingFolder));
        MaterialFluidRecipeBuilder.material(TicEXMaterials.BLAZING)
                .setTemperature(1250)
                .setFluid(TicEXTags.Fluids.BLAZING, FluidValues.INGOT)
                .save(topConsumer, prefix(TicEXMaterials.BLAZING, materialCastingFolder));

        // smeltery

        if(TicEXRegistry.MOLTEN_CRYSTAL_MATRIX != null) {
                MaterialMeltingRecipeBuilder.material(TicEXMaterials.CRYSTAL_MATRIX,
                        3180,
                                FluidOutput.fromFluid(TicEXRegistry.MOLTEN_CRYSTAL_MATRIX.get().getSource(), FluidValues.INGOT))
                    .save(topConsumer, prefix(TicEXMaterials.CRYSTAL_MATRIX, materialMeltingFolder));

            metalIngotOptional(topConsumer, TicEXTags.Fluids.CRYSTAL_MATRIX, TicEXTags.Items.CRYSTAL_MATRIX_BLOCK, 2100, TicEXRegistry.MOLTEN_CRYSTAL_MATRIX.getId());
        }

        if(TicEXRegistry.MOLTEN_INFINITY != null) {
                MaterialMeltingRecipeBuilder.material(TicEXMaterials.INFINITY,
                            3180,
                                FluidOutput.fromFluid(TicEXRegistry.MOLTEN_INFINITY.get().getSource(), FluidValues.INGOT))
                    .save(topConsumer, prefix(TicEXMaterials.INFINITY, materialMeltingFolder));

            metalIngotOptional(topConsumer, TicEXTags.Fluids.INFINITY, TicEXTags.Items.INFINITY_BLOCK, 6360, TicEXRegistry.MOLTEN_INFINITY.getId());
        }

        if(TicEXRegistry.MOLTEN_NEUTRON != null) {
                MaterialMeltingRecipeBuilder.material(TicEXMaterials.NEUTRON,
                        1400,
                                FluidOutput.fromFluid(TicEXRegistry.MOLTEN_NEUTRON.get().getSource(), FluidValues.INGOT))
                    .save(topConsumer, prefix(TicEXMaterials.NEUTRON, materialMeltingFolder));

            metalIngotOptional(topConsumer, TicEXTags.Fluids.NEUTRON, TicEXTags.Items.NEUTRON_BLOCK, 5000, TicEXRegistry.MOLTEN_NEUTRON.getId());
        }

        if(TicEXRegistry.MOLTEN_BLAZING != null) {
            MaterialMeltingRecipeBuilder.material(TicEXMaterials.BLAZING,
                            2500,
                            FluidOutput.fromFluid(TicEXRegistry.MOLTEN_BLAZING.get().getSource(), FluidValues.INGOT))
                    .save(topConsumer, prefix(TicEXMaterials.BLAZING, materialMeltingFolder));

            metalIngotOptional(topConsumer, TicEXTags.Fluids.BLAZING, TicEXTags.Items.BLAZING_BLOCK, 1800, TicEXRegistry.MOLTEN_BLAZING.getId());
        }
    }

    @Override
    public @NotNull String getModId() {
        return TicEX.MODID;
    }
}
