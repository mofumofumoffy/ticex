package moffy.ticex.modules.sakura;

import moffy.addonapi.AddonModule;
import moffy.ticex.modifier.ModifierSakuraCmp;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class TicEXSakuraModule implements AddonModule {
    @Override
    public void init(FMLJavaModLoadingContext context) {
        TicEXRegistry.FLOWERSTORM_MODIFIER = TicEXRegistry.MODIFIERS.register("flowerstorm", ModifierSakuraCmp::new);
    }
}
