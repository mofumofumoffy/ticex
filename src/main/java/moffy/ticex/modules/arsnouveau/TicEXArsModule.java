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
import moffy.ticex.modifier.ModifierReactive;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class TicEXArsModule implements AddonModule {
    @Override
    public void init(FMLJavaModLoadingContext context) {
        TicEXRegistry.REACTIVE_CORE = TicEXRegistry.ITEMS.register("reactive_core", ()->new ItemReconstCore(new Item.Properties(), "reactive"));

        TicEXRegistry.REACTIVE_MODIFIER = TicEXRegistry.MODIFIERS.register("reactive", ModifierReactive::new);

        MinecraftForge.EVENT_BUS.addListener(TicEXArsEvent::onResolveSpellPre);
        MinecraftForge.EVENT_BUS.addListener(TicEXArsEvent::onResolveSpellPost);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient(FMLJavaModLoadingContext context) {

    }
}
