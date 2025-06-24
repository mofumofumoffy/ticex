package moffy.ticex.modules.sakura;

import moffy.addonapi.AddonModule;
import moffy.ticex.modifier.ModifierSakuraCmp;
import moffy.ticex.modules.general.TicEXRegistry;

public class TicEXSakuraModule extends AddonModule {

    public TicEXSakuraModule() {
        TicEXRegistry.FLOWERSTORM_MODIFIER = TicEXRegistry.MODIFIERS.register("flowerstorm", ModifierSakuraCmp::new);
    }
}
