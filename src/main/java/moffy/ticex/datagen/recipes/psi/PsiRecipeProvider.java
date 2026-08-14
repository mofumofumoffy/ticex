package moffy.ticex.datagen.recipes.psi;

import moffy.ticex.TicEX;
import moffy.ticex.datagen.recipes.ITicEXRecipeHelper;
import moffy.ticex.datagen.recipes.ticex.builder.SingleEmbossmentModifierRecipeBuilder;
import moffy.ticex.datagen.recipes.ticex.builder.ValidatableIncrementalModifierRecipeBuilder;
import moffy.ticex.registry.TicEXItems;
import moffy.ticex.registry.TicEXModifiers;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;
import vazkii.psi.api.recipe.TrickRecipeBuilder;
import vazkii.psi.common.Psi;
import vazkii.psi.common.item.base.ModItems;
import vazkii.psi.common.lib.LibPieceNames;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class PsiRecipeProvider implements ITicEXRecipeHelper {
    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> topConsumer = withCondition(
            pWriter,
            modsAvailable(TicEX.getResource("psi_compat"))
        );

        if(TicEXModifiers.SENSOR_MODIFIER != null) {
            SingleEmbossmentModifierRecipeBuilder.modifier(TicEXModifiers.SENSOR_MODIFIER.getId(), Ingredient.of(
                    ModItems.exosuitSensorLight,
                    ModItems.exosuitSensorHeat,
                    ModItems.exosuitSensorStress,
                    ModItems.exosuitSensorWater,
                    ModItems.exosuitSensorTrigger
            ))
                    .setTools(TinkerTags.Items.HELMETS)
                    .save(topConsumer, prefix(TicEXModifiers.SENSOR_MODIFIER, slotlessFolder));
        }


        if(TicEXModifiers.SOCKET_MODIFIER != null) {
            ValidatableIncrementalModifierRecipeBuilder.modifier(TicEXModifiers.SOCKET_MODIFIER)
                    .allowCrystal()
                    .input(ModItems.cadSocketBasic, 1, 1)
                    .setMaxLevel(5)
                    .setSlots(SlotType.UPGRADE, 1)
                    .setTools(Ingredient.fromValues(Stream.of(
                            new Ingredient.TagValue(TinkerTags.Items.MELEE_WEAPON),
                            new Ingredient.TagValue(TinkerTags.Items.HARVEST),
                            new Ingredient.TagValue(TinkerTags.Items.ARMOR)
                    )))
                    .save(topConsumer, prefix(TicEXModifiers.SOCKET_MODIFIER, upgradeFolder));
        }

        if(TicEXModifiers.PSIONIZING_RADIATION_MODIFIER != null) {
            ModifierRecipeBuilder.modifier(TicEXModifiers.PSIONIZING_RADIATION_MODIFIER)
                    .setTools(Ingredient.fromValues(Stream.of(
                            new Ingredient.TagValue(TinkerTags.Items.MELEE_WEAPON),
                            new Ingredient.TagValue(TinkerTags.Items.HARVEST),
                            new Ingredient.TagValue(TinkerTags.Items.ARMOR)
                    )))
                    .addInput(TicEXItems.PSIONIZING_RADIATION_CORE.get())
                    .setSlots(SlotType.ABILITY, 1)
                    .setMaxLevel(1)
                    .checkTraitLevel()
                    .allowCrystal()
                    .save(topConsumer, prefix(TicEXModifiers.PSIONIZING_RADIATION_MODIFIER, abilityFolder));
        }

        if(TicEXItems.PSIONIZING_RADIATION_CORE != null) {
            TrickRecipeBuilder.of(TicEXItems.PSIONIZING_RADIATION_CORE.get())
                    .cad(ModItems.cadAssemblyPsimetal)
                    .input(TicEXItems.RECONSTRUCTION_CORE.get())
                    .trick(Psi.location(LibPieceNames.TRICK_GREATER_INFUSION))
                    .build(topConsumer, prefix(TicEXItems.PSIONIZING_RADIATION_CORE, coresFolder));
        }
    }
}
