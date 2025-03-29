package moffy.ticex.modules;

import moffy.addonapi.AddonModuleProvider;
import moffy.ticex.TicEX;
import moffy.ticex.modules.avaritia.TicEXAvaritiaModule;
import moffy.ticex.modules.draconicevolution.TicEXDEModule;
import moffy.ticex.modules.mekanism.TicEXMekanismModule;
import net.minecraft.resources.ResourceLocation;

public class TicEXModuleProvider extends AddonModuleProvider{

    @Override
    public void registerRawModules() {
        addRawModule(new ResourceLocation(TicEX.MODID, "default_compat"), "Default Compat", TicEXModule::new, new String[]{"tconstruct"}, true);
        addRawModule(new ResourceLocation(TicEX.MODID, "avaritia_compat"), "Avaritia Compat", TicEXAvaritiaModule::new, new String[]{"tconstruct", "avaritia"});
        addRawModule(new ResourceLocation(TicEX.MODID, "mekanism_compat"), "Mekanism Compat", TicEXMekanismModule::new, new String[]{"tconstruct", "mekanism"});
        addRawModule(new ResourceLocation(TicEX.MODID, "draconicevolution_compat"), "Draconic Evolution Compat", TicEXDEModule::new, new String[]{"tconstruct", "draconicevolution"});
    }
    

    @Override
    public String getModId() {
        return TicEX.MODID;
    }
}
