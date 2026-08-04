package moffy.ticex.datagen.general.recipes.botania;

import moffy.ticex.TicEX;
import moffy.ticex.datagen.general.recipes.ITicEXSmelteryRecipeHelper;
import moffy.ticex.registry.TicEXItems;
import moffy.ticex.registry.TicEXModifiers;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.recipe.IMaterialRecipeHelper;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;
import vazkii.botania.common.item.BotaniaItems;

import java.util.function.Consumer;

public class BotaniaRecipeProvider implements ITicEXSmelteryRecipeHelper, IMaterialRecipeHelper {
    private final TicEXManaInfusionProvider manaInfusionProvider;

    public BotaniaRecipeProvider(PackOutput output) {
        this.manaInfusionProvider = new TicEXManaInfusionProvider(output);
    }

    public void buildRecipes(@NotNull Consumer<FinishedRecipe> pWriter){
        Consumer<FinishedRecipe> topConsumer = withCondition(
                pWriter,
                modsAvailable(TicEX.getResource("botania_compat"))
        );

        manaInfusionProvider.buildRecipes(pWriter);

        ModifierRecipeBuilder.modifier(TicEXModifiers.AHRIM_MODIFIER)
                .allowCrystal()
                .setTools(TinkerTags.Items.HELMETS)
                .addInput(BotaniaItems.ancientWillAhrim,1)
                .setSlots(SlotType.DEFENSE,1)
                .setMaxLevel(1)
                .checkTraitLevel()
                .saveSalvage(topConsumer,prefix(TicEXModifiers.AHRIM_MODIFIER.getId(),defenseSalvage))
                .save(topConsumer,prefix(TicEXModifiers.AHRIM_MODIFIER.getId(),defenseFolder));
        ModifierRecipeBuilder.modifier(TicEXModifiers.DHAROK_MODIFIER)
                .allowCrystal()
                .setTools(TinkerTags.Items.HELMETS)
                .addInput(BotaniaItems.ancientWillDharok,1)
                .setSlots(SlotType.DEFENSE,1)
                .setMaxLevel(1)
                .checkTraitLevel()
                .saveSalvage(topConsumer,prefix(TicEXModifiers.DHAROK_MODIFIER.getId(),defenseSalvage))
                .save(topConsumer,prefix(TicEXModifiers.DHAROK_MODIFIER.getId(),defenseFolder));
        ModifierRecipeBuilder.modifier(TicEXModifiers.GUTHAN_MODIFIER)
                .allowCrystal()
                .setTools(TinkerTags.Items.HELMETS)
                .addInput(BotaniaItems.ancientWillGuthan,1)
                .setSlots(SlotType.DEFENSE,1)
                .setMaxLevel(1)
                .checkTraitLevel()
                .saveSalvage(topConsumer,prefix(TicEXModifiers.GUTHAN_MODIFIER.getId(),defenseSalvage))
                .save(topConsumer,prefix(TicEXModifiers.GUTHAN_MODIFIER.getId(),defenseFolder));
        ModifierRecipeBuilder.modifier(TicEXModifiers.TORAG_MODIFIER)
                .allowCrystal()
                .setTools(TinkerTags.Items.HELMETS)
                .addInput(BotaniaItems.ancientWillTorag,1)
                .setSlots(SlotType.DEFENSE,1)
                .setMaxLevel(1)
                .checkTraitLevel()
                .saveSalvage(topConsumer,prefix(TicEXModifiers.TORAG_MODIFIER.getId(),defenseSalvage))
                .save(topConsumer,prefix(TicEXModifiers.TORAG_MODIFIER.getId(),defenseFolder));
        ModifierRecipeBuilder.modifier(TicEXModifiers.VERAC_MODIFIER)
                .allowCrystal()
                .setTools(TinkerTags.Items.HELMETS)
                .addInput(BotaniaItems.ancientWillVerac,1)
                .setSlots(SlotType.DEFENSE,1)
                .setMaxLevel(1)
                .checkTraitLevel()
                .saveSalvage(topConsumer,prefix(TicEXModifiers.VERAC_MODIFIER.getId(),defenseSalvage))
                .save(topConsumer,prefix(TicEXModifiers.VERAC_MODIFIER.getId(),defenseFolder));
        ModifierRecipeBuilder.modifier(TicEXModifiers.KARIL_MODIFIER)
                .allowCrystal()
                .setTools(TinkerTags.Items.HELMETS)
                .addInput(BotaniaItems.ancientWillKaril,1)
                .setSlots(SlotType.DEFENSE,1)
                .setMaxLevel(1)
                .checkTraitLevel()
                .saveSalvage(topConsumer,prefix(TicEXModifiers.KARIL_MODIFIER.getId(),defenseSalvage))
                .save(topConsumer,prefix(TicEXModifiers.KARIL_MODIFIER.getId(),defenseFolder));
        ModifierRecipeBuilder.modifier(TicEXModifiers.NECTAR_MODIFIER)
                .allowCrystal()
                .setTools(TinkerTags.Items.ARMOR)
                .addInput(TicEXItems.NECTAR_CORE.get(),1)
                .setMaxLevel(4)
                .checkTraitLevel()
                .save(topConsumer,prefix(TicEXModifiers.NECTAR_MODIFIER.getId(),slotlessFolder));

    }
}
