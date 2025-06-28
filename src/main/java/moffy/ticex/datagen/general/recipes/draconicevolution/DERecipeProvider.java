package moffy.ticex.datagen.general.recipes.draconicevolution;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.datagen.FusionRecipeBuilder;
import com.brandon3055.draconicevolution.init.DEContent;
import moffy.ticex.TicEX;
import moffy.ticex.datagen.general.recipes.ITicEXRecipeHelper;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class DERecipeProvider implements ITicEXRecipeHelper {
    public void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        Consumer<FinishedRecipe> topConsumer = withCondition(
                pWriter,
                modsAvailable(new ResourceLocation(TicEX.MODID, "draconicevolution_compat"))
        );

        if(TicEXRegistry.DRACONIUM_EVOLVED_CORE != null) {
            FusionRecipeBuilder.builder(TicEXRegistry.DRACONIUM_EVOLVED_CORE.get(), 1, prefix(TicEXRegistry.DRACONIUM_EVOLVED_CORE, coresFolder))
                    .techLevel(TechLevel.DRACONIUM)
                    .energy(16000000)
                    .catalyst(TicEXRegistry.RECONSTRUCTION_CORE.get())
                    .ingredient(DEContent.CORE_DRACONIUM)
                    .ingredient(Items.DRAGON_BREATH)
                    .ingredient(Items.GOLDEN_APPLE)
                    .ingredient(Items.GOLDEN_APPLE)
                    .ingredient(Items.ENDER_EYE)
                    .ingredient(Items.ENDER_EYE)
                    .ingredient(Tags.Items.INGOTS_NETHERITE)
                    .ingredient(Tags.Items.INGOTS_NETHERITE)
                    .ingredient(Items.SHULKER_SHELL)
                    .ingredient(Items.SHULKER_SHELL)
                    .ingredient(DEContent.MODULE_CORE)
                    .build(topConsumer);
        }

        if(TicEXRegistry.WYVERN_EVOLVED_CORE != null) {
            FusionRecipeBuilder.builder(TicEXRegistry.WYVERN_EVOLVED_CORE.get(), 1, prefix(TicEXRegistry.WYVERN_EVOLVED_CORE, coresFolder))
                    .techLevel(TechLevel.WYVERN)
                    .energy(64000000)
                    .catalyst(TicEXRegistry.RECONSTRUCTION_CORE.get())
                    .ingredient(DEContent.CORE_WYVERN)
                    .ingredient(DEContent.CORE_DRACONIUM)
                    .ingredient(DEContent.CORE_DRACONIUM)
                    .ingredient(Items.NETHER_STAR)
                    .ingredient(Items.NETHER_STAR)
                    .ingredient(DEContent.SWORD_WYVERN)
                    .ingredient(DEContent.SHOVEL_WYVERN)
                    .ingredient(DEContent.PICKAXE_WYVERN)
                    .ingredient(DEContent.MODULE_CORE)
                    .build(topConsumer);
        }

        if (TicEXRegistry.DRACONIC_EVOLVED_CORE != null) {
            FusionRecipeBuilder.builder(TicEXRegistry.DRACONIC_EVOLVED_CORE.get(), 1, prefix(TicEXRegistry.DRACONIC_EVOLVED_CORE, coresFolder))
                    .techLevel(TechLevel.DRACONIC)
                    .energy(256000000)
                    .catalyst(TicEXRegistry.RECONSTRUCTION_CORE.get())
                    .ingredient(DEContent.CORE_WYVERN)
                    .ingredient(DEContent.CORE_AWAKENED)
                    .ingredient(Items.NETHER_STAR)
                    .ingredient(Items.NETHER_STAR)
                    .ingredient(Tags.Items.STORAGE_BLOCKS_NETHERITE)
                    .ingredient(Tags.Items.STORAGE_BLOCKS_NETHERITE)
                    .ingredient(DEContent.CORE_AWAKENED)
                    .ingredient(DEContent.SWORD_DRACONIC)
                    .ingredient(DEContent.SHOVEL_DRACONIC)
                    .ingredient(DEContent.PICKAXE_DRACONIC)
                    .ingredient(DEContent.MODULE_CORE)
                    .build(topConsumer);
        }

        if (TicEXRegistry.CHAOTIC_EVOLVED_CORE != null) {
            FusionRecipeBuilder.builder(TicEXRegistry.CHAOTIC_EVOLVED_CORE.get(), 1, prefix(TicEXRegistry.CHAOTIC_EVOLVED_CORE, coresFolder))
                    .techLevel(TechLevel.CHAOTIC)
                    .energy(1024000000)
                    .catalyst(TicEXRegistry.RECONSTRUCTION_CORE.get())
                    .ingredient(DEContent.CORE_WYVERN)
                    .ingredient(DEContent.CORE_WYVERN)
                    .ingredient(DEContent.CORE_AWAKENED)
                    .ingredient(DEContent.CORE_AWAKENED)
                    .ingredient(DEContent.CORE_CHAOTIC)
                    .ingredient(Items.DRAGON_EGG)
                    .ingredient(Items.DRAGON_EGG)
                    .ingredient(DEContent.SWORD_CHAOTIC)
                    .ingredient(DEContent.SHOVEL_CHAOTIC)
                    .ingredient(DEContent.PICKAXE_CHAOTIC)
                    .ingredient(DEContent.MODULE_CORE)
                    .build(topConsumer);
        }

        if (TicEXRegistry.INJECT_CORE != null) {
            FusionRecipeBuilder.builder(TicEXRegistry.INJECT_CORE.get(), 1, prefix(TicEXRegistry.INJECT_CORE, coresFolder))
                    .techLevel(TechLevel.CHAOTIC)
                    .energy(256000000)
                    .catalyst(TicEXRegistry.RECONSTRUCTION_CORE.get())
                    .ingredient(DEContent.CORE_DRACONIUM)
                    .ingredient(DEContent.CORE_WYVERN)
                    .ingredient(DEContent.CORE_AWAKENED)
                    .ingredient(DEContent.CORE_CHAOTIC)
                    .build(topConsumer);
        }
    }
}
