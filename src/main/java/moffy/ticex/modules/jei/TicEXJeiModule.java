package moffy.ticex.modules.jei;

import moffy.addonapi.AddonModule;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class TicEXJeiModule extends AddonModule {
    public TicEXJeiModule() {
    }

    @Override
    public void clientSetup(FMLClientSetupEvent event) {
        TicEXRegistry.JEI_INTEGRATIONS.init();
    }
}
