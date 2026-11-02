package moffy.ticex.modules.jei;

import moffy.addonapi.AddonModule;
import moffy.ticex.TicEX;
import moffy.ticex.jei.ticex.TicEXJEIIntegration;
import moffy.ticex.lib.TicEXBootstrap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class TicEXJeiModule implements AddonModule {

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient(FMLJavaModLoadingContext context) {
        TicEXBootstrap.INSTANCE.register(
                TicEX.getResource("jei_compat"),
                TicEXJEIIntegration.class
        );

        TicEXBootstrap.INSTANCE.init();
    }
}
