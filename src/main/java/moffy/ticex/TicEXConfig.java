package moffy.ticex;

import moffy.addonapi.AddonModuleRegistry;
import moffy.ticex.lib.config.ConfigListUtil;
import moffy.ticex.modules.general.TicEXModuleProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;

public class TicEXConfig {
    // TicEX
    public static ForgeConfigSpec.ConfigValue<Integer> RF_FURNACE_RATE_CAPACITY;
    public static ForgeConfigSpec.ConfigValue<Boolean> USE_SHADER;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> FLUID_TRANSMUTER_PATTERNS;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> FLUID_TRANSMUTER_EXCLUDE_PATTERNS;

    // More Config
    public static ForgeConfigSpec.ConfigValue<Boolean> USE_MORE_CONFIG;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> RF_FURNACE_FUEL_TEMP;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> RF_FURNACE_FUEL_RATE;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> SLOTS_CONFIG;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> MODIFIER_CONFIG;
    public static ForgeConfigSpec.ConfigValue<Boolean> SHOULD_CONSUME_SLASHBLADE;

    // Avaritia
    public static ForgeConfigSpec.ConfigValue<Float> CONDENSING_DROP_PROBABILITY;

    // Mekanism
    public static ForgeConfigSpec.ConfigValue<Boolean> MEKAPLATE_USE_POWER_SHIELD;

    // Apotheosis
    public static ForgeConfigSpec.ConfigValue<Integer> OVERRIDE_LIMIT;

    // Ars Nouveau
    public static ForgeConfigSpec.ConfigValue<Integer> REACTIVE_COOLDOWN;

    // Curios
    public static ForgeConfigSpec.ConfigValue<Integer> GAUNTLET_REMAIN_TICKS;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> GLOVE_DROP_BLACKLIST;
    public static ForgeConfigSpec.ConfigValue<Boolean> GLOVE_DROP_BLACKLIST_AS_WHITELIST;

    public static void registerConfig(FMLJavaModLoadingContext context) {
        final ForgeConfigSpec.Builder COMMON = new ForgeConfigSpec.Builder();
        final ForgeConfigSpec.Builder CLIENT = new ForgeConfigSpec.Builder();
        final ForgeConfigSpec.Builder MORE_CONFIG = new ForgeConfigSpec.Builder();

        COMMON.push("general");
        RF_FURNACE_RATE_CAPACITY = COMMON.comment("MAX Rate Capacity(RF/t)").define("rateCapacity", 100000);
        FLUID_TRANSMUTER_PATTERNS = COMMON.comment(
                "Fluid Transmuter valid tag prefix list"
        ).defineListAllowEmpty("fluid_transmuter_patterns", List.of("forge:"), o -> o instanceof String);
        FLUID_TRANSMUTER_EXCLUDE_PATTERNS = COMMON.comment(
                "Fluid Transmuter invalid tag prefix list"
        ).defineListAllowEmpty("fluid_transmuter_exclude_patterns", List.of("forge:water", "forge:lava"), o -> o instanceof String str && ResourceLocation.isValidResourceLocation(str));
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
        REACTIVE_COOLDOWN = COMMON.comment("Internal cooldown ticks of spellcasting").define("reactiveCooldown", 10);
        COMMON.pop();

        COMMON.push("curios");
        GAUNTLET_REMAIN_TICKS = COMMON.comment("Ticks remaining on the gauntlet after a gauntlet shot hits")
                .define("gantletRemainTicks", 40);
        GLOVE_DROP_BLACKLIST = COMMON.comment("Blacklist of entities that do not drop the glove.")
                .defineListAllowEmpty("gloveDropBlacklist", List.of("minecraft:armor_stand", "dummmmmmy:target_dummy"), o -> o instanceof String str && ResourceLocation.isValidResourceLocation(str));
        GLOVE_DROP_BLACKLIST_AS_WHITELIST = COMMON.comment("Use gloveDropBlacklist as whitelist")
                .define("gloveDropBlacklistAsWhitelist", false);
        COMMON.pop();


        MORE_CONFIG.push("general");
        USE_MORE_CONFIG = MORE_CONFIG.comment("Using More-Config(If true, it may override your datapack!)").define("useMoreConfig", false);
        MORE_CONFIG.pop();

        MORE_CONFIG.push("RF Furnace Fuel Temperature Settings");
        MORE_CONFIG.comment("The format for these is \"key|value\".");
        MORE_CONFIG.comment("- key:RF Furnace Fuel index(1-19)");
        MORE_CONFIG.comment("- value:32-bit signed integer (Maximum value is about 2.147G)");
        MORE_CONFIG.comment("ex. \"1|200\"(Temperature of RF Furnace Fuel 1 will be set to 200)");
        RF_FURNACE_FUEL_TEMP = MORE_CONFIG.defineListAllowEmpty("RF Furnace Temps", List.of("19|2147483647"), o -> o instanceof String str && str.matches("1?[0-9]\\|[0-9]+"));
        MORE_CONFIG.pop();

        MORE_CONFIG.push("RF Furnace Fuel Speed Rate Settings");
        MORE_CONFIG.comment("The format for these is \"key|value\".");
        MORE_CONFIG.comment(
                "- key:RF Furnace Fuel index(1-19)",
                "- value:32-bit signed integer (Maximum value is about 2.147G)",
                "The actual speed multiplier is calculated by dividing this value by 10.",
                "It means setting it to 100 results in 10x speed, and 25 results in 2.5x speed."
        );
        MORE_CONFIG.comment("ex. \"1|250\"(Speed rate of RF Furnace Fuel 1 will be set to 25x)");
        RF_FURNACE_FUEL_RATE = MORE_CONFIG.defineListAllowEmpty("Speed Rate of RF Furnace Fuel", List.of("19|655350"), o -> o instanceof String str && str.matches("1?[0-9]\\|[0-9]+"));
        MORE_CONFIG.pop();

        MORE_CONFIG.push("Tool/Armor Slots Override Settings");
        MORE_CONFIG.comment("The format for these is \"key1|key2|value\".");
        MORE_CONFIG.comment("- key1:ResourceLocation of tool");
        MORE_CONFIG.comment("- key2:\"upgrades\"/\"abilities\"/\"defence\"");
        MORE_CONFIG.comment("- value: 32-bit signed integer. (Maximum value is about 2.147G)");
        MORE_CONFIG.comment("ex. \"tconstruct:cleaver|abilities|3\"(ability slot size of tconstruct:cleaver will be set to 3)");
        SLOTS_CONFIG = MORE_CONFIG.defineListAllowEmpty("Slots Override Settings", List.of("tconstruct:sword|upgrades|5"), o -> o instanceof String str && str.matches("[A-Za-z0-9]+:[A-Za-z0-9]+\\|[A-Za-z0-9]+\\|[0-9]+"));
        MORE_CONFIG.pop();

        MORE_CONFIG.push("Modifier Maximum Level Settings");
        MORE_CONFIG.comment("The format for these is \"key|value\".");
        MORE_CONFIG.comment("- key1:ResourceLocation of modifier");
        MORE_CONFIG.comment("- value: 32-bit signed integer. (Maximum value is about 2.147G)");
        MORE_CONFIG.comment("ex. \"tconstruct:tools/modifiers/upgrade/necrotic|10\"(maximum level of tconstruct:tools/modifiers/upgrade/necrotic will be set to 10)");
        MODIFIER_CONFIG = MORE_CONFIG.defineListAllowEmpty("Maximum Level of Modifiers", List.of("tconstruct:tools/modifiers/upgrade/reinforced|7"), o -> o instanceof String str && str.matches("[A-Za-z0-9]+:[A-Za-z0-9/]+\\|[0-9]+"));
        MORE_CONFIG.pop();
        MORE_CONFIG.push("Catalyst Settings");
        SHOULD_CONSUME_SLASHBLADE = MORE_CONFIG.comment("If set to true, the catalyst will consume the Slashblade upon use.")
                .define("shouldConsumeSlashblade", true);
        MORE_CONFIG.pop();

        CLIENT.comment("Client Settings").push("client");
        USE_SHADER = CLIENT.comment("Rendering with shaders for some tools/armors").define("useShader", true);


        AddonModuleRegistry.INSTANCE.LoadModule(new TicEXModuleProvider(context), COMMON);

        context.registerConfig(Type.COMMON, COMMON.build());
        context.registerConfig(Type.COMMON, MORE_CONFIG.build(), "ticex-more-config.toml");
        context.registerConfig(Type.CLIENT, CLIENT.build());
    }
}
