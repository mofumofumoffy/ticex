package moffy.ticex.modules.general;

import moffy.addonapi.AddonModuleProvider;
import moffy.ticex.TicEX;
import moffy.ticex.modules.apotheosis.TicEXApotheosisModule;
import moffy.ticex.modules.avaritia.TicEXAvaritiaModule;
import moffy.ticex.modules.cc_tweaked.TicEXCCModule;
import moffy.ticex.modules.create.TicEXCreateModule;
import moffy.ticex.modules.curios.TicEXCuriosModule;
import moffy.ticex.modules.draconicevolution.TicEXDEModule;
import moffy.ticex.modules.irons.TicEXIronsModule;
import moffy.ticex.modules.mekanism.TicEXMekanismModule;
import moffy.ticex.modules.projecte.TicEXPEModule;
import moffy.ticex.modules.sakura.TicEXSakuraModule;
import moffy.ticex.modules.slashblade.TicEXSlashBladeModule;
import moffy.ticex.modules.tacz.TicEXTaczModule;
import moffy.ticex.modules.things.TicEXThingsModule;
import net.minecraft.resources.ResourceLocation;

public class TicEXModuleProvider extends AddonModuleProvider{

    @Override
    public void registerRawModules() {
        addRawModule(new ResourceLocation(TicEX.MODID, "default_compat"), "Default Compat", TicEXModule.class, new String[]{"tconstruct"}, true);
        addRawModule(new ResourceLocation(TicEX.MODID, "avaritia_compat"), "Avaritia Compat", TicEXAvaritiaModule.class, new String[]{"tconstruct", "avaritia"});
        addRawModule(new ResourceLocation(TicEX.MODID, "mekanism_compat"), "Mekanism Compat", TicEXMekanismModule.class, new String[]{"tconstruct", "mekanism"});
        addRawModule(new ResourceLocation(TicEX.MODID, "draconicevolution_compat"), "Draconic Evolution Compat", TicEXDEModule.class, new String[]{"tconstruct", "draconicevolution"});
        addRawModule(new ResourceLocation(TicEX.MODID, "slashblade_compat"), "SlashBlade Compat", TicEXSlashBladeModule.class, new String[]{"tconstruct", "slashblade"});
        addRawModule(new ResourceLocation(TicEX.MODID, "apotheosis_compat"), "Apotheosis Compat", TicEXApotheosisModule.class, new String[]{"tconstruct", "apotheosis"});
        addRawModule(new ResourceLocation(TicEX.MODID, "tacz_compat"), "TaCz Compat", TicEXTaczModule.class, new String[]{"tconstruct", "tacz"});
        addRawModule(new ResourceLocation(TicEX.MODID, "create_compat"), "Create Compat", TicEXCreateModule.class, new String[]{"tconstruct", "create"});
        addRawModule(new ResourceLocation(TicEX.MODID, "irons_spellbooks_compat"), "Iron's Spells n' Spellbooks Compat", TicEXIronsModule.class, new String[]{"tconstruct", "irons_spellbooks"});
        addRawModule(new ResourceLocation(TicEX.MODID, "projecte_compat"), "ProjectE Compat", TicEXPEModule.class, new String[]{"tconstruct", "projecte"});
        addRawModule(new ResourceLocation(TicEX.MODID, "computercraft_compat"), "CC:Tweaked Compat", TicEXCCModule.class, new String[]{"tconstruct", "computercraft"});
        addRawModule(new ResourceLocation(TicEX.MODID, "curios_compat"), "Curios API Compat", TicEXCuriosModule.class, new String[]{"tconstruct", "curios"});
        addRawModule(new ResourceLocation(TicEX.MODID, "sakura_compat"), "Sakura Tinker Compat", TicEXSakuraModule.class, new String[]{"tconstruct", "sakuratinker"});
        addRawModule(new ResourceLocation(TicEX.MODID, "things_compat"), "Tinkers' Things Compat", TicEXThingsModule.class, new String[]{"tconstruct", "tinkers_things"});
    }
    

    @Override
    public String getModId() {
        return TicEX.MODID;
    }
}
