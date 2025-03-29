package moffy.ticex.modules.draconicevolution;

import moffy.addonapi.AddonMixinPlugin;

public class TicEXDEMixinPlugin extends AddonMixinPlugin{

    @Override
    public String[] getRequiredModIds() {
        return new String[]{"draconicevolution"};
    }
    
}
