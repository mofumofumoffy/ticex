package moffy.ticex;

import moffy.addonapi.AddonModuleRegistry;
import moffy.ticex.lib.config.ModifierLevelPreset;
import moffy.ticex.lib.config.ToolSlotPreset;
import moffy.ticex.modules.general.TicEXModuleProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicEXConfig {
    // TicEX
    public static ForgeConfigSpec.ConfigValue<Integer> RF_FURNACE_RATE_CAPACITY;
    public static ForgeConfigSpec.ConfigValue<Boolean> USE_SHADER;
    public static ForgeConfigSpec.ConfigValue<Boolean> USE_MORE_CONFIG;
    public static ForgeConfigSpec.ConfigValue<List<String>> FLUID_TRANSMUTER_PATTERNS;
    public static ForgeConfigSpec.ConfigValue<List<String>> FLUID_TRANSMUTER_EXCLUDE_PATTERNS;

    // More Config
    public static ForgeConfigSpec.ConfigValue<Boolean> PROVIDE_PROPERTIES;
    public static List<ForgeConfigSpec.ConfigValue<Integer>> RF_FURNACE_FUEL_TEMP = new ArrayList<>();
    public static List<ForgeConfigSpec.ConfigValue<Integer>> RF_FURNACE_FUEL_RATE = new ArrayList<>();
    public static Map<ResourceLocation, ToolSlotPreset.SlotConfigSpec> SLOTS_CONFIG = new HashMap<>();
    public static Map<ResourceLocation, ForgeConfigSpec.ConfigValue<Integer>> MODIFIER_CONFIG = new HashMap<>();
    public static ForgeConfigSpec.ConfigValue<Boolean> SHOULD_CONSUME_SLASHBLADE;

    // Avaritia
    public static ForgeConfigSpec.ConfigValue<Float> CONDENSING_DROP_PROBABILITY;

    // Mekanism
    public static ForgeConfigSpec.ConfigValue<Boolean> MEKAPLATE_USE_POWER_SHIELD;

    // Apotheosis
    public static ForgeConfigSpec.ConfigValue<Integer> OVERRIDE_LIMIT;

    // Ars Nouveau
    public static ForgeConfigSpec.ConfigValue<Integer> REACTIVE_COOLDOWN_TICK;

    // Curios
    public static ForgeConfigSpec.ConfigValue<Integer> GAUNTLET_REMAIN_TICKS;
    public static ForgeConfigSpec.ConfigValue<List<String>> GLOVE_DROP_BLACKLIST;
    public static ForgeConfigSpec.ConfigValue<Boolean> GLOVE_DROP_BLACKLIST_AS_WHITELIST;

    public static void registerConfig(FMLJavaModLoadingContext context) {
        final ForgeConfigSpec.Builder COMMON = new ForgeConfigSpec.Builder();
        final ForgeConfigSpec.Builder CLIENT = new ForgeConfigSpec.Builder();
        final ForgeConfigSpec.Builder MORE_CONFIG = new ForgeConfigSpec.Builder();

        COMMON.comment("General").push("general");
        USE_MORE_CONFIG = COMMON.comment("Using ticex-more-config.toml(If true, it will override your datapack!)").define("useMoreConfig", true);
        RF_FURNACE_RATE_CAPACITY = COMMON.comment("MAX Rate Capacity(RF/t)").define("rateCapacity", 100000);
        FLUID_TRANSMUTER_PATTERNS = COMMON.comment(
                "Fluid Transmuter valid tag prefix list"
        ).define("fluid_transmuter_patterns", List.of("forge:"));
        FLUID_TRANSMUTER_EXCLUDE_PATTERNS = COMMON.comment(
                "Fluid Transmuter invalid tag prefix list"
        ).define("fluid_transmuter_exclude_patterns", List.of("forge:water", "forge:lava"));
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

        COMMON.push("ars nouveau");
        REACTIVE_COOLDOWN_TICK = COMMON.comment("Cooldown ticks of reactive modifier (Setting it too low may cause freezing)").define(
                "reactiveCooldownTick",
                100
        );
        COMMON.pop();

        COMMON.push("curios");
        GAUNTLET_REMAIN_TICKS = COMMON.comment("Ticks remaining on the gauntlet after a gauntlet shot hits")
                .define("gantletRemainTicks", 40);
        GLOVE_DROP_BLACKLIST = COMMON.comment("Blacklist of entities that do not drop the glove.")
                .define("gloveDropBlacklist", List.of("minecraft:armor_stand", "dummmmmmy:target_dummy"));
        GLOVE_DROP_BLACKLIST_AS_WHITELIST = COMMON.comment("Use gloveDropBlacklist as whitelist")
                .define("gloveDropBlacklistAsWhitelist", false);
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
        MORE_CONFIG.push("Catalyst Settings");
        SHOULD_CONSUME_SLASHBLADE = MORE_CONFIG.comment("If set to true, the catalyst will consume the Slashblade upon use.")
                .define("shouldConsumeSlashblade", true);
        MORE_CONFIG.pop();

        CLIENT.comment("Client Settings").push("client");
        USE_SHADER = CLIENT.comment("Rendering with shaders for some tools/armors").define("useShader", true);


        AddonModuleRegistry.INSTANCE.LoadModule(new TicEXModuleProvider(context), COMMON);

        ModLoadingContext.get().registerConfig(Type.COMMON, COMMON.build());
        ModLoadingContext.get().registerConfig(Type.COMMON, MORE_CONFIG.build(), "ticex-more-config.toml");
        ModLoadingContext.get().registerConfig(Type.CLIENT, CLIENT.build());
    }
}
