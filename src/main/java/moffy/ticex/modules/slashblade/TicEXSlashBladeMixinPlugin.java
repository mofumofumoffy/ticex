package moffy.ticex.modules.slashblade;

import moffy.addonapi.AddonMixinPlugin;

public class TicEXSlashBladeMixinPlugin extends AddonMixinPlugin {

    @Override
    public String[] getRequiredModIds() {
        return new String[] { "tconstruct", "slashblade" };
    }
}
