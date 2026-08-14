package moffy.ticex.datagen.recipes.mekanism;

import mekanism.api.datagen.recipe.builder.PressurizedReactionRecipeBuilder;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import mekanism.common.registries.MekanismGases;
import mekanism.common.registries.MekanismItems;
import meranha.mekaweapons.MekaWeapons;
import moffy.ticex.TicEX;
import moffy.ticex.datagen.recipes.ITicEXRecipeHelper;
import moffy.ticex.datagen.recipes.ticex.IEmbossmentToolRecipeHelper;
import moffy.ticex.datagen.recipes.ticex.builder.EmbossmentBuildingRecipeBuilder;
import moffy.ticex.datagen.recipes.ticex.builder.SingleEmbossmentModifierRecipeBuilder;
import moffy.ticex.lib.TicEXTags;
import moffy.ticex.registry.TicEXItems;
import moffy.ticex.registry.TicEXModifiers;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.crafting.DifferenceIngredient;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;

import java.util.function.Consumer;

public class MekanismRecipeProvider implements ITicEXRecipeHelper, IEmbossmentToolRecipeHelper {
    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> topConsumer = withCondition(
                pWriter,
                modsAvailable(TicEX.getResource("mekanism_compat"))
        );

        buildArmorRecipes(topConsumer);

        if(TicEXModifiers.MEKANIC_MODIFIER != null) {
            SingleEmbossmentModifierRecipeBuilder.modifier(TicEXModifiers.MEKANIC_MODIFIER.getId(), Ingredient.of(
                            TicEXItems.CATALYST_MEKASUIT.get(ArmorItem.Type.HELMET)
            ))
                    .setTools(TinkerTags.Items.HELMETS)
                    .save(topConsumer, prefix(TicEXModifiers.MEKANIC_MODIFIER, slotlessFolder).withSuffix("_helmet"));
            SingleEmbossmentModifierRecipeBuilder.modifier(TicEXModifiers.MEKANIC_MODIFIER.getId(), Ingredient.of(
                            TicEXItems.CATALYST_MEKASUIT.get(ArmorItem.Type.CHESTPLATE)
                    ))
                    .setTools(TinkerTags.Items.CHESTPLATES)
                    .save(topConsumer, prefix(TicEXModifiers.MEKANIC_MODIFIER, slotlessFolder).withSuffix("_chestplate"));
            SingleEmbossmentModifierRecipeBuilder.modifier(TicEXModifiers.MEKANIC_MODIFIER.getId(), Ingredient.of(
                            TicEXItems.CATALYST_MEKASUIT.get(ArmorItem.Type.LEGGINGS)
                    ))
                    .setTools(TinkerTags.Items.LEGGINGS)
                    .save(topConsumer, prefix(TicEXModifiers.MEKANIC_MODIFIER, slotlessFolder).withSuffix("_leggings"));
            SingleEmbossmentModifierRecipeBuilder.modifier(TicEXModifiers.MEKANIC_MODIFIER.getId(), Ingredient.of(
                            TicEXItems.CATALYST_MEKASUIT.get(ArmorItem.Type.BOOTS)
                    ))
                    .setTools(TinkerTags.Items.BOOTS)
                    .save(topConsumer, prefix(TicEXModifiers.MEKANIC_MODIFIER, slotlessFolder).withSuffix("_boots"));
            SingleEmbossmentModifierRecipeBuilder.modifier(TicEXModifiers.MEKANIC_MODIFIER.getId(), Ingredient.of(
                            TicEXItems.CATALYST_MEKA_TOOL.get()
                    ))
                    .setTools(TinkerTags.Items.HARVEST)
                    .save(topConsumer, prefix(TicEXModifiers.MEKANIC_MODIFIER, slotlessFolder).withSuffix("_harvest"));
            SingleEmbossmentModifierRecipeBuilder.modifier(TicEXModifiers.MEKANIC_MODIFIER.getId(), Ingredient.of(
                            TicEXItems.CATALYST_MEKA_TOOL.get()
                    ))
                    .setTools(TinkerTags.Items.MELEE_WEAPON)
                    .save(withCondition(topConsumer, new NotCondition(new ModLoadedCondition("mekaweapons"))), prefix(TicEXModifiers.MEKANIC_MODIFIER, slotlessFolder).withSuffix("_melee"));
        }

        if (TicEXItems.RADIATION_SHELDING_CORE != null) {
            PressurizedReactionRecipeBuilder.reaction(
                            IngredientCreatorAccess.item().from(TicEXItems.RECONSTRUCTION_CORE.get()),
                            IngredientCreatorAccess.fluid().from(Fluids.WATER, 1000),
                            IngredientCreatorAccess.gas().from(MekanismGases.POLONIUM, 1000),
                            100,
                            new ItemStack(TicEXItems.RADIATION_SHELDING_CORE.get()),
                            MekanismGases.SPENT_NUCLEAR_WASTE.getStack(1000))
                    .build(topConsumer, prefix(TicEXItems.RADIATION_SHELDING_CORE, coresFolder));
        }

        if (TicEXModifiers.RADIATION_SHIELDING_MODIFIER != null) {
            ModifierRecipeBuilder.modifier(TicEXModifiers.RADIATION_SHIELDING_MODIFIER)
                    .setTools(DifferenceIngredient.of(Ingredient.of(TinkerTags.Items.WORN_ARMOR), Ingredient.of(TicEXTags.Items.MEKASUIT_ARMOR)))
                    .addInput(TicEXItems.RADIATION_SHELDING_CORE.get())
                    .setSlots(SlotType.DEFENSE, 1)
                    .save(topConsumer, prefix(TicEXModifiers.RADIATION_SHIELDING_MODIFIER.getId(), defenseFolder));
        }

        if(TicEXItems.CATALYST_MEKA_TOOL != null){
            embossmentCasting(topConsumer, TicEXItems.CATALYST_MEKA_TOOL.get(), 1, MekanismItems.MEKA_TOOL.get(), true, prefix(TicEXItems.CATALYST_MEKA_TOOL.get().getStatType(), partsCastingFolder));
        }

        if (TicEXItems.MEKA_EDGE != null) {
            EmbossmentBuildingRecipeBuilder.buildingRecipe(TicEXItems.MEKA_EDGE.get())
                    .outputSize(1)
                    .save(topConsumer, prefix(TicEXItems.MEKA_EDGE, buildingFolder));
        }

        //weapons
        Consumer<FinishedRecipe> weaponsConsumer = withCondition(topConsumer, new ModLoadedCondition("mekaweapons"));
        if(TicEXItems.CATALYST_MEKA_TANA != null){
            embossmentCasting(weaponsConsumer, TicEXItems.CATALYST_MEKA_TANA.get(), 1, MekaWeapons.MEKA_TANA.get(), true, prefix(TicEXItems.CATALYST_MEKA_TANA.get().getStatType(), partsCastingFolder));
        }
        if(TicEXItems.CATALYST_MEKA_BOW != null){
            embossmentCasting(weaponsConsumer, TicEXItems.CATALYST_MEKA_BOW.get(), 1, MekaWeapons.MEKA_BOW.get(), true, prefix(TicEXItems.CATALYST_MEKA_BOW.get().getStatType(), partsCastingFolder));
        }
        SingleEmbossmentModifierRecipeBuilder.modifier(TicEXModifiers.MEKANIC_MODIFIER.getId(), Ingredient.of(
                        TicEXItems.CATALYST_MEKA_TANA.get()
                ))
                .setTools(TinkerTags.Items.MELEE_WEAPON)
                .save(weaponsConsumer, prefix(TicEXModifiers.MEKANIC_MODIFIER, slotlessFolder).withSuffix("_melee_weapons"));
        SingleEmbossmentModifierRecipeBuilder.modifier(TicEXModifiers.MEKANIC_MODIFIER.getId(), Ingredient.of(
                        TicEXItems.CATALYST_MEKA_BOW.get()
                ))
                .setTools(TinkerTags.Items.RANGED)
                .save(weaponsConsumer, prefix(TicEXModifiers.MEKANIC_MODIFIER, slotlessFolder).withSuffix("_ranged_weapons"));
    }

    public void buildArmorRecipes(Consumer<FinishedRecipe> topConsumer) {
        if(TicEXItems.MEKAPLATE_ARMOR != null) {
            embossmentCasting(topConsumer, TicEXItems.CATALYST_MEKASUIT.get(ArmorItem.Type.HELMET), 1, MekanismItems.MEKASUIT_HELMET.get(), true,
                    prefix(TicEXItems.CATALYST_MEKASUIT.get(ArmorItem.Type.HELMET).getStatType(), partsCastingFolder));
            embossmentCasting(topConsumer, TicEXItems.CATALYST_MEKASUIT.get(ArmorItem.Type.CHESTPLATE), 1, MekanismItems.MEKASUIT_BODYARMOR.get(), true,
                    prefix(TicEXItems.CATALYST_MEKASUIT.get(ArmorItem.Type.CHESTPLATE).getStatType(), partsCastingFolder));
            embossmentCasting(topConsumer, TicEXItems.CATALYST_MEKASUIT.get(ArmorItem.Type.LEGGINGS), 1, MekanismItems.MEKASUIT_PANTS.get(), true,
                    prefix(TicEXItems.CATALYST_MEKASUIT.get(ArmorItem.Type.LEGGINGS).getStatType(), partsCastingFolder));
            embossmentCasting(topConsumer, TicEXItems.CATALYST_MEKASUIT.get(ArmorItem.Type.BOOTS), 1, MekanismItems.MEKASUIT_BOOTS.get(), true,
                    prefix(TicEXItems.CATALYST_MEKASUIT.get(ArmorItem.Type.BOOTS).getStatType(), partsCastingFolder));
        }
    }
}
