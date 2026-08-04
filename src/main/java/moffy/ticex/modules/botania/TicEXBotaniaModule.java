package moffy.ticex.modules.botania;

import moffy.addonapi.AddonModule;
import moffy.ticex.event.TicEXBotaniaEvent;
import moffy.ticex.item.cores.ItemReconstCore;
import moffy.ticex.registry.TicEXRegistry;
import moffy.ticex.registry.TicEXItems;
import moffy.ticex.registry.TicEXModifiers;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class TicEXBotaniaModule implements AddonModule {
    @Override
    public void init(FMLJavaModLoadingContext context) {
        TicEXModifiers.AHRIM_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("ahrim");
        TicEXModifiers.DHAROK_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("dharok");
        TicEXModifiers.GUTHAN_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("guthan");
        TicEXModifiers.TORAG_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("torag");
        TicEXModifiers.VERAC_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("verac");
        TicEXModifiers.KARIL_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("karil");

        TicEXModifiers.NECTAR_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("nectar");

        TicEXItems.NECTAR_CORE = TicEXRegistry.ITEMS.register("nectar_core",() ->
                new ItemReconstCore(new Item.Properties(), "nectar")
        );

        MinecraftForge.EVENT_BUS.addListener(TicEXBotaniaEvent::onCritical);
        MinecraftForge.EVENT_BUS.addListener(TicEXBotaniaEvent::onLivingAttack);
    }
}
