package moffy.ticex.modules.arsnouveau;

/*
 * This file is part of the TicEXArsModule.
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 * See the LICENSES/LGPL-3.0.md file for details.
 * 2025 Moffy
 */


import moffy.addonapi.AddonModule;
import moffy.ticex.event.TicEXArsEvent;
import moffy.ticex.modifier.ModifierReactive;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class TicEXArsModule implements AddonModule {
    @Override
    public void init(FMLJavaModLoadingContext context) {
        TicEXRegistry.REACTIVE_MODIFIER = TicEXRegistry.MODIFIERS.register("reactive", ModifierReactive::new);

        MinecraftForge.EVENT_BUS.addListener(TicEXArsEvent::beforeCastSpell);
        MinecraftForge.EVENT_BUS.addListener(TicEXArsEvent::afterCastSpell);
    }
}
