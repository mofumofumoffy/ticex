package moffy.ticex.caps;

import java.util.function.Supplier;

import moffy.ticex.registry.TicEXModifierHooks;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider.IToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class TiCEXToolCapabilityProvider implements IToolCapabilityProvider {

    private final EmbossmentMaterialCapability embossmentMaterialCapability;

    public TiCEXToolCapabilityProvider(ItemStack stack, Supplier<? extends IToolStackView> toolSupplier) {
        embossmentMaterialCapability = new EmbossmentMaterialCapability(toolSupplier.get());
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull IToolStackView tool, @NotNull Capability<T> capability) {
        if (capability == EmbossmentMaterialCapability.EMBOSSMENT_MATERIAL_CAPABILITY) {
            return LazyOptional.of(() -> embossmentMaterialCapability).cast();
        }
        return LazyOptional.empty();
    }
}
