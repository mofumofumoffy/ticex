package moffy.ticex.datagen.general.recipes.botania;

import com.google.gson.JsonObject;
import moffy.ticex.TicEX;
import moffy.ticex.datagen.general.recipes.ITicEXSmelteryRecipeHelper;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.recipe.IMaterialRecipeHelper;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;
import vazkii.botania.api.recipe.ManaInfusionRecipe;
import vazkii.botania.common.item.BotaniaItems;


import java.util.function.Consumer;

public class BotaniaRecipeProvider implements ITicEXSmelteryRecipeHelper, IMaterialRecipeHelper {
    public void buildRecipes(@NotNull Consumer<FinishedRecipe> pWriter){
        Consumer<FinishedRecipe> topConsumer = withCondition(
                pWriter,
                modsAvailable(new ResourceLocation(TicEX.MODID, "botania_compat"))
        );

        ModifierRecipeBuilder.modifier(TicEXRegistry.AHRIM_MODIFIER)
                .allowCrystal()
                .setTools(TinkerTags.Items.HELMETS)
                .addInput(BotaniaItems.ancientWillAhrim,1)
                .setSlots(SlotType.DEFENSE,1)
                .setMaxLevel(1)
                .checkTraitLevel()
                .saveSalvage(topConsumer,prefix(TicEXRegistry.AHRIM_MODIFIER.getId(),defenseSalvage))
                .save(topConsumer,prefix(TicEXRegistry.AHRIM_MODIFIER.getId(),defenseFolder));
        ModifierRecipeBuilder.modifier(TicEXRegistry.DHAROK_MODIFIER)
                .allowCrystal()
                .setTools(TinkerTags.Items.HELMETS)
                .addInput(BotaniaItems.ancientWillDharok,1)
                .setSlots(SlotType.DEFENSE,1)
                .setMaxLevel(1)
                .checkTraitLevel()
                .saveSalvage(topConsumer,prefix(TicEXRegistry.DHAROK_MODIFIER.getId(),defenseSalvage))
                .save(topConsumer,prefix(TicEXRegistry.DHAROK_MODIFIER.getId(),defenseFolder));
        ModifierRecipeBuilder.modifier(TicEXRegistry.GUTHAN_MODIFIER)
                .allowCrystal()
                .setTools(TinkerTags.Items.HELMETS)
                .addInput(BotaniaItems.ancientWillGuthan,1)
                .setSlots(SlotType.DEFENSE,1)
                .setMaxLevel(1)
                .checkTraitLevel()
                .saveSalvage(topConsumer,prefix(TicEXRegistry.GUTHAN_MODIFIER.getId(),defenseSalvage))
                .save(topConsumer,prefix(TicEXRegistry.GUTHAN_MODIFIER.getId(),defenseFolder));
        ModifierRecipeBuilder.modifier(TicEXRegistry.TORAG_MODIFIER)
                .allowCrystal()
                .setTools(TinkerTags.Items.HELMETS)
                .addInput(BotaniaItems.ancientWillTorag,1)
                .setSlots(SlotType.DEFENSE,1)
                .setMaxLevel(1)
                .checkTraitLevel()
                .saveSalvage(topConsumer,prefix(TicEXRegistry.TORAG_MODIFIER.getId(),defenseSalvage))
                .save(topConsumer,prefix(TicEXRegistry.TORAG_MODIFIER.getId(),defenseFolder));
        ModifierRecipeBuilder.modifier(TicEXRegistry.VERAC_MODIFIER)
                .allowCrystal()
                .setTools(TinkerTags.Items.HELMETS)
                .addInput(BotaniaItems.ancientWillVerac,1)
                .setSlots(SlotType.DEFENSE,1)
                .setMaxLevel(1)
                .checkTraitLevel()
                .saveSalvage(topConsumer,prefix(TicEXRegistry.VERAC_MODIFIER.getId(),defenseSalvage))
                .save(topConsumer,prefix(TicEXRegistry.VERAC_MODIFIER.getId(),defenseFolder));
        ModifierRecipeBuilder.modifier(TicEXRegistry.KARIL_MODIFIER)
                .allowCrystal()
                .setTools(TinkerTags.Items.HELMETS)
                .addInput(BotaniaItems.ancientWillKaril,1)
                .setSlots(SlotType.DEFENSE,1)
                .setMaxLevel(1)
                .checkTraitLevel()
                .saveSalvage(topConsumer,prefix(TicEXRegistry.KARIL_MODIFIER.getId(),defenseSalvage))
                .save(topConsumer,prefix(TicEXRegistry.KARIL_MODIFIER.getId(),defenseFolder));
        ModifierRecipeBuilder.modifier(TicEXRegistry.NECTAR_MODIFIER)
                .allowCrystal()
                .setTools(TinkerTags.Items.ARMOR)
                .addInput(TicEXRegistry.NECTAR_CORE.get(),1)
                .setMaxLevel(4)
                .checkTraitLevel()
                .save(topConsumer,prefix(TicEXRegistry.NECTAR_MODIFIER.getId(),slotlessFolder));

    }
}
