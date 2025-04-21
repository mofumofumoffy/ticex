package moffy.ticex.modules.create;

import moffy.addonapi.AddonModule;
import moffy.ticex.event.TicEXCreateEvent;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class TicEXCreateModule extends AddonModule{
    public TicEXCreateModule(){
        TicEXRegistry.CARDBOARD_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("cardboard");
    }

    @Override
    public void setup(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(TicEXCreateEvent::playerHitboxChangesWhenHidingAsBox);
        MinecraftForge.EVENT_BUS.addListener(TicEXCreateEvent::playersStealthWhenWearingCardboard);
        MinecraftForge.EVENT_BUS.addListener(TicEXCreateEvent::mobsMayLoseTargetWhenItIsWearingCardboard);
    }

    @Override
    public void clientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(TicEXCreateEvent::keepCacheAliveDesignDespiteNotRendering);
        MinecraftForge.EVENT_BUS.addListener(TicEXCreateEvent::playerRendersAsBoxWhenSneaking);
        MinecraftForge.EVENT_BUS.addListener(TicEXCreateEvent::keepCacheAliveDesignDespiteNotRendering);
    }
}
