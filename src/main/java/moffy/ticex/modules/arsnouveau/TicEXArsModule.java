package moffy.ticex.modules.arsnouveau;

/*
 * This file is part of the TicEXArsModule.
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 * See the LICENSES/LGPL-3.0.md file for details.
 * 2025 Moffy
 */


import com.hollingsworth.arsnouveau.api.perk.ArmorPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import moffy.addonapi.AddonModule;
import moffy.ticex.event.TicEXArsEvent;
import moffy.ticex.item.cores.ItemReconstCore;
import moffy.ticex.modifier.ModifierAlterative;
import moffy.ticex.modifier.ModifierReactive;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TicEXArsModule implements AddonModule {
    @Override
    public void init(FMLJavaModLoadingContext context) {
        TicEXRegistry.REACTIVE_CORE = TicEXRegistry.ITEMS.register("reactive_core", ()->new ItemReconstCore(new Item.Properties(), "reactive"));
        TicEXRegistry.ALTERATIVE_CORE = TicEXRegistry.ITEMS.register("alterative_core", ()->new ItemReconstCore(new Item.Properties(), "alterative"));

        TicEXRegistry.REACTIVE_MODIFIER = TicEXRegistry.MODIFIERS.register("reactive", ModifierReactive::new);
        TicEXRegistry.ALTERATIVE_MODIFIER = TicEXRegistry.MODIFIERS.register("alterative", ModifierAlterative::new);

        MinecraftForge.EVENT_BUS.addListener(TicEXArsEvent::onResolveSpellPre);
        MinecraftForge.EVENT_BUS.addListener(TicEXArsEvent::onResolveSpellPost);
    }
}
