package moffy.ticex.lib.config;

import java.util.Optional;

public class SlotValues {
    public Integer abilities;
    public Integer defense;
    public Integer upgrades;

     public static SlotValues fromSpec(Object spec) {
        SlotValues values = new SlotValues();
        if (spec instanceof ToolSlotPreset.BothSlotConfigSpec s) {
            values.abilities = s.abilitySlot().get();
            values.defense = s.defenseSlot().get();
            values.upgrades = s.upgradeSlot().get();
        } else if (spec instanceof ToolSlotPreset.AbilitySlotConfigSpec s) {
            values.abilities = s.abilitySlot().get();
            values.upgrades = s.upgradeSlot().get();
        } else if (spec instanceof ToolSlotPreset.DefenseSlotConfigSpec s) {
            values.defense = s.defenseSlot().get();
            values.upgrades = s.upgradeSlot().get();
        } else if (spec instanceof ToolSlotPreset.NoUpgradeSlotConfigSpec s) {
            values.abilities = s.abilitySlot().get();
            values.defense = s.defenseSlot().get();
        } else if (spec instanceof ToolSlotPreset.DefenseOnlySlotConfigSpec s) {
            values.defense = s.defenseSlot().get();
        } else {
            return null;
        }
        return values;
    }
}
