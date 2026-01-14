package moffy.ticex.datagen.general.recipes;

import moffy.addonapi.AddonAPI;
import moffy.addonapi.ModsAvailableCondition;
import moffy.ticex.TicEX;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.recipe.data.IRecipeHelper;

public interface ITicEXRecipeHelper extends IRecipeHelper, IConditionBuilder {
    // tools/modifiers/
    String upgradeFolder    = "tools/modifiers/upgrade/";
    String abilityFolder    = "tools/modifiers/ability/";
    String slotlessFolder   = "tools/modifiers/slotless/";
    String upgradeSalvage   = "tools/modifiers/salvage/upgrade/";
    String abilitySalvage   = "tools/modifiers/salvage/ability/";
    String defenseFolder    = "tools/modifiers/defense/";
    String defenseSalvage   = "tools/modifiers/salvage/defense/";
    String compatFolder     = "tools/modifiers/compat/";
    String compatSalvage    = "tools/modifiers/salvage/compat/";
    String worktableFolder  = "tools/modifiers/worktable/";

    // tools/parts/
    String partsFolder = "tools/parts/";
    String partsBuilderFolder      = "tools/parts/builder/";
    String partsCastingFolder = "tools/parts/casting/";

    // tools/armor/
    String armorFolder   = "tools/armor/";

    // tools/materials/
    String materialFolder   = "tools/materials/";
    String materialCastingFolder = "tools/materials/casting/";
    String materialMeltingFolder = "tools/materials/melting/";

    // tools/building/
    String buildingFolder   = "tools/building/";

    // smeltery/
    String alloysFolder   = "smeltery/alloys/";
    String smelteryCastingFolder   = "smeltery/casting/";
    String smelteryCastsFolder   = "smeltery/casts/";
    String smelteryMeltingFolder   = "smeltery/melting/";

    // items/
    String coresFolder      = "items/cores/";
    String itemsFolder      = "items/";

    default ICondition modsAvailable(ResourceLocation rl) {
        return new ModsAvailableCondition(ResourceLocation.fromNamespaceAndPath(AddonAPI.MODID, "mods_available"), rl);
    }

    default Item item(ResourceLocation resourceLocation) {
        return ForgeRegistries.ITEMS.getValue(resourceLocation);
    }

    @Override
    default @NotNull String getModId() {
        return TicEX.MODID;
    }
}
