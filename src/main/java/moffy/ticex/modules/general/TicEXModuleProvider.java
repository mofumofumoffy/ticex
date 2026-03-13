package moffy.ticex.modules.general;

import moffy.addonapi.AddonModuleProvider;
import moffy.ticex.TicEX;
import moffy.ticex.modules.apotheosis.TicEXApotheosisModule;
import moffy.ticex.modules.arsnouveau.TicEXArsModule;
import moffy.ticex.modules.avaritia.TicEXAvaritiaModule;
import moffy.ticex.modules.botania.TicEXBotaniaModule;
import moffy.ticex.modules.cc_tweaked.TicEXCCModule;
import moffy.ticex.modules.create.TicEXCreateModule;
import moffy.ticex.modules.curios.TicEXCuriosModule;
import moffy.ticex.modules.draconicevolution.TicEXDEModule;
import moffy.ticex.modules.irons.TicEXIronsModule;
import moffy.ticex.modules.jei.TicEXJeiModule;
import moffy.ticex.modules.mekanism.TicEXMekanismModule;
import moffy.ticex.modules.projecte.TicEXPEModule;
import moffy.ticex.modules.psi.TicEXPsiModule;
import moffy.ticex.modules.sakura.TicEXSakuraModule;
import moffy.ticex.modules.slashblade.TicEXSlashBladeModule;
import moffy.ticex.modules.tacz.TicEXTaczModule;
import moffy.ticex.modules.things.TicEXThingsModule;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class TicEXModuleProvider extends AddonModuleProvider {

    public TicEXModuleProvider(FMLJavaModLoadingContext context) {
        super(context);
    }

    @Override
    public void registerRawModules() {
        addRawModule(
                "default",
                "Default",
                TicEXModule.class,
                new String[]{"tconstruct"},
                true
        );
        addRawModule(
                "default_material",
                "TiCEX Original Materials and Modifiers",
                TicEXMaterialModule.class,
                new String[]{"tconstruct"}
        );
        addRawModule(
                "default_utility",
                "TiCEX Original Utilities",
                TicEXUtilityModule.class,
                new String[]{"tconstruct"}
        );
        addRawModule(
                "avaritia_compat",
                "Avaritia Compat",
                TicEXAvaritiaModule.class,
                new String[]{"tconstruct", "avaritia"}
        );
        addRawModule(
                "mekanism_compat",
                "Mekanism Compat",
                TicEXMekanismModule.class,
                new String[]{"tconstruct", "mekanism"}
        );
        addRawModule(
                "draconicevolution_compat",
                "Draconic Evolution Compat",
                TicEXDEModule.class,
                new String[]{"tconstruct", "draconicevolution"}
        );
        addRawModule(
                "slashblade_compat",
                "SlashBlade Compat",
                TicEXSlashBladeModule.class,
                new String[]{"tconstruct", "slashblade"}
        );
        addRawModule(
                "apotheosis_compat",
                "Apotheosis Compat",
                TicEXApotheosisModule.class,
                new String[]{"tconstruct", "apotheosis"}
        );
        addRawModule(
                "tacz_compat",
                "TaCz Compat",
                TicEXTaczModule.class,
                new String[]{"tconstruct", "tacz"}
        );
        addRawModule(
                "create_compat",
                "Create Compat",
                TicEXCreateModule.class,
                new String[]{"tconstruct", "create"}
        );
        addRawModule(
                "irons_spellbooks_compat",
                "Iron's Spells n' Spellbooks Compat",
                TicEXIronsModule.class,
                new String[]{"tconstruct", "irons_spellbooks"}
        );
        addRawModule(
                "projecte_compat",
                "ProjectE Compat",
                TicEXPEModule.class,
                new String[]{"tconstruct", "projecte"}
        );
        addRawModule(
                "computercraft_compat",
                "CC:Tweaked Compat",
                TicEXCCModule.class,
                new String[]{"tconstruct", "computercraft"}
        );
        addRawModule(
                "psi_compat",
                "Psi Compat",
                TicEXPsiModule.class,
                new String[]{"tconstruct", "psi"}
        );
        addRawModule(
                "curios_compat",
                "Curios API Compat",
                TicEXCuriosModule.class,
                new String[]{"tconstruct", "curios"}
        );
        addRawModule(
                "botania_compat",
                "Botania Compat",
                TicEXBotaniaModule.class,
                new String[]{"tconstruct", "botania"}
        );
        addRawModule(
                "ars_compat",
                "Ars Nouveau Compat",
                TicEXArsModule.class,
                new String[]{"tconstruct", "ars_nouveau"}
        );
        addRawModule(
                "jei_compat",
                "JEI Compat",
                TicEXJeiModule.class,
                new String[]{"tconstruct", "jei"},
                true
        );
    }

    @Override
    public String getModId() {
        return TicEX.MODID;
    }
}
