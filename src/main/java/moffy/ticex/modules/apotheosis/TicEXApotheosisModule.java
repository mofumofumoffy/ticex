package moffy.ticex.modules.apotheosis;

import moffy.addonapi.AddonModule;
import moffy.ticex.datagen.general.recipes.apotheosis.FixedModuleCondition;
import moffy.ticex.event.TicEXApotheosisEvent;
import moffy.ticex.item.cores.ItemReconstCore;
import moffy.ticex.modifier.ModifierApothSupplier;
import moffy.ticex.modifier.ModifierOverload;
import moffy.ticex.modifier.ModifierOverride;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class TicEXApotheosisModule extends AddonModule {

    public TicEXApotheosisModule() {
        Item.Properties defaultProperties = new Item.Properties();

        TicEXRegistry.OVERLOAD_CORE = TicEXRegistry.ITEMS.register("overload_core", () ->
            new ItemReconstCore(defaultProperties, "overload")
        );
        TicEXRegistry.OVERRIDE_CORE = TicEXRegistry.ITEMS.register("override_core", () ->
            new ItemReconstCore(defaultProperties, "override")
        );

        TicEXRegistry.APOTH_SUPPLIER_MODIFIER = TicEXRegistry.MODIFIERS.register("apoth_supplier", ModifierApothSupplier::new);
        TicEXRegistry.OVERLOAD_MODIFIER = TicEXRegistry.MODIFIERS.register("overload", ModifierOverload::new);
        TicEXRegistry.OVERRIDE_MODIFIER = TicEXRegistry.MODIFIERS.register("override", ModifierOverride::new);

        MinecraftForge.EVENT_BUS.addListener(TicEXApotheosisEvent::onSocketGem);
    }

    @Override
    public void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> CraftingHelper.register(new FixedModuleCondition.Serializer()));
    }
}
