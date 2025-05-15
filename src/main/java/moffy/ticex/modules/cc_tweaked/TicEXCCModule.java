package moffy.ticex.modules.cc_tweaked;

import moffy.addonapi.AddonModule;
import moffy.ticex.modules.TicEXRegistry;

public class TicEXCCModule extends AddonModule{
    public TicEXCCModule(){
        TicEXRegistry.MODEM_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("modem");
    }
}
