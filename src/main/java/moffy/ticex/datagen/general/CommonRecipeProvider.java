package moffy.ticex.datagen.general;

import java.util.function.Consumer;

import dan200.computercraft.shared.ModRegistry;
import committee.nova.mods.avaritia.init.registry.ModItems;
import moffy.addonapi.AddonAPI;
import moffy.addonapi.ModsAvailableCondition;
import moffy.ticex.TicEX;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import slimeknights.mantle.recipe.data.ICommonRecipeHelper;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.SlimeType;

public class CommonRecipeProvider extends RecipeProvider implements ICommonRecipeHelper,IConditionBuilder{

    public CommonRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    public String getModId() {
        return TicEX.MODID;
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        common(pWriter);
        computerCraft(pWriter);
        avaritia(pWriter);
    }

    public void common(Consumer<FinishedRecipe> pWriter){
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TicEXRegistry.RECONSTRUCTION_CORE.get())
            .define('c', TinkerCommons.slimeball.get(SlimeType.SKY))
            .define('a', Items.AMETHYST_SHARD)
            .define('s', Items.SHULKER_SHELL)
            .define('p', Items.BLAZE_POWDER)
            .pattern("asa")
            .pattern("pcp")
            .pattern("asa")
            .unlockedBy("has_item", has(TinkerCommons.slimeball.get(SlimeType.SKY)))
            .save(pWriter,"cores/reconstruction_core");

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TicEXRegistry.FLICKERING_RECONSTRUCTION_CORE.get())
            .define('c', TicEXRegistry.RECONSTRUCTION_CORE.get())
            .define('s', Items.NETHER_STAR)
            .pattern("ccc")
            .pattern("csc")
            .pattern("ccc")
            .unlockedBy("has_item", has(TicEXRegistry.RECONSTRUCTION_CORE.get()))
            .save(pWriter,"cores/flickering_reconstruction_core");
    }

    public void computerCraft(Consumer<FinishedRecipe> pWriter){
        String upgradeFolder = "tools/modifiers/upgrade/";
        String abilityFolder = "tools/modifiers/ability/";
        String slotlessFolder = "tools/modifiers/slotless/";
        String upgradeSalvage = "tools/modifiers/salvage/upgrade/";
        String abilitySalvage = "tools/modifiers/salvage/ability/";
        String defenseFolder = "tools/modifiers/defense/";
        String defenseSalvage = "tools/modifiers/salvage/defense/";
        String compatFolder = "tools/modifiers/compat/";
        String compatSalvage = "tools/modifiers/salvage/compat/";
        String worktableFolder = "tools/modifiers/worktable/";

        Consumer<FinishedRecipe> topConsumer = withCondition(pWriter, modsAvailable(new ResourceLocation(TicEX.MODID, "computercraft_compat")));
        
        if(TicEXRegistry.MODEM_MODIFIER != null){
            ModifierRecipeBuilder.modifier(TicEXRegistry.MODEM_MODIFIER)
                .setTools(TinkerTags.Items.CHESTPLATES)
                .addInput(ModRegistry.Items.WIRELESS_MODEM_ADVANCED.get())
                .setSlots(SlotType.UPGRADE, 1)
                .setMaxLevel(1).checkTraitLevel()
                .saveSalvage(topConsumer, prefix(TicEXRegistry.MODEM_MODIFIER.getId(), upgradeSalvage))
                .save(topConsumer, prefix(TicEXRegistry.MODEM_MODIFIER.getId(), upgradeFolder));
        }
    }

    public void avaritia(Consumer<FinishedRecipe> pWriter){
        String upgradeFolder = "tools/modifiers/upgrade/";
        String abilityFolder = "tools/modifiers/ability/";
        String slotlessFolder = "tools/modifiers/slotless/";
        String upgradeSalvage = "tools/modifiers/salvage/upgrade/";
        String abilitySalvage = "tools/modifiers/salvage/ability/";
        String defenseFolder = "tools/modifiers/defense/";
        String defenseSalvage = "tools/modifiers/salvage/defense/";
        String compatFolder = "tools/modifiers/compat/";
        String compatSalvage = "tools/modifiers/salvage/compat/";
        String worktableFolder = "tools/modifiers/worktable/";

        Consumer<FinishedRecipe> topConsumer = withCondition(pWriter, modsAvailable(new ResourceLocation(TicEX.MODID, "avaritia_compat")));

        if(TicEXRegistry.ENDESTSHOT_MODIFIER!= null){
            ModifierRecipeBuilder.modifier(TicEXRegistry.ENDESTSHOT_MODIFIER)
                .setTools(TinkerTags.Items.RANGED)
                .addInput(ModItems.endest_pearl.get())
                .setSlots(SlotType.ABILITY, 2)
                .setMaxLevel(1).checkTraitLevel()
                .saveSalvage(topConsumer, prefix(TicEXRegistry.ENDESTSHOT_MODIFIER.getId(), abilitySalvage))
                .save(topConsumer, prefix(TicEXRegistry.ENDESTSHOT_MODIFIER.getId(), abilityFolder));
        }
    }

    public ICondition modsAvailable(ResourceLocation rl){
        return new ModsAvailableCondition(new ResourceLocation(AddonAPI.MODID, "mods_available"), rl);
    }
}
