package moffy.ticex.datagen.general.recipes;

import moffy.addonapi.AddonAPI;
import moffy.addonapi.ModsAvailableCondition;
import moffy.ticex.TicEX;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import slimeknights.mantle.recipe.data.IRecipeHelper;

public interface ITicEXRecipeHelper extends IRecipeHelper, IConditionBuilder {
    public String upgradeFolder = "tools/modifiers/upgrade/";
    public String abilityFolder = "tools/modifiers/ability/";
    public String slotlessFolder = "tools/modifiers/slotless/";
    public String upgradeSalvage = "tools/modifiers/salvage/upgrade/";
    public String abilitySalvage = "tools/modifiers/salvage/ability/";
    public String defenseFolder = "tools/modifiers/defense/";
    public String defenseSalvage = "tools/modifiers/salvage/defense/";
    public String compatFolder = "tools/modifiers/compat/";
    public String compatSalvage = "tools/modifiers/salvage/compat/";
    public String worktableFolder = "tools/modifiers/worktable/";
    public String materialFolder = "tools/materials/";

    public default ICondition modsAvailable(ResourceLocation rl) {
        return new ModsAvailableCondition(new ResourceLocation(AddonAPI.MODID, "mods_available"), rl);
    }

    @Override
    default String getModId() {
        return TicEX.MODID;
    }
}
