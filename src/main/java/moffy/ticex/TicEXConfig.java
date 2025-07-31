package moffy.ticex;

import moffy.addonapi.AddonModuleRegistry;
import moffy.ticex.lib.config.ModifierLevelPreset;
import moffy.ticex.lib.config.ToolSlotPreset;
import moffy.ticex.modules.general.TicEXModuleProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicEXConfig {

    public static ForgeConfigSpec.ConfigValue<Integer> RF_FURNACE_RATE_CAPACITY;
    public static ForgeConfigSpec.ConfigValue<Boolean> USE_SHADER;
    public static ForgeConfigSpec.ConfigValue<Float> CONDENSING_DROP_PROBABILITY;
    public static ForgeConfigSpec.ConfigValue<Boolean> MEKAPLATE_USE_POWER_SHIELD;
    public static ForgeConfigSpec.ConfigValue<Integer> OVERRIDE_LIMIT;
    public static ForgeConfigSpec.ConfigValue<Boolean> PROVIDE_PROPERTIES;
    public static List<ForgeConfigSpec.ConfigValue<Integer>> RF_FURNACE_FUEL_TEMP = new ArrayList<>();
    public static List<ForgeConfigSpec.ConfigValue<Integer>> RF_FURNACE_FUEL_RATE = new ArrayList<>();
    public static Map<ResourceLocation, ToolSlotPreset.SlotConfigSpec> SLOTS_CONFIG = new HashMap<>();
    public static Map<ResourceLocation, ForgeConfigSpec.ConfigValue<Integer>> MODIFIER_CONFIG = new HashMap<>();

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
        ToolSlotPreset.PRESET.forEach(preset -> {
            ToolSlotPreset.SlotConfigSpec spec = null;
            if (preset.upgradeSlot() > 0) {
                if (preset.abilitySlot() > 0) {
                    if (preset.defenseSlot() > 0) {
                        spec = new ToolSlotPreset.BothSlotConfigSpec(
                                MORE_CONFIG.comment("Upgrade Slots of " + preset.name()).define(preset.configName() + "UpgradeSlots", preset.upgradeSlot()),
                                MORE_CONFIG.comment("Ability Slots of " + preset.name()).define(preset.configName() + "AbilitySlots", preset.abilitySlot()),
                                MORE_CONFIG.comment("Defense Slots of " + preset.name()).define(preset.configName() + "DefenseSlots", preset.defenseSlot())
                        );
                    } else {
                        spec = new ToolSlotPreset.AbilitySlotConfigSpec(
                                MORE_CONFIG.comment("Upgrade Slots of " + preset.name()).define(preset.configName() + "UpgradeSlots", preset.upgradeSlot()),
                                MORE_CONFIG.comment("Ability Slots of " + preset.name()).define(preset.configName() + "AbilitySlots", preset.abilitySlot())
                        );
                    }
                } else {
                    if (preset.defenseSlot() > 0) {
                        spec = new ToolSlotPreset.DefenseSlotConfigSpec(
                                MORE_CONFIG.comment("Upgrade Slots of " + preset.name()).define(preset.configName() + "UpgradeSlots", preset.upgradeSlot()),
                                MORE_CONFIG.comment("Defense Slots of " + preset.name()).define(preset.configName() + "AbilitySlots", preset.defenseSlot())
                        );
                    }
                }
            } else {
                if (preset.defenseSlot() > 0) {
                    if (preset.abilitySlot() > 0) {
                        spec = new ToolSlotPreset.NoUpgradeSlotConfigSpec(
                                MORE_CONFIG.comment("Ability Slots of " + preset.name()).define(preset.configName() + "AbilitySlots", preset.abilitySlot()),
                                MORE_CONFIG.comment("Defense Slots of " + preset.name()).define(preset.configName() + "DefenseSlots", preset.defenseSlot())
                        );
                    } else {
                        spec = new ToolSlotPreset.DefenseOnlySlotConfigSpec(
                                MORE_CONFIG.comment("Defense Slots of " + preset.name()).define(preset.configName() + "DefenseSlots", preset.defenseSlot())
                        );
                    }
                }
            }
            if (spec != null) {
                SLOTS_CONFIG.put(preset.rl(), spec);
            }
        });
        MORE_CONFIG.pop();
        MORE_CONFIG.push("Modifier Level Settings");
        MORE_CONFIG.comment("These values are 32-bit signed integer. (Maximum value is about 2.147G)");
        ModifierLevelPreset.PRESET.forEach(preset ->
                MODIFIER_CONFIG.put(preset.rl(), MORE_CONFIG.comment("Max Level of " + preset.name())
                        .define(preset.configName() + "MaxLevel", preset.max()))
        );
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
