package moffy.ticex.modules.curios;

import moffy.addonapi.AddonMixinPlugin;

public class TicEXCuriosMixinPlugin extends AddonMixinPlugin {

    @Override
    public String[] getRequiredModIds() {
        return new String[]{"curios"};
    }
    
}
