package moffy.ticex.modules.things;

import moffy.addonapi.AddonModule;
import moffy.ticex.item.cores.ItemReconstCore;
import moffy.ticex.modifier.ModifierLamellar;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class TicEXThingsModule implements AddonModule {

    @Override
    public void init(FMLJavaModLoadingContext context) {
        Item.Properties defaultProperties = new Item.Properties();

        /*TicEXRegistry.LAMELLAR_CORE = TicEXRegistry.ITEMS.register("lamellar_core", () ->
                new ItemReconstCore(defaultProperties, "lamellar")
        );*/

        TicEXRegistry.LAMELLAR_MODIFIER = TicEXRegistry.MODIFIERS.register("lamellar", ModifierLamellar::new);
    }
}
