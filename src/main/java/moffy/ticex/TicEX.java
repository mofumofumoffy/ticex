package moffy.ticex;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import moffy.addonapi.AddonModuleRegistry;
import moffy.ticex.modules.TicEXModuleProvider;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TicEX.MODID)
public class TicEX {
    public static final String MODID = "ticex";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ForgeConfigSpec.ConfigValue<Integer> RF_FURNACE_RATE_CAPACITY;
    public static ForgeConfigSpec.ConfigValue<Float> CONDENSING_DROP_PROBABILITY;

    public TicEX(){
        ForgeConfigSpec.Builder COMMON = new ForgeConfigSpec.Builder();

        COMMON.comment("RFFurnace Settings").push("rf_furnace");
        RF_FURNACE_RATE_CAPACITY = COMMON.comment("MAX Rate Capacity(RF/t)").define("RateCapacity", 100000);
        COMMON.pop();

        COMMON.push("avaritia");
        CONDENSING_DROP_PROBABILITY = COMMON.comment("The probability of a neutron pile is dropped by condensing modifier").define("CondensingDropProbability", 0.01f);
        COMMON.pop();

        AddonModuleRegistry.INSTANCE.LoadModule(new TicEXModuleProvider(), COMMON);
        ModLoadingContext.get().registerConfig(Type.COMMON, COMMON.build());

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        TicEXRegistry.ITEMS.register(bus);
        TicEXRegistry.ITEMS_EXTENDED.register(bus);
        TicEXRegistry.BLOCKS.register(bus);
        TicEXRegistry.BLOCK_ENTITIES.register(bus);
        TicEXRegistry.FLUIDS.register(bus);
        TicEXRegistry.MODIFIERS.register(bus);
        TicEXRegistry.ATTRIBUTES.register(bus);
        TicEXRegistry.CREATIVE_TABS.register(bus);
    }
}
