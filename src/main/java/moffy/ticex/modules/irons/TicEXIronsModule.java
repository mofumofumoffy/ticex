package moffy.ticex.modules.irons;

import moffy.addonapi.AddonModule;
import moffy.ticex.TicEX;
import moffy.ticex.event.TicEXIronsEvent;
import moffy.ticex.item.modifiable.ModifiableIronsSpellbookItem;
import moffy.ticex.lib.CatalystMaterialStatsType;
import moffy.ticex.modifier.ModifierOvercasting;
import moffy.ticex.modules.general.TicEXRegistry;
import moffy.ticex.registry.TicEXItems;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;

public class TicEXIronsModule implements AddonModule {

    @Override
    public void init(FMLJavaModLoadingContext context) {
        TicEXItems.CATALYST_IRONS_SPELLBOOK = TicEXRegistry.ITEMS_EXTENDED.register("catalyst_irons_spellbook", () ->
                new ToolPartItem(
                        new Item.Properties(),
                        CatalystMaterialStatsType.getOrMakeType("catalyst_irons_spellbook").getId()
                )
        );

        TicEXItems.REVIVAL_SPELLBOOK_IRONS = TicEXRegistry.ITEMS_EXTENDED.register("revival_spellbook_irons", ModifiableIronsSpellbookItem::new
        );

        TicEXRegistry.OVERCASTING_MODIFIER = TicEXRegistry.MODIFIERS.register("overcasting", ModifierOvercasting::new);

        MinecraftForge.EVENT_BUS.addListener(TicEXIronsEvent::onLivingHurt);
    }
}
