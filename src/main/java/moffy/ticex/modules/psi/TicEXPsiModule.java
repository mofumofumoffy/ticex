package moffy.ticex.modules.psi;

import moffy.addonapi.AddonModule;
import moffy.ticex.caps.psi.PsiItemCapabilityProvider;
import moffy.ticex.event.TicEXPsiEvent;
import moffy.ticex.item.cores.ItemReconstCore;
import moffy.ticex.modifier.ModifierPsionizingRadiation;
import moffy.ticex.modifier.ModifierSensor;
import moffy.ticex.modifier.ModifierSocket;
import moffy.ticex.registry.TicEXRegistry;
import moffy.ticex.registry.TicEXItems;
import moffy.ticex.registry.TicEXModifiers;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;

public class TicEXPsiModule implements AddonModule {
    @Override
    public void init(FMLJavaModLoadingContext context) {
        ToolCapabilityProvider.register(PsiItemCapabilityProvider::new);

        TicEXItems.PSIONIZING_RADIATION_CORE = TicEXRegistry.ITEMS.register("psionizing_radiation_core", () ->
                new ItemReconstCore(new Item.Properties(), "psionizing_radiation")
        );

        TicEXModifiers.PSIONIZING_RADIATION_MODIFIER = TicEXRegistry.MODIFIERS.register(
                "psionizing_radiation",
                ModifierPsionizingRadiation::new
        );
        TicEXModifiers.SOCKET_MODIFIER = TicEXRegistry.MODIFIERS.register("socket", ModifierSocket::new);
        TicEXModifiers.SENSOR_MODIFIER = TicEXRegistry.MODIFIERS.register("sensor", ModifierSensor::new);

        MinecraftForge.EVENT_BUS.addListener(TicEXPsiEvent::onPsiArmorEvent);
    }
}
