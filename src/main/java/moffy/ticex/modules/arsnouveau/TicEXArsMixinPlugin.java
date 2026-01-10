package moffy.ticex.modules.arsnouveau;

import moffy.addonapi.AddonMixinPlugin;

public class TicEXArsMixinPlugin extends AddonMixinPlugin {
    @Override
    public String[] getRequiredModIds() {
        return new String[]{"ars_nouveau"};
    }
}
