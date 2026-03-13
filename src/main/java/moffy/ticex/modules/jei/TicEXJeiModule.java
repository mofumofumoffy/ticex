package moffy.ticex.modules.jei;

import moffy.addonapi.AddonModule;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class TicEXJeiModule implements AddonModule {

    @OnlyIn(Dist.CLIENT)
    @Override
    public void clientSetup(FMLClientSetupEvent event) {
        TicEXRegistry.JEI_INTEGRATIONS.init();
    }
}
