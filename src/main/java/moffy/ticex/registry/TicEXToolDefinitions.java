package moffy.ticex.registry;

import moffy.ticex.TicEX;
import net.minecraft.sounds.SoundEvents;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;

public class TicEXToolDefinitions {
    public static ModifiableArmorMaterial MEKAPLATE_DEFINITION = ModifiableArmorMaterial.create(
            TicEX.getResource("mekaplate"),
            SoundEvents.ARMOR_EQUIP_NETHERITE
    );
    public static ModifiableArmorMaterial SINGULAR_GEM_DEFINITION = ModifiableArmorMaterial.create(
            TicEX.getResource("singular_gem"),
            SoundEvents.ARMOR_EQUIP_NETHERITE
    );
    public static ToolDefinition SLASHBLADE_DEFINITION = ToolDefinition.create(
            TicEX.getResource("reforged_slashblade")
    );
    public static ToolDefinition GUN_DEFINITION = ToolDefinition.create(
            TicEX.getResource("blitz_gun")
    );
    public static ToolDefinition SPELLBOOK_DEFINITION = ToolDefinition.create(
            TicEX.getResource("revival_spellbook")
    );
    public static ToolDefinition MEKA_TOOL_DEFINITION = ToolDefinition.create(
            TicEX.getResource("meka_edge")
    );
    public static ToolDefinition GAUNTLET_DEFINITION = ToolDefinition.create(
            TicEX.getResource("resonance_gauntlet")
    );

}
