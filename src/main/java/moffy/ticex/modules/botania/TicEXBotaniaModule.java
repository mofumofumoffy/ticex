package moffy.ticex.modules.botania;

import moffy.addonapi.AddonModule;
import moffy.ticex.event.TicEXBotaniaEvent;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.library.modifiers.util.DynamicModifier;

public class TicEXBotaniaModule implements AddonModule {
    @Override
    public void init(FMLJavaModLoadingContext context) {
        TicEXRegistry.AHRIM = TicEXRegistry.MODIFIERS.registerDynamic("ahrim");
        TicEXRegistry.DHAROK = TicEXRegistry.MODIFIERS.registerDynamic("dharok");
        TicEXRegistry.GUTHAN = TicEXRegistry.MODIFIERS.registerDynamic("guthan");
        TicEXRegistry.TORAG = TicEXRegistry.MODIFIERS.registerDynamic("torag");
        TicEXRegistry.VERAC = TicEXRegistry.MODIFIERS.registerDynamic("verac");
        TicEXRegistry.KARIL = TicEXRegistry.MODIFIERS.registerDynamic("karil");

        MinecraftForge.EVENT_BUS.addListener(TicEXBotaniaEvent::onCritical);
        MinecraftForge.EVENT_BUS.addListener(TicEXBotaniaEvent::onLivingAttack);
    }
}
