package moffy.ticex.modules;

import moffy.addonapi.AddonModuleProvider;
import moffy.ticex.modules.avaritia.TicEXAvaritiaModule;
import moffy.ticex.modules.mekanism.TicEXMekanismModule;

public class TicEXModuleProvider extends AddonModuleProvider{

    @Override
    public void registerRawModules() {
        addRawModule("Default", TicEXModule.class, new String[]{"tconstruct"});
        addRawModule("Avaritia Compat", TicEXAvaritiaModule.class, new String[]{"tconstruct", "avaritia"});
        addRawModule("Mekanism Compat", TicEXMekanismModule.class, new String[]{"tconstruct", "mekanism"});
    }
    
}
