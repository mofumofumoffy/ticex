package moffy.ticex.caps.mekanism;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import mekanism.common.capabilities.ItemCapabilityWrapper;
import mekanism.common.capabilities.ItemCapabilityWrapper.ItemCapability;
import moffy.ticex.item.modifiable.IModifiableMekItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider.IToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class MekItemCapabilityProvider implements IToolCapabilityProvider {

    private IToolStackView tool;
    private ItemCapabilityWrapper mekCapabilityWrapper;

    public MekItemCapabilityProvider(ItemStack stack, Supplier<? extends IToolStackView> toolSupplier) {
        this.tool = toolSupplier.get();

        List<ItemCapability> capabilities = new ArrayList<>();

        if (this.tool.getItem() instanceof IModifiableMekItem) {
            IModifiableMekItem modifiableMekItem = (IModifiableMekItem) this.tool.getItem();
            if (modifiableMekItem.areCapabilityConfigsLoaded()) {
                modifiableMekItem.gatherCapabilities(capabilities, stack);
            }
        }

        this.mekCapabilityWrapper = new ItemCapabilityWrapper(stack, capabilities.toArray(ItemCapability[]::new));
    }

    @Override
    public <T> LazyOptional<T> getCapability(IToolStackView tool, Capability<T> capability) {
        if (tool.getItem() instanceof IModifiableMekItem) {
            return mekCapabilityWrapper.getCapability(capability, null);
        }
        return LazyOptional.empty();
    }
}
