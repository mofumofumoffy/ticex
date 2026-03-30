package moffy.ticex.modules.cctweaked;

import moffy.addonapi.AddonMixinPlugin;

public class TicEXCCPlugin extends AddonMixinPlugin {

    @Override
    public String[] getRequiredModIds() {
        return new String[] { "computercraft" };
    }
}
