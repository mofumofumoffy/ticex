package moffy.ticex;

import moffy.addonapi.AddonModuleRegistry;
import moffy.ticex.modules.general.TicEXModuleProvider;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;

public class TicEXConfig {
    public static ForgeConfigSpec.ConfigValue<Integer> RF_FURNACE_RATE_CAPACITY;
    public static ForgeConfigSpec.ConfigValue<Boolean> USE_SHADER;
    public static ForgeConfigSpec.ConfigValue<Float> CONDENSING_DROP_PROBABILITY;
    public static ForgeConfigSpec.ConfigValue<Boolean> MEKAPLATE_USE_POWER_SHIELD;
    public static ForgeConfigSpec.ConfigValue<Integer> OVERRIDE_LIMIT;

    public static void registerConfig(){
        final ForgeConfigSpec.Builder COMMON = new ForgeConfigSpec.Builder();
        final ForgeConfigSpec.Builder CLIENT = new ForgeConfigSpec.Builder();

        COMMON.comment("RFFurnace Settings").push("rf_furnace");
        RF_FURNACE_RATE_CAPACITY = COMMON.comment("MAX Rate Capacity(RF/t)").define("rateCapacity", 100000);
        COMMON.pop();

        COMMON.push("avaritia");
        CONDENSING_DROP_PROBABILITY = COMMON.comment("Probability of a neutron pile is dropped by condensing modifier").define("condensingDropProbability", 0.003f);
        COMMON.pop();

        COMMON.push("mekanism");
        MEKAPLATE_USE_POWER_SHIELD = COMMON.comment("Allow Mekaplate can use shield of electricity").define("mekaplateUseShield", true);
        COMMON.pop();

        COMMON.push("apotheosis");
        OVERRIDE_LIMIT = COMMON.comment("Maximum level of enchantments granted by override").define("overrideLevelLimit", 255);
        COMMON.pop();

        CLIENT.comment("Client Settings").push("client");
        USE_SHADER = CLIENT.comment("Rendering with shaders for some tools/armors").define("useShader", true);
        CLIENT.pop();

        AddonModuleRegistry.INSTANCE.LoadModule(new TicEXModuleProvider(), COMMON);

        ModLoadingContext.get().registerConfig(Type.COMMON, COMMON.build());
        ModLoadingContext.get().registerConfig(Type.CLIENT, CLIENT.build());
    }
}
