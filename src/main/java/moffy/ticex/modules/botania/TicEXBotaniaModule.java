package moffy.ticex.modules.botania;

import moffy.addonapi.AddonModule;
import moffy.ticex.event.TicEXBotaniaEvent;
import moffy.ticex.item.cores.ItemReconstCore;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.library.modifiers.util.DynamicModifier;

public class TicEXBotaniaModule implements AddonModule {
    @Override
    public void init(FMLJavaModLoadingContext context) {
        TicEXRegistry.AHRIM_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("ahrim");
        TicEXRegistry.DHAROK_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("dharok");
        TicEXRegistry.GUTHAN_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("guthan");
        TicEXRegistry.TORAG_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("torag");
        TicEXRegistry.VERAC_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("verac");
        TicEXRegistry.KARIL_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("karil");

        TicEXRegistry.NECTAR_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("nectar");

        TicEXRegistry.NECTAR_CORE = TicEXRegistry.ITEMS.register("nectar_core",() ->
                new ItemReconstCore(new Item.Properties(), "nectar")
        );

        MinecraftForge.EVENT_BUS.addListener(TicEXBotaniaEvent::onCritical);
        MinecraftForge.EVENT_BUS.addListener(TicEXBotaniaEvent::onLivingAttack);
    }
}
