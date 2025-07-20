package moffy.ticex.modules.projecte;

import moffy.addonapi.AddonModule;
import moffy.ticex.event.TicEXPEEvent;
import moffy.ticex.item.modifiable.ModifiableGemArmor;
import moffy.ticex.lib.CatalystMaterialStatsType;
import moffy.ticex.modifier.ModifierAbyssal;
import moffy.ticex.modifier.ModifierGravitiy;
import moffy.ticex.modifier.ModifierHurricane;
import moffy.ticex.modifier.ModifierInfernal;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;

public class TicEXPEModule extends AddonModule {

    public TicEXPEModule() {
        TicEXRegistry.SINGULAR_GEM_ARMOR = TicEXRegistry.ITEMS_EXTENDED.registerEnum(
            "singular_gem",
            ArmorItem.Type.values(),
            type ->
                new ModifiableGemArmor(
                    type,
                    TicEXRegistry.SINGULAR_GEM_DEFINITION,
                    1,
                    new Item.Properties().fireResistant()
                )
        );

        TicEXRegistry.CATALYST_GEM = TicEXRegistry.ITEMS_EXTENDED.registerEnum(
            "catalyst_gem",
            ArmorItem.Type.values(),
            type ->
                new ToolPartItem(
                    new Item.Properties(),
                    CatalystMaterialStatsType.getOrMakeType("catalyst_gem", type).getId()
                )
        );

        TicEXRegistry.ABYSSAL_MODIFIER = TicEXRegistry.MODIFIERS.register("abyssal", ModifierAbyssal::new);
        TicEXRegistry.INFERNAL_MODIFIER = TicEXRegistry.MODIFIERS.register("infernal", ModifierInfernal::new);
        TicEXRegistry.GRAVITY_MODIFIER = TicEXRegistry.MODIFIERS.register("gravity", ModifierGravitiy::new);
        TicEXRegistry.HURRICANE_MODIFIER = TicEXRegistry.MODIFIERS.register("hurricane", ModifierHurricane::new);

        MinecraftForge.EVENT_BUS.addListener(TicEXPEEvent::onJump);
        MinecraftForge.EVENT_BUS.addListener(TicEXPEEvent::onPlayerTick);
    }
}
