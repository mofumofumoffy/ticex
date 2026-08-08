package moffy.ticex.datagen.general.recipes.avaritia;

import committee.nova.mods.avaritia.init.data.provider.recipe.ModShapedRecipeBuilder;
import committee.nova.mods.avaritia.init.registry.ModItems;
import moffy.ticex.TicEX;
import moffy.ticex.datagen.general.recipes.ITicEXSmelteryRecipeHelper;
import moffy.ticex.datagen.general.recipes.TicEXRecipeProvider;
import moffy.ticex.lib.TicEXMaterials;
import moffy.ticex.lib.TicEXTags;
import moffy.ticex.registry.TicEXFluids;
import moffy.ticex.registry.TicEXItems;
import moffy.ticex.registry.TicEXModifiers;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.recipe.IMaterialRecipeHelper;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialFluidRecipeBuilder;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelBuilder;
import slimeknights.tconstruct.library.recipe.melting.MaterialMeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;

import java.util.function.Consumer;

public class AvaritiaRecipeProvider implements ITicEXSmelteryRecipeHelper, IMaterialRecipeHelper {

    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> topConsumer = withCondition(
            pWriter,
            modsAvailable(TicEX.getResource("avaritia_compat"))
        );

        metalMaterialItemOptional(topConsumer, TicEXTags.Fluids.INFINITY, TicEXMaterials.INFINITY, 3180, true);
        metalMaterialItemOptional(topConsumer, TicEXTags.Fluids.NEUTRON, TicEXMaterials.NEUTRON, 2100, true);
        metalMaterialItemOptional(topConsumer, TicEXTags.Fluids.CRYSTAL_MATRIX, TicEXMaterials.CRYSTAL_MATRIX, 1880);
        metalMaterialItemOptional(topConsumer, TicEXTags.Fluids.BLAZING, TicEXMaterials.BLAZING, 1500);

        MeltingFuelBuilder.fuel(TicEXFluids.MOLTEN_BLAZING.ingredient(50), 150, 3500)
                .rate(55)
                .save(pWriter, prefix(TicEXFluids.MOLTEN_BLAZING, smelteryMeltingFolder + "fuel/"));

        // modifier
        if (TicEXModifiers.CELESTIAL_MODIFIER != null) {
            ModifierRecipeBuilder.modifier(TicEXModifiers.CELESTIAL_MODIFIER)
                .setTools(TinkerTags.Items.BOOTS)
                .addInput(TicEXItems.CELESTIAL_CORE.get())
                .setSlots(SlotType.DEFENSE, 2)
                .setMaxLevel(1)
                .checkTraitLevel()
                .saveSalvage(topConsumer, prefix(TicEXModifiers.CELESTIAL_MODIFIER.getId(), defenseSalvage))
                .save(topConsumer, prefix(TicEXModifiers.CELESTIAL_MODIFIER.getId(), defenseFolder));
        }

        if (TicEXModifiers.ENDESTSHOT_MODIFIER != null) {
            ModifierRecipeBuilder.modifier(TicEXModifiers.ENDESTSHOT_MODIFIER)
                .setTools(TinkerTags.Items.RANGED)
                .addInput(ModItems.endest_pearl.get())
                .setSlots(SlotType.ABILITY, 2)
                .setMaxLevel(1)
                .checkTraitLevel()
                .saveSalvage(topConsumer, prefix(TicEXModifiers.ENDESTSHOT_MODIFIER.getId(), abilitySalvage))
                .save(topConsumer, prefix(TicEXModifiers.ENDESTSHOT_MODIFIER.getId(), abilityFolder));
        }

        if(TicEXModifiers.ETERNITY_MODIFIER != null){
            ModifierRecipeBuilder.modifier(TicEXModifiers.ETERNITY_MODIFIER)
                    .setTools(TinkerTags.Items.DURABILITY)
                    .addInput(ModItems.enhancement_core.get())
                    .addInput(ModItems.upgrade_smithing_template.get())
                    .setLevelRange(1,3)
                    .allowCrystal()
                    .checkTraitLevel()
                    .save(topConsumer, prefix(TicEXModifiers.ETERNITY_MODIFIER, slotlessFolder));
        }

        // core

        if (TicEXItems.CELESTIAL_CORE != null) {
            ModShapedRecipeBuilder.shaped(RecipeCategory.MISC, TicEXItems.CELESTIAL_CORE.get(),1)
                    .showNotification(true)
                    .define('c', TicEXItems.RECONSTRUCTION_CORE.get())
                    .define('d', Items.DRAGON_HEAD)
                    .define('e', Items.ELYTRA)
                    .define('i', ModItems.infinity_catalyst.get())
                    .define('p', ModItems.endest_pearl.get())
                    .pattern(" d ")
                    .pattern("ice")
                    .pattern(" p ")
                    .unlockedBy("has_item", TicEXRecipeProvider.has(TicEXItems.RECONSTRUCTION_CORE.get()))
                    .save(topConsumer, prefix(TicEXItems.CELESTIAL_CORE.getId(), coresFolder));
        }
    }

    @Override
    public @NotNull String getModId() {
        return TicEX.MODID;
    }
}
