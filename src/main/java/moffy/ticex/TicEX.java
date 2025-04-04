package moffy.ticex;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod(TicEX.MODID)
public class TicEX {
    public static final String MODID = "ticex";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(MODID, "main"), 
        ()->PROTOCOL_VERSION, 
        PROTOCOL_VERSION::equals, 
        PROTOCOL_VERSION::equals
    );

    private static int packetHandlerId = 0;

    public TicEX(){

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        TicEXRegistry.ITEMS.register(bus);
        TicEXRegistry.ITEMS_EXTENDED.register(bus);
        TicEXRegistry.BLOCKS.register(bus);
        TicEXRegistry.BLOCK_ENTITIES.register(bus);
        TicEXRegistry.FLUIDS.register(bus);
        TicEXRegistry.ENTITIES.register(bus);
        TicEXRegistry.MODIFIERS.register(bus);
        TicEXRegistry.ATTRIBUTES.register(bus);
        TicEXRegistry.CREATIVE_TABS.register(bus);

        TicEXConfig.registerConfig();
    }

    public static int getPacketHandlerId(){
        return packetHandlerId++;
    }
}
