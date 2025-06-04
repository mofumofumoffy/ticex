package moffy.ticex.modules.projecte;

import moffy.addonapi.AddonMixinPlugin;

public class TicEXPEMixinPlugin extends AddonMixinPlugin{

    @Override
    public String[] getRequiredModIds() {
        return new String[]{"projecte"};
    }

}
