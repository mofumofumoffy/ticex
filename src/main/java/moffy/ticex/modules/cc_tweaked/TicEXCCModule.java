package moffy.ticex.modules.cc_tweaked;

import moffy.addonapi.AddonModule;
import moffy.ticex.event.TicEXCCEvent;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class TicEXCCModule implements AddonModule {
    @Override
    public void init(FMLJavaModLoadingContext context) {
        TicEXRegistry.MODEM_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("modem");

        MinecraftForge.EVENT_BUS.addListener(TicEXCCEvent::onPlayerAttack);
        MinecraftForge.EVENT_BUS.addListener(TicEXCCEvent::onPlayerDeath);
        MinecraftForge.EVENT_BUS.addListener(TicEXCCEvent::onPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(TicEXCCEvent::onPlayerHurt);
        MinecraftForge.EVENT_BUS.addListener(TicEXCCEvent::onPlayerJump);
    }
}
