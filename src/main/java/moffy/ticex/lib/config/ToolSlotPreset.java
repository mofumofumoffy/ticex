package moffy.ticex.lib.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public record ToolSlotPreset(ResourceLocation rl, String name, int upgradeSlot, int abilitySlot, int defenseSlot) {
    public static List<ToolSlotPreset> PRESET = List.of(
            of("ticex", "blitz_gun", "Blitz Gun", 3, 1, 0),
            of("ticex", "meka_tool", "MekaEdge", 3, 1, 0),
            of("ticex", "mekaplate_boots", "Mekaplate Boots", 2, 0, 3),
            of("ticex", "mekaplate_chestplate", "Mekaplate Chestplate", 2, 0, 3),
            of("ticex", "mekaplate_helmet", "Mekaplate Helmet", 2, 0, 3),
            of("ticex", "mekaplate_leggings", "Mekaplate Leggings", 2, 0, 3),
            of("ticex", "reforged_slashblade", "Reforged Slashblade", 3, 1, 0),
            of("ticex", "revival_spellbook", "Revival Spellbook", 3, 1, 0),
            of("ticex", "singular_gem_boots", "Singular Gem Boots", 2, 0, 3),
            of("ticex", "singular_gem_chestplate", "Singular Gem Chestplate", 2, 0, 3),
            of("ticex", "singular_gem_helmet", "Singular Gem Helmet", 2, 0, 3),
            of("ticex", "singular_gem_leggings", "Singular Gem Leggings", 2, 0, 3),of("tconstruct", "battlesign", "battlesign", 1, 3, 0),
            of("tconstruct", "broad_axe", "Broad Axe", 1, 0, 2),
            of("tconstruct", "cleaver", "Cleaver", 1, 0, 2),
            of("tconstruct", "crossbow", "Crossbow", 1, 0, 3),
            of("tconstruct", "dagger", "Dagger", 1, 0, 3),
            of("tconstruct", "earth_staff", "Earth_staff", 2, 3, 2),
            of("tconstruct", "ender_staff", "Ender_staff", 2, 1, 3),
            of("tconstruct", "excavator", "Excavator", 1, 0, 2),
            of("tconstruct", "flint_and_brick", "Flint And Brick", 0, 0, 1),
            of("tconstruct", "hand_axe", "Hand Axe", 1, 0, 3),
            of("tconstruct", "ichor_staff", "Ichor Staff", 3, 0, 2),
            of("tconstruct", "kama", "Kama", 1, 0, 3),
            of("tconstruct", "longbow", "Longbow", 1, 0, 2),
            of("tconstruct", "mattock", "Mattock", 1, 0, 3),
            of("tconstruct", "melting_pan", "Melting Pan", 2, 1, 1),
            of("tconstruct", "pickadze", "Pickadze", 1, 0, 3),
            of("tconstruct", "pickaxe", "Pickaxe", 1, 0, 3),
            of("tconstruct", "plate_boots", "Plate Boots", 0, 3, 2),
            of("tconstruct", "plate_chestplate", "Plate Chestplate", 0, 3, 2),
            of("tconstruct", "plate_helmet", "Plate Helmet", 0, 3, 2),
            of("tconstruct", "plate_leggings", "Plate Leggings", 0, 3, 2),
            of("tconstruct", "plate_shield", "Plate Shield", 0, 3, 2),
            of("tconstruct", "scythe", "Scythe", 1, 0, 2),
            of("tconstruct", "sky_staff", "Sky Staff", 2, 0, 5),
            of("tconstruct", "sledge_hammer", "Sledge Hammer", 1, 0, 2),
            of("tconstruct", "slime_boots", "Slime Boots", 1, 0, 5),
            of("tconstruct", "slime_chestplate", "Slime Chestplate", 1, 0, 5),
            of("tconstruct", "slime_helmet", "Slime Helmet", 1, 0, 5),
            of("tconstruct", "slime_leggings", "Slime Leggings", 1, 0, 5),
            of("tconstruct", "swasher", "Swasher", 1, 0, 3),
            of("tconstruct", "sword", "Sword", 1, 0, 3),
            of("tconstruct", "travelers_boots", "Travelers Boots", 1, 2, 2),
            of("tconstruct", "travelers_chestplate", "Travelers Chestplate", 1, 2, 2),
            of("tconstruct", "travelers_helmet", "Travelers Helmet", 1, 2, 2),
            of("tconstruct", "travelers_leggings", "Travelers Leggings", 1, 2, 2),
            of("tconstruct", "travelers_shield", "Travelers Shield", 1, 2, 2),
            of("tconstruct", "vein_hammer", "Vein Hammer", 1, 0, 2),
            of("tconstruct", "war_pick", "War Pick", 1, 0, 3)
    );

    private static ToolSlotPreset of(String ns, String path, String name, int upgradeSlot, int abilitySlot, int defenseSlot) {
        return new ToolSlotPreset(new ResourceLocation(ns, path), name, upgradeSlot, abilitySlot, defenseSlot);
    }

    public String configName() {
        String noSpaces = name.replace(" ", "");
        if (noSpaces.isEmpty()) {
            return "";
        }
        return Character.toLowerCase(noSpaces.charAt(0)) + noSpaces.substring(1);
    }

    public interface SlotConfigSpec {
    }

    public record AbilitySlotConfigSpec(ForgeConfigSpec.ConfigValue<Integer> upgradeSlot, ForgeConfigSpec.ConfigValue<Integer> abilitySlot) implements SlotConfigSpec {
    }

    public record DefenseSlotConfigSpec(ForgeConfigSpec.ConfigValue<Integer> upgradeSlot, ForgeConfigSpec.ConfigValue<Integer> defenseSlot) implements SlotConfigSpec {
    }

    public record BothSlotConfigSpec(ForgeConfigSpec.ConfigValue<Integer> upgradeSlot, ForgeConfigSpec.ConfigValue<Integer> abilitySlot, ForgeConfigSpec.ConfigValue<Integer> defenseSlot) implements SlotConfigSpec {
    }

    public record NoUpgradeSlotConfigSpec(ForgeConfigSpec.ConfigValue<Integer> abilitySlot, ForgeConfigSpec.ConfigValue<Integer> defenseSlot) implements SlotConfigSpec {
    }

    public record DefenseOnlySlotConfigSpec(ForgeConfigSpec.ConfigValue<Integer> defenseSlot) implements SlotConfigSpec {
    }
}
