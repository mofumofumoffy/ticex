package moffy.ticex;

import moffy.addonapi.AddonModuleRegistry;
import moffy.ticex.modules.general.TicEXModuleProvider;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;

import java.util.ArrayList;
import java.util.List;

public class TicEXConfig {

    public static ForgeConfigSpec.ConfigValue<Integer> RF_FURNACE_RATE_CAPACITY;
    public static ForgeConfigSpec.ConfigValue<Boolean> USE_SHADER;
    public static ForgeConfigSpec.ConfigValue<Float> CONDENSING_DROP_PROBABILITY;
    public static ForgeConfigSpec.ConfigValue<Boolean> MEKAPLATE_USE_POWER_SHIELD;
    public static ForgeConfigSpec.ConfigValue<Integer> OVERRIDE_LIMIT;
    public static ForgeConfigSpec.ConfigValue<Boolean> PROVIDE_PROPERTIES;
    public static List<ForgeConfigSpec.ConfigValue<Integer>> RF_FURNACE_FUEL_TEMP = new ArrayList<>();
    public static List<ForgeConfigSpec.ConfigValue<Integer>> RF_FURNACE_FUEL_RATE = new ArrayList<>();
    public static ForgeConfigSpec.ConfigValue<Integer> BLITZ_GUN_UPGRADE_SLOTS;
    public static ForgeConfigSpec.ConfigValue<Integer> BLITZ_GUN_ABILITY_SLOTS;
    public static ForgeConfigSpec.ConfigValue<Integer> MEKA_EDGE_UPGRADE_SLOTS;
    public static ForgeConfigSpec.ConfigValue<Integer> MEKA_EDGE_ABILITY_SLOTS;
    public static ForgeConfigSpec.ConfigValue<Integer> MEKAPLATE_BOOTS_UPGRADE_SLOTS;
    public static ForgeConfigSpec.ConfigValue<Integer> MEKAPLATE_BOOTS_DEFENSE_SLOTS;
    public static ForgeConfigSpec.ConfigValue<Integer> MEKAPLATE_CHESTPLATE_UPGRADE_SLOTS;
    public static ForgeConfigSpec.ConfigValue<Integer> MEKAPLATE_CHESTPLATE_DEFENSE_SLOTS;
    public static ForgeConfigSpec.ConfigValue<Integer> MEKAPLATE_HELMET_UPGRADE_SLOTS;
    public static ForgeConfigSpec.ConfigValue<Integer> MEKAPLATE_HELMET_DEFENSE_SLOTS;
    public static ForgeConfigSpec.ConfigValue<Integer> MEKAPLATE_LEGGINGS_UPGRADE_SLOTS;
    public static ForgeConfigSpec.ConfigValue<Integer> MEKAPLATE_LEGGINGS_DEFENSE_SLOTS;
    public static ForgeConfigSpec.ConfigValue<Integer> REFORGED_SLASHBLADE_UPGRADE_SLOTS;
    public static ForgeConfigSpec.ConfigValue<Integer> REFORGED_SLASHBLADE_ABILITY_SLOTS;
    public static ForgeConfigSpec.ConfigValue<Integer> REVIVAL_SPELLBOOK_UPGRADE_SLOTS;
    public static ForgeConfigSpec.ConfigValue<Integer> REVIVAL_SPELLBOOK_ABILITY_SLOTS;
    public static ForgeConfigSpec.ConfigValue<Integer> SINGULAR_GEM_BOOTS_UPGRADE_SLOTS;
    public static ForgeConfigSpec.ConfigValue<Integer> SINGULAR_GEM_BOOTS_DEFENSE_SLOTS;
    public static ForgeConfigSpec.ConfigValue<Integer> SINGULAR_GEM_CHESTPLATE_UPGRADE_SLOTS;
    public static ForgeConfigSpec.ConfigValue<Integer> SINGULAR_GEM_CHESTPLATE_DEFENSE_SLOTS;
    public static ForgeConfigSpec.ConfigValue<Integer> SINGULAR_GEM_HELMET_UPGRADE_SLOTS;
    public static ForgeConfigSpec.ConfigValue<Integer> SINGULAR_GEM_HELMET_DEFENSE_SLOTS;
    public static ForgeConfigSpec.ConfigValue<Integer> SINGULAR_GEM_LEGGINGS_UPGRADE_SLOTS;
    public static ForgeConfigSpec.ConfigValue<Integer> SINGULAR_GEM_LEGGINGS_DEFENSE_SLOTS;

    public static void registerConfig() {
        final ForgeConfigSpec.Builder COMMON = new ForgeConfigSpec.Builder();
        final ForgeConfigSpec.Builder CLIENT = new ForgeConfigSpec.Builder();
        final ForgeConfigSpec.Builder MORE_CONFIG = new ForgeConfigSpec.Builder();

        COMMON.comment("RFFurnace Settings").push("rf_furnace");
        RF_FURNACE_RATE_CAPACITY = COMMON.comment("MAX Rate Capacity(RF/t)").define("rateCapacity", 100000);
        COMMON.pop();

        COMMON.push("avaritia");
        CONDENSING_DROP_PROBABILITY = COMMON.comment(
            "Probability of a neutron pile is dropped by condensing modifier"
        ).define("condensingDropProbability", 0.003f);
        COMMON.pop();

        COMMON.push("mekanism");
        MEKAPLATE_USE_POWER_SHIELD = COMMON.comment("Allow Mekaplate can use shield of electricity").define(
            "mekaplateUseShield",
            true
        );
        COMMON.pop();

        COMMON.push("apotheosis");
        OVERRIDE_LIMIT = COMMON.comment("Maximum level of enchantments granted by override").define(
            "overrideLevelLimit",
            255
        );
        COMMON.pop();

        COMMON.push("cc:tweaked");
        PROVIDE_PROPERTIES = COMMON.comment(
            "",
            "CAUTION: Setting the value to \"true\" may BREAK the game balance and your world!"
        ).define("provideProperties", false);
        COMMON.pop();

        MORE_CONFIG.push("RF Furnace Fuel Temperature Settings");
        MORE_CONFIG.comment("These values are 32-bit signed integer. (Maximum value is about 2.147G)");
        int[] temps = new int[]{20, 90, 225, 402, 625, 902, 1230, 1603, 2026, 2494, 3036, 3594, 4240, 5095,
                5647, 6397, 7242, 8101, 9039, 10000};
        for (int i = 0; i < 20; i++) {
            RF_FURNACE_FUEL_TEMP.add(MORE_CONFIG.comment("Temperature of RF Furnace Fuel " + i)
                    .define("rfFuel" + i + "Temp", temps[i]));
        }
        MORE_CONFIG.pop();
        MORE_CONFIG.push("RF Furnace Fuel Speed Rate Settings");
        MORE_CONFIG.comment(
                "These values are 32-bit signed integer. (Maximum value is about 2.147G)",
                "The actual speed multiplier is calculated by dividing this value by 10.",
                "For example, setting it to 100 results in 10x speed, and 25 results in 2.5x speed."
        );
        for (int i = 0; i < 20; i++) {
            RF_FURNACE_FUEL_RATE.add(MORE_CONFIG.comment("Speed Rate of RF Furnace Fuel " + i)
                    .define("rfRate" + i + "Temp", i * 5 + 5));
        }
        MORE_CONFIG.pop();
        MORE_CONFIG.push("Tool/Armor Slots Settings");
        MORE_CONFIG.comment("These values are 32-bit signed integer. (Maximum value is about 2.147G)");
        BLITZ_GUN_UPGRADE_SLOTS = MORE_CONFIG.comment("Upgrade Slots of Blitz Gun.").define("blitzGunUpgradeSlots", 3);
        BLITZ_GUN_ABILITY_SLOTS = MORE_CONFIG.comment("Ability Slots of Blitz Gun.").define("blitzGunAbilitySlots", 1);
        MEKA_EDGE_UPGRADE_SLOTS = MORE_CONFIG.comment("Upgrade Slots of MekaEdge.").define("mekaEdgeUpgradeSlots", 3);
        MEKA_EDGE_ABILITY_SLOTS = MORE_CONFIG.comment("Ability Slots of MekaEdge.").define("mekaEdgeAbilitySlots", 1);
        MEKAPLATE_BOOTS_UPGRADE_SLOTS = MORE_CONFIG.comment("Upgrade Slots of Mekaplate Boots.").define("mekaplateBootsUpgradeSlots", 2);
        MEKAPLATE_BOOTS_DEFENSE_SLOTS = MORE_CONFIG.comment("Defense Slots of Mekaplate Boots.").define("mekaplateBootsDefenseSlots", 3);
        MEKAPLATE_CHESTPLATE_UPGRADE_SLOTS = MORE_CONFIG.comment("Upgrade Slots of Mekaplate Chestplate.").define("mekaplateChestplateUpgradeSlots", 2);
        MEKAPLATE_CHESTPLATE_DEFENSE_SLOTS = MORE_CONFIG.comment("Defense Slots of Mekaplate Chestplate.").define("mekaplateChestplateDefenseSlots", 3);
        MEKAPLATE_HELMET_UPGRADE_SLOTS = MORE_CONFIG.comment("Upgrade Slots of Mekaplate Helmet.").define("mekaplateHelmetUpgradeSlots", 2);
        MEKAPLATE_HELMET_DEFENSE_SLOTS = MORE_CONFIG.comment("Defense Slots of Mekaplate Helmet.").define("mekaplateHelmetDefenseSlots", 3);
        MEKAPLATE_LEGGINGS_UPGRADE_SLOTS = MORE_CONFIG.comment("Upgrade Slots of Mekaplate Leggings.").define("mekaplateLeggingsUpgradeSlots", 2);
        MEKAPLATE_LEGGINGS_DEFENSE_SLOTS = MORE_CONFIG.comment("Defense Slots of Mekaplate Leggings.").define("mekaplateLeggingsDefenseSlots", 3);
        REFORGED_SLASHBLADE_UPGRADE_SLOTS = MORE_CONFIG.comment("Upgrade Slots of Reforged Slashblade.").define("reforgedSlashbladeUpgradeSlots", 3);
        REFORGED_SLASHBLADE_ABILITY_SLOTS = MORE_CONFIG.comment("Ability Slots of Reforged Slashblade.").define("reforgedSlashbladeAbilitySlots", 1);
        REVIVAL_SPELLBOOK_UPGRADE_SLOTS = MORE_CONFIG.comment("Upgrade Slots of Revival Spellbook.").define("revivalSpellbookUpgradeSlots", 3);
        REVIVAL_SPELLBOOK_ABILITY_SLOTS = MORE_CONFIG.comment("Ability Slots of Revival Spellbook").define("revivalSpellbookAbilitySlots", 1);
        SINGULAR_GEM_BOOTS_UPGRADE_SLOTS = MORE_CONFIG.comment("Upgrade Slots of Singular Gem Boots.").define("singularGemBootsUpgradeSlots", 2);
        SINGULAR_GEM_BOOTS_DEFENSE_SLOTS = MORE_CONFIG.comment("Defense Slots of Singular Gem Boots.").define("singularGemBootsDefenseSlots", 3);
        SINGULAR_GEM_CHESTPLATE_UPGRADE_SLOTS = MORE_CONFIG.comment("Upgrade Slots of Singular Gem Chestplate.").define("singularGemChestplateUpgradeSlots", 2);
        SINGULAR_GEM_CHESTPLATE_DEFENSE_SLOTS = MORE_CONFIG.comment("Defense Slots of Singular Gem Chestplate.").define("singularGemChestplateDefenseSlots", 3);
        SINGULAR_GEM_HELMET_UPGRADE_SLOTS = MORE_CONFIG.comment("Upgrade Slots of Singular Gem Helmet.").define("singularGemHelmetUpgradeSlots", 2);
        SINGULAR_GEM_HELMET_DEFENSE_SLOTS = MORE_CONFIG.comment("Defense Slots of Singular Gem Helmet.").define("singularGemHelmetDefenseSlots", 3);
        SINGULAR_GEM_LEGGINGS_UPGRADE_SLOTS = MORE_CONFIG.comment("Upgrade Slots of Singular Gem Leggings.").define("singularGemLeggingsUpgradeSlots", 2);
        SINGULAR_GEM_LEGGINGS_DEFENSE_SLOTS = MORE_CONFIG.comment("Defense Slots of Singular Gem Leggings.").define("singularGemLeggingsDefenseSlots", 3);
        MORE_CONFIG.pop();

        CLIENT.comment("Client Settings").push("client");
        USE_SHADER = CLIENT.comment("Rendering with shaders for some tools/armors").define("useShader", true);
        CLIENT.pop();

        AddonModuleRegistry.INSTANCE.LoadModule(new TicEXModuleProvider(), COMMON);

        ModLoadingContext.get().registerConfig(Type.COMMON, COMMON.build());
        ModLoadingContext.get().registerConfig(Type.COMMON, MORE_CONFIG.build(), "ticex-more-config.toml");
        ModLoadingContext.get().registerConfig(Type.CLIENT, CLIENT.build());
    }
}
