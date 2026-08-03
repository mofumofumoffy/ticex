package moffy.ticex.registry;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;

public class TicEXItems {
    public static RegistryObject<Item> EXHAUSTED_GLOVE = null;
    public static RegistryObject<Item> ETHERIC_INGOT = null;
    public static RegistryObject<Item> OD_INGOT = null;
    public static RegistryObject<Item> DRACONIUM_CRYSTAL = null;
    public static RegistryObject<Item> WYVERN_CRYSTAL = null;
    public static RegistryObject<Item> DRACONIC_CRYSTAL = null;
    public static RegistryObject<Item> CHAOTIC_CRYSTAL = null;
    public static RegistryObject<Item> RECONSTRUCTION_CORE = null;
    public static RegistryObject<Item> FLICKERING_RECONSTRUCTION_CORE = null;
    public static RegistryObject<Item> CELESTIAL_CORE = null;
    public static RegistryObject<Item> RADIATION_SHELDING_CORE = null;
    public static RegistryObject<Item> DRACONIUM_EVOLVED_CORE = null;
    public static RegistryObject<Item> WYVERN_EVOLVED_CORE = null;
    public static RegistryObject<Item> DRACONIC_EVOLVED_CORE = null;
    public static RegistryObject<Item> CHAOTIC_EVOLVED_CORE = null;
    public static RegistryObject<Item> INJECT_CORE = null;
    public static RegistryObject<Item> KONPAKU_CORE = null;
    public static RegistryObject<Item> OVERLOAD_CORE = null;
    public static RegistryObject<Item> OVERRIDE_CORE = null;
    public static RegistryObject<Item> CARDBOARD_CORE = null;
    public static RegistryObject<Item> PSIONIZING_RADIATION_CORE = null;
    public static RegistryObject<Item> NECTAR_CORE = null;
    public static RegistryObject<Item> REACTIVE_CORE = null;
    public static RegistryObject<Item> ALTERATIVE_CORE = null;

    public static RegistryObject<Item> MEKANIC_ARROW = null;

    public static ItemObject<ToolPartItem> SLASHBLADE_BLADE = null;
    public static ItemObject<ToolPartItem> SLASHBLADE_SAYA = null;

    public static CastItemObject SLASHBLADE_BLADE_CAST;
    public static CastItemObject SLASHBLADE_SAYA_CAST;

    public static EnumObject<ArmorItem.Type, ToolPartItem> CATALYST_MEKASUIT = null;
    public static EnumObject<ArmorItem.Type, ToolPartItem> CATALYST_GEM = null;
    public static ItemObject<ToolPartItem> CATALYST_SLASHBLADE = null;
    public static ItemObject<ToolPartItem> CATALYST_KINETIC_GUN = null;
    public static ItemObject<ToolPartItem> CATALYST_IRONS_SPELLBOOK = null;
    public static ItemObject<ToolPartItem> CATALYST_MEKA_TOOL = null;
    public static ItemObject<ToolPartItem> CATALYST_MEKA_TANA = null;
    public static ItemObject<ToolPartItem> CATALYST_MEKA_BOW = null;

    public static ItemObject<? extends Item> REFORGED_SLASHBLADE = null;
    public static ItemObject<? extends Item> BLITZ_GUN = null;
    public static ItemObject<? extends Item> REVIVAL_SPELLBOOK_IRONS = null;
    public static ItemObject<? extends ModifiableItem> MEKA_EDGE = null;
    public static ItemObject<? extends Item> RESONANCE_GAUNTLET = null;

    public static EnumObject<ArmorItem.Type, ? extends IModifiable> MEKAPLATE_ARMOR = null;
    public static EnumObject<ArmorItem.Type, ? extends IModifiable> SINGULAR_GEM_ARMOR = null;
}
