package moffy.ticex.modules;

import moffy.addonapi.AddonModuleProvider;
import moffy.ticex.TicEX;
import moffy.ticex.modules.avaritia.TicEXAvaritiaModule;
import moffy.ticex.modules.mekanism.TicEXMekanismModule;
import net.minecraft.resources.ResourceLocation;

public class TicEXModuleProvider extends AddonModuleProvider{

    @Override
    public void registerRawModules() {
        addRawModule(new ResourceLocation(TicEX.MODID, "compat_default"), "Default Compat", TicEXModule.class, new String[]{"tconstruct"}, true);
        addRawModule(new ResourceLocation(TicEX.MODID, "compat_avaritia"), "Avaritia Compat", TicEXAvaritiaModule.class, new String[]{"tconstruct", "avaritia"});
        addRawModule(new ResourceLocation(TicEX.MODID, "compat_mekanism"), "Mekanism Compat", TicEXMekanismModule.class, new String[]{"tconstruct", "mekanism"});
    }
    

    @Override
    public String getModId() {
        return TicEX.MODID;
    }
}
