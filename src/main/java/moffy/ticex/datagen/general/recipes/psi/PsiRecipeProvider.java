package moffy.ticex.datagen.general.recipes.psi;

import moffy.ticex.TicEX;
import moffy.ticex.datagen.general.recipes.ITicEXRecipeHelper;
import moffy.ticex.datagen.general.recipes.ticex.builder.SingleEmbossmentModifierRecipeBuilder;
import moffy.ticex.datagen.general.recipes.ticex.builder.ValidatableIncrementalModifierRecipeBuilder;
import moffy.ticex.modules.general.TicEXRegistry;
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

        if(TicEXRegistry.SENSOR_MODIFIER != null) {
            SingleEmbossmentModifierRecipeBuilder.modifier(TicEXRegistry.SENSOR_MODIFIER.getId(), Ingredient.of(
                    ModItems.exosuitSensorLight,
                    ModItems.exosuitSensorHeat,
                    ModItems.exosuitSensorStress,
                    ModItems.exosuitSensorWater,
                    ModItems.exosuitSensorTrigger
            ))
                    .setTools(TinkerTags.Items.HELMETS)
                    .save(topConsumer, prefix(TicEXRegistry.SENSOR_MODIFIER, slotlessFolder));
        }


        if(TicEXRegistry.SOCKET_MODIFIER != null) {
            ValidatableIncrementalModifierRecipeBuilder.modifier(TicEXRegistry.SOCKET_MODIFIER)
                    .allowCrystal()
                    .input(ModItems.cadSocketBasic, 1, 1)
                    .setMaxLevel(5)
                    .setSlots(SlotType.UPGRADE, 1)
                    .setTools(Ingredient.fromValues(Stream.of(
                            new Ingredient.TagValue(TinkerTags.Items.MELEE_WEAPON),
                            new Ingredient.TagValue(TinkerTags.Items.HARVEST),
                            new Ingredient.TagValue(TinkerTags.Items.ARMOR)
                    )))
                    .save(topConsumer, prefix(TicEXRegistry.SOCKET_MODIFIER, upgradeFolder));
        }

        if(TicEXRegistry.PSIONIZING_RADIATION_MODIFIER != null) {
            ModifierRecipeBuilder.modifier(TicEXRegistry.PSIONIZING_RADIATION_MODIFIER)
                    .setTools(Ingredient.fromValues(Stream.of(
                            new Ingredient.TagValue(TinkerTags.Items.MELEE_WEAPON),
                            new Ingredient.TagValue(TinkerTags.Items.HARVEST),
                            new Ingredient.TagValue(TinkerTags.Items.ARMOR)
                    )))
                    .addInput(TicEXRegistry.PSIONIZING_RADIATION_CORE.get())
                    .setSlots(SlotType.ABILITY, 1)
                    .setMaxLevel(1)
                    .checkTraitLevel()
                    .allowCrystal()
                    .save(topConsumer, prefix(TicEXRegistry.PSIONIZING_RADIATION_MODIFIER, abilityFolder));
        }

        if(TicEXRegistry.PSIONIZING_RADIATION_CORE != null) {
            TrickRecipeBuilder.of(TicEXRegistry.PSIONIZING_RADIATION_CORE.get())
                    .cad(ModItems.cadAssemblyPsimetal)
                    .input(TicEXRegistry.RECONSTRUCTION_CORE.get())
                    .trick(Psi.location(LibPieceNames.TRICK_GREATER_INFUSION))
                    .build(topConsumer, prefix(TicEXRegistry.PSIONIZING_RADIATION_CORE, coresFolder));
        }
    }
}
