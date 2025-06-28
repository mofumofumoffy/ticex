package moffy.ticex.datagen.general.recipes.avaritia;

import committee.nova.mods.avaritia.init.data.provider.recipe.ModShapedRecipeBuilder;
import committee.nova.mods.avaritia.init.registry.ModItems;
import moffy.ticex.TicEX;
import moffy.ticex.datagen.general.recipes.ITicEXRecipeHelper;
import moffy.ticex.datagen.general.recipes.TicEXRecipeProvider;
import moffy.ticex.lib.TicEXMaterials;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.recipe.IMaterialRecipeHelper;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;

import java.util.function.Consumer;

public class AvaritiaRecipeProvider implements ITicEXRecipeHelper, IMaterialRecipeHelper {

    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> topConsumer = withCondition(
            pWriter,
            modsAvailable(new ResourceLocation(TicEX.MODID, "avaritia_compat"))
        );

        metalMaterialRecipe(topConsumer, TicEXMaterials.INFINITY, materialFolder, "infinity", true);
        metalMaterialRecipe(topConsumer, TicEXMaterials.NEUTRON, materialFolder, "neutron", true);
        metalMaterialRecipe(topConsumer, TicEXMaterials.CRYSTAL_MATRIX, materialFolder, "crystal_matrix", true);


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

        if (TicEXRegistry.CELESTIAL_CORE != null) {
            ModShapedRecipeBuilder.shaped(RecipeCategory.MISC, TicEXRegistry.CELESTIAL_CORE.get(), 1)
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
    }

    @Override
    public @NotNull String getModId() {
        return TicEX.MODID;
    }
}
