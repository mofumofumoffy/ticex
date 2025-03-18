package moffy.ticex.item.modifiable;

import java.util.List;

import mekanism.common.capabilities.ItemCapabilityWrapper.ItemCapability;
import net.minecraft.world.item.ItemStack;


public interface IModifiableMekItem {
    public boolean areCapabilityConfigsLoaded();

    public void gatherCapabilities(List<ItemCapability> capabilities, ItemStack stack);
}
