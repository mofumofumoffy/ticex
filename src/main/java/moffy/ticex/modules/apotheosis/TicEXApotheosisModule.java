package moffy.ticex.modules.apotheosis;

import moffy.addonapi.AddonModule;
import moffy.ticex.item.cores.ItemReconstCore;
import moffy.ticex.modifier.ModifierOverload;
import moffy.ticex.modifier.ModifierOverride;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.world.item.Item;

public class TicEXApotheosisModule extends AddonModule{
    public TicEXApotheosisModule(){
        Item.Properties defaultProperties = new Item.Properties();

        TicEXRegistry.OVERLOAD_CORE = TicEXRegistry.ITEMS.register("overload_core", ()->new ItemReconstCore(defaultProperties, "overload"));
        TicEXRegistry.OVERRIDE_CORE = TicEXRegistry.ITEMS.register("override_core", ()->new ItemReconstCore(defaultProperties, "override"));

        TicEXRegistry.OVERLOAD_MODIFIER = TicEXRegistry.MODIFIERS.register("overload", ModifierOverload::new);
        TicEXRegistry.OVERRIDE_MODIFIER = TicEXRegistry.MODIFIERS.register("override", ModifierOverride::new);
    }
}
