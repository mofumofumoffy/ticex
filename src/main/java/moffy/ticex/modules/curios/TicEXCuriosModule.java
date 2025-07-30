package moffy.ticex.modules.curios;

import moffy.addonapi.AddonModule;
import moffy.ticex.caps.curios.CuriosCapProvider;
import moffy.ticex.item.MittenItem;
import moffy.ticex.item.cores.ItemReconstCore;
import moffy.ticex.item.modifiable.ModifiableGauntlet;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;

public class TicEXCuriosModule extends AddonModule {

    public TicEXCuriosModule() {
        ToolCapabilityProvider.register(CuriosCapProvider::new);

        TicEXRegistry.EXHAUSTED_MITTEN = TicEXRegistry.ITEMS.register("exhausted_mitten", () -> new MittenItem(new Item.Properties().stacksTo(1)));
        TicEXRegistry.RESONANCE_GAUNTLET = TicEXRegistry.ITEMS_EXTENDED.register("resonance_gauntlet", ()->new ModifiableGauntlet(new Item.Properties().stacksTo(1), TicEXRegistry.GAUNTLET_DEFINITION));

        TicEXRegistry.INCOMPARABLE_CORE = TicEXRegistry.ITEMS.register("incomparable_core", () ->
            new ItemReconstCore(new Properties(), "incomparable")
        );

        TicEXRegistry.INCOMPARABLE_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("incomparable");
    }
}
