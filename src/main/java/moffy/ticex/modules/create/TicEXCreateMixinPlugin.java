package moffy.ticex.modules.create;

import moffy.addonapi.AddonMixinPlugin;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;

public class TicEXCreateMixinPlugin extends AddonMixinPlugin {

    private static final String MIN_VERSION = "6.0.0";

    @Override
    public boolean shouldApplyMixin(String arg0, String arg1) {
        boolean result = super.shouldApplyMixin(arg0, arg1);
        if (result) {
            ModFileInfo createInfo = LoadingModList.get().getModFileById("create");
            if (createInfo.versionString().compareTo(MIN_VERSION) <= 0) {
                return false;
            }
        }
        return result;
    }

    @Override
    public String[] getRequiredModIds() {
        return new String[] { "create" };
    }
}
