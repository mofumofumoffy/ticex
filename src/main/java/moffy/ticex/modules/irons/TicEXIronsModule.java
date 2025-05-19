package moffy.ticex.modules.irons;

import moffy.addonapi.AddonModule;
import moffy.ticex.event.TicEXIronsEvent;
import moffy.ticex.item.modifiable.ModifiableIronsSpellbookItem;
import moffy.ticex.lib.CatalystMaterialStatsType;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;

public class TicEXIronsModule extends AddonModule{
    public TicEXIronsModule(){

        TicEXRegistry.CATALYST_IRONS_SPELLBOOK = TicEXRegistry.ITEMS_EXTENDED.register("catalyst_irons_spellbook", ()->new ToolPartItem(new Item.Properties(), CatalystMaterialStatsType.getOrMakeType("catalyst_irons_spellbook").getId()));

        TicEXRegistry.REVIVAL_SPELLBOOK_IRONS = TicEXRegistry.ITEMS_EXTENDED.register("revival_spellbook_irons", ()->new ModifiableIronsSpellbookItem(TicEXRegistry.SPELLBOOK_DEFINITION, 1));
    
        TicEXRegistry.OVERCASTING_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("overcasting");

        MinecraftForge.EVENT_BUS.addListener(TicEXIronsEvent::onCastSpell);
        MinecraftForge.EVENT_BUS.addListener(TicEXIronsEvent::onSpellDamage);
    }
}
