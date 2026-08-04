package moffy.ticex.modules.create;

import moffy.addonapi.AddonModule;
import moffy.ticex.item.cores.ItemReconstCore;
import moffy.ticex.registry.TicEXRegistry;
import moffy.ticex.registry.TicEXItems;
import moffy.ticex.registry.TicEXModifiers;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class TicEXCreateModule implements AddonModule {

    @Override
    public void init(FMLJavaModLoadingContext context) {
        TicEXItems.CARDBOARD_CORE = TicEXRegistry.ITEMS.register("cardboard_core", () ->
                new ItemReconstCore(new Properties(), "cardboard")
        );

        TicEXModifiers.CARDBOARD_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("cardboard");
    }
}
