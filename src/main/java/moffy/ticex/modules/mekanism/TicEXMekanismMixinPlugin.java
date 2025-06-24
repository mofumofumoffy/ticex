package moffy.ticex.modules.mekanism;

import moffy.addonapi.AddonMixinPlugin;

public class TicEXMekanismMixinPlugin extends AddonMixinPlugin {

    @Override
    public String[] getRequiredModIds() {
        return new String[] { "mekanism" };
    }
}
