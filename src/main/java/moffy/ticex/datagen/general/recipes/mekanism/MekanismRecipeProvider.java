package moffy.ticex.datagen.general.recipes.mekanism;

import mekanism.api.datagen.recipe.builder.PressurizedReactionRecipeBuilder;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.registries.MekanismGases;
import mekanism.common.registries.MekanismItems;
import moffy.ticex.TicEX;
import moffy.ticex.datagen.general.recipes.ITicEXRecipeHelper;
import moffy.ticex.datagen.general.recipes.ticex.IEmbossmentToolRecipeHelper;
import moffy.ticex.datagen.general.recipes.ticex.embossment.SingleEmbossmentModifierRecipeBuilder;
import moffy.ticex.lib.TicEXTags;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.crafting.DifferenceIngredient;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;

import java.util.function.Consumer;

public class MekanismRecipeProvider implements ITicEXRecipeHelper, IEmbossmentToolRecipeHelper {
    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> topConsumer = withCondition(
                pWriter,
                modsAvailable(new ResourceLocation(TicEX.MODID, "mekanism_compat"))
        );

        buildArmorRecipes(topConsumer);

        if(TicEXRegistry.MEKANIC_MODIFIER != null) {
            SingleEmbossmentModifierRecipeBuilder.modifier(TicEXRegistry.MEKANIC_MODIFIER.getId(), Ingredient.of(
                    TicEXRegistry.CATALYST_MEKASUIT.get(ArmorItem.Type.HELMET),
                    TicEXRegistry.CATALYST_MEKASUIT.get(ArmorItem.Type.CHESTPLATE),
                    TicEXRegistry.CATALYST_MEKASUIT.get(ArmorItem.Type.LEGGINGS),
                    TicEXRegistry.CATALYST_MEKASUIT.get(ArmorItem.Type.BOOTS)
            ))
                    .setTools(TicEXTags.Items.PLATE)
                    .save(topConsumer, prefix(TicEXRegistry.MEKANIC_MODIFIER, slotlessFolder));
        }

        if (TicEXRegistry.RADIATION_SHELDING_CORE != null) {
            PressurizedReactionRecipeBuilder.reaction(
                            IngredientCreatorAccess.item().from(TicEXRegistry.RECONSTRUCTION_CORE.get()),
                            IngredientCreatorAccess.fluid().from(Fluids.WATER, 1000),
                            IngredientCreatorAccess.gas().from(MekanismGases.POLONIUM, 1000),
                            100,
                            new ItemStack(TicEXRegistry.RADIATION_SHELDING_CORE.get()),
                            MekanismGases.SPENT_NUCLEAR_WASTE.getStack(1000))
                    .build(topConsumer, prefix(TicEXRegistry.RADIATION_SHELDING_CORE, coresFolder));
        }

        if (TicEXRegistry.RADIATION_SHIELDING_MODIFIER != null) {
            ModifierRecipeBuilder.modifier(TicEXRegistry.RADIATION_SHIELDING_MODIFIER)
                    .setTools(DifferenceIngredient.of(Ingredient.of(TinkerTags.Items.WORN_ARMOR), Ingredient.of(TicEXTags.Items.MEKASUIT_ARMOR)))
                    .addInput(TicEXRegistry.RADIATION_SHELDING_CORE.get())
                    .setSlots(SlotType.DEFENSE, 1)
                    .save(topConsumer, prefix(TicEXRegistry.RADIATION_SHIELDING_MODIFIER.getId(), defenseFolder));
        }
    }

    public void buildArmorRecipes(Consumer<FinishedRecipe> topConsumer) {
        if(TicEXRegistry.MEKAPLATE_ARMOR != null) {
            ResourceLocation seramGear = new ResourceLocation(TicEX.MODID, "seram_gear");
            embossmentBuilding(topConsumer, TicEXRegistry.MEKAPLATE_ARMOR.get(ArmorItem.Type.HELMET), armorFolder, seramGear);
            embossmentBuilding(topConsumer, TicEXRegistry.MEKAPLATE_ARMOR.get(ArmorItem.Type.CHESTPLATE), armorFolder, seramGear);
            embossmentBuilding(topConsumer, TicEXRegistry.MEKAPLATE_ARMOR.get(ArmorItem.Type.LEGGINGS), armorFolder, seramGear);
            embossmentBuilding(topConsumer, TicEXRegistry.MEKAPLATE_ARMOR.get(ArmorItem.Type.BOOTS), armorFolder, seramGear);

            embossmentCasting(topConsumer, TicEXRegistry.CATALYST_MEKASUIT.get(ArmorItem.Type.HELMET), 1, MekanismItems.MEKASUIT_HELMET.get(), true,
                    prefix(TicEXRegistry.CATALYST_MEKASUIT.get(ArmorItem.Type.HELMET).getStatType(), partsCastingFolder));
            embossmentCasting(topConsumer, TicEXRegistry.CATALYST_MEKASUIT.get(ArmorItem.Type.CHESTPLATE), 1, MekanismItems.MEKASUIT_BODYARMOR.get(), true,
                    prefix(TicEXRegistry.CATALYST_MEKASUIT.get(ArmorItem.Type.CHESTPLATE).getStatType(), partsCastingFolder));
            embossmentCasting(topConsumer, TicEXRegistry.CATALYST_MEKASUIT.get(ArmorItem.Type.LEGGINGS), 1, MekanismItems.MEKASUIT_PANTS.get(), true,
                    prefix(TicEXRegistry.CATALYST_MEKASUIT.get(ArmorItem.Type.LEGGINGS).getStatType(), partsCastingFolder));
            embossmentCasting(topConsumer, TicEXRegistry.CATALYST_MEKASUIT.get(ArmorItem.Type.BOOTS), 1, MekanismItems.MEKASUIT_BOOTS.get(), true,
                    prefix(TicEXRegistry.CATALYST_MEKASUIT.get(ArmorItem.Type.BOOTS).getStatType(), partsCastingFolder));
        }
    }
}
