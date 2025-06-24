package moffy.ticex.modules.create;

import moffy.addonapi.AddonModule;
import moffy.ticex.item.cores.ItemReconstCore;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.item.Item.Properties;

public class TicEXCreateModule extends AddonModule {

    public TicEXCreateModule() {
        TicEXRegistry.CARDBOARD_CORE = TicEXRegistry.ITEMS.register("cardboard_core", () ->
            new ItemReconstCore(new Properties(), "cardboard")
        );

        TicEXRegistry.CARDBOARD_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("cardboard");
    }
}
