package moffy.ticex.modules.curios;

import moffy.addonapi.AddonModule;
import moffy.ticex.caps.curios.CuriosCapProvider;
import moffy.ticex.event.TicEXCuriosEvent;
import moffy.ticex.item.GloveItem;
import moffy.ticex.item.cores.ItemReconstCore;
import moffy.ticex.item.modifiable.ModifiableGauntlet;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.common.MinecraftForge;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;

public class TicEXCuriosModule extends AddonModule {

    public TicEXCuriosModule() {
        ToolCapabilityProvider.register(CuriosCapProvider::new);

        TicEXRegistry.EXHAUSTED_GLOVE = TicEXRegistry.ITEMS.register("exhausted_glove", () -> new GloveItem(new Item.Properties().stacksTo(1)));
        TicEXRegistry.RESONANCE_GAUNTLET = TicEXRegistry.ITEMS_EXTENDED.register("resonance_gauntlet", ()->new ModifiableGauntlet(new Item.Properties().stacksTo(1), TicEXRegistry.GAUNTLET_DEFINITION));

        TicEXRegistry.INCOMPARABLE_CORE = TicEXRegistry.ITEMS.register("incomparable_core", () ->
            new ItemReconstCore(new Properties(), "incomparable")
        );

        TicEXRegistry.INCOMPARABLE_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("incomparable");

        MinecraftForge.EVENT_BUS.addListener(TicEXCuriosEvent::onLivingDeath);
    }
}
