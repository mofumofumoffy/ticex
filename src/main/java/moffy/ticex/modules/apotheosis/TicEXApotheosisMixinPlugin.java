package moffy.ticex.modules.apotheosis;

import moffy.addonapi.AddonMixinPlugin;

public class TicEXApotheosisMixinPlugin extends AddonMixinPlugin {
    @Override
    public String[] getRequiredModIds() {
        return new String[]{"apotheosis"};
    }
}
