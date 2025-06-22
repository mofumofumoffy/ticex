package moffy.ticex.modules.psi;

import moffy.addonapi.AddonModule;
import moffy.ticex.caps.psi.PsiItemCapabilityProvider;
import moffy.ticex.event.TicEXPsiEvent;
import moffy.ticex.item.cores.ItemReconstCore;
import moffy.ticex.modifier.ModifierPsionizingRadiation;
import moffy.ticex.modifier.ModifierSensor;
import moffy.ticex.modifier.ModifierSocket;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;

public class TicEXPsiModule extends AddonModule{
    public TicEXPsiModule(){
        ToolCapabilityProvider.register(PsiItemCapabilityProvider::new);

        TicEXRegistry.PSIONIZING_RADIATION_CORE = TicEXRegistry.ITEMS.register("psionizing_radiation_core", ()->new ItemReconstCore(new Item.Properties(), "psionizing_radiation"));

        TicEXRegistry.PSIONIZING_RADIATION_MODIFIER = TicEXRegistry.MODIFIERS.register("psionizing_radiation", ModifierPsionizingRadiation::new);
        TicEXRegistry.SOCKET_MODIFIER = TicEXRegistry.MODIFIERS.register("socket", ModifierSocket::new);
        TicEXRegistry.SENSOR_MODIFIER = TicEXRegistry.MODIFIERS.register("sensor", ModifierSensor::new);

        MinecraftForge.EVENT_BUS.addListener(TicEXPsiEvent::onPsiArmorEvent);
    }
}
