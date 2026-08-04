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
import moffy.ticex.item.cores.ItemReconstCore;
import moffy.ticex.modifier.ModifierAlterative;
import moffy.ticex.modifier.ModifierReactive;
import moffy.ticex.registry.TicEXRegistry;
import moffy.ticex.registry.TicEXItems;
import moffy.ticex.registry.TicEXModifiers;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class TicEXArsModule implements AddonModule {
    @Override
    public void init(FMLJavaModLoadingContext context) {
        TicEXItems.REACTIVE_CORE = TicEXRegistry.ITEMS.register("reactive_core", ()->new ItemReconstCore(new Item.Properties(), "reactive"));
        TicEXItems.ALTERATIVE_CORE = TicEXRegistry.ITEMS.register("alterative_core", ()->new ItemReconstCore(new Item.Properties(), "alterative"));

        TicEXModifiers.REACTIVE_MODIFIER = TicEXRegistry.MODIFIERS.register("reactive", ModifierReactive::new);
        TicEXModifiers.ALTERATIVE_MODIFIER = TicEXRegistry.MODIFIERS.register("alterative", ModifierAlterative::new);

        MinecraftForge.EVENT_BUS.addListener(TicEXArsEvent::onResolveSpellPre);
        MinecraftForge.EVENT_BUS.addListener(TicEXArsEvent::onResolveSpellPost);
    }
}
