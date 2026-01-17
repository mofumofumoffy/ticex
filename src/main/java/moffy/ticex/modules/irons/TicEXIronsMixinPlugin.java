package moffy.ticex.modules.irons;

import moffy.addonapi.AddonMixinPlugin;

public class TicEXIronsMixinPlugin extends AddonMixinPlugin {

    @Override
    public String[] getRequiredModIds() {
        return new String[] {
                "irons_spellbooks"
        };
    }
}
