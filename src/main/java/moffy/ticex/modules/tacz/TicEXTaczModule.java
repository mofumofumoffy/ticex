package moffy.ticex.modules.tacz;

/*
 * This file is part of the TicEXTaczModule.
 *
 * Licensed under the GNU General Public License v3.0.
 * See the LICENSES/GPL-3.0.md file for details.
 * 2025 Moffy
 */

import moffy.addonapi.AddonModule;
import moffy.ticex.client.render.custom.CustomModel;
import moffy.ticex.client.render.ticex.TicEXRenders;
import moffy.ticex.event.TicEXTaczEvent;
import moffy.ticex.item.modifiable.ModifiableGunItem;
import moffy.ticex.lib.CatalystMaterialStatsType;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;

public class TicEXTaczModule implements AddonModule {

    @Override
    public void init(FMLJavaModLoadingContext context) {
        Item.Properties defaultProperties = new Item.Properties();

        TicEXRegistry.CATALYST_KINETIC_GUN = TicEXRegistry.ITEMS_EXTENDED.register("catalyst_kinetic_gun", () ->
                new ToolPartItem(defaultProperties, CatalystMaterialStatsType.getOrMakeType("catalyst_kinetic_gun").getId())
        );

        TicEXRegistry.BLITZ_GUN = TicEXRegistry.ITEMS_EXTENDED.register("blitz_gun", () ->
                new ModifiableGunItem(TicEXRegistry.GUN_DEFINITION, 1)
        );

        MinecraftForge.EVENT_BUS.addListener(TicEXTaczEvent::onBeforeHit);
        MinecraftForge.EVENT_BUS.addListener(TicEXTaczEvent::onAfterHit);
        MinecraftForge.EVENT_BUS.addListener(TicEXTaczEvent::onMelee);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void clientSetup(FMLClientSetupEvent event) {
        TicEXRenders.CUSTOM_MODELS.put(TicEXRegistry.BLITZ_GUN.get(), CustomModel::new);
    }
}
