package moffy.ticex.modules.slashblade;

import moffy.addonapi.AddonModule;
import moffy.ticex.TicEX;
import moffy.ticex.caps.slashblade.SBItemCapabilityProvider;
import moffy.ticex.item.modifiable.ModifiableSlashBladeItem;
import moffy.ticex.modifier.ModifierKonpaku;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;

public class TicEXSlashBladeModule extends AddonModule{

    public static final ToolDefinition SLASHBLADE_DEFINITION = new ToolDefinition(new ResourceLocation(TicEX.MODID, "slashblade_tool")); 

    public TicEXSlashBladeModule(){

        ToolCapabilityProvider.register(SBItemCapabilityProvider::new);

        TicEXRegistry.SLASHBLADE_TOOL = TicEXRegistry.ITEMS_EXTENDED.register("slashblade_tool", ()->new ModifiableSlashBladeItem(new Item.Properties().stacksTo(1), SLASHBLADE_DEFINITION));

        TicEXRegistry.KONPAKU_MODIFIER = TicEXRegistry.MODIFIERS.register("konpaku", ModifierKonpaku::new);
    }
}
