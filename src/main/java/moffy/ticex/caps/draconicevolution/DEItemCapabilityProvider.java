package moffy.ticex.caps.draconicevolution;

import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import java.util.function.Supplier;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider.IToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class DEItemCapabilityProvider implements IToolCapabilityProvider {

    protected EvolvedModuleHost moduleHost;
    protected EvolvedOPStorage opStorage;
    protected EvolvedEnergyStorage energyStorage;

    public DEItemCapabilityProvider(ItemStack stack, Supplier<? extends IToolStackView> toolSupplier) {
        this.moduleHost = new EvolvedModuleHost(toolSupplier.get());
        this.opStorage = new EvolvedOPStorage(this.moduleHost, toolSupplier.get());
        this.energyStorage = new EvolvedEnergyStorage(opStorage);
    }

    @Override
    public <T> LazyOptional<T> getCapability(IToolStackView tool, Capability<T> capability) {
        if (
            (capability == DECapabilities.MODULE_HOST_CAPABILITY ||
                capability == DECapabilities.PROPERTY_PROVIDER_CAPABILITY) &&
            tool.getModifierLevel(TicEXRegistry.EVOLVED_MODIFIER.get()) > 0
        ) {
            return LazyOptional.of(() -> moduleHost).cast();
        } else if (
            capability == DECapabilities.OP_STORAGE && tool.getModifierLevel(TicEXRegistry.EVOLVED_MODIFIER.get()) > 0
        ) {
            return LazyOptional.of(() -> opStorage).cast();
        } else if (
            capability == ForgeCapabilities.ENERGY && tool.getModifierLevel(TicEXRegistry.EVOLVED_MODIFIER.get()) > 0
        ) {
            return LazyOptional.of(() -> energyStorage).cast();
        }
        return LazyOptional.empty();
    }
}
