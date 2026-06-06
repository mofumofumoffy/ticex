package moffy.ticex.caps;

import java.util.function.Supplier;

import moffy.ticex.lib.hook.TicEXModifierHooks;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider.IToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class TiCEXToolCapabilityProvider implements IToolCapabilityProvider {

    private final EmbossmentMaterialCapability embossmentMaterialCapability;
    private final TinkerUniversalEnergyCapability universalEnergyCapability;

    public TiCEXToolCapabilityProvider(ItemStack stack, Supplier<? extends IToolStackView> toolSupplier) {
        embossmentMaterialCapability = new EmbossmentMaterialCapability(toolSupplier.get());
        universalEnergyCapability = new TinkerUniversalEnergyCapability(stack, toolSupplier);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull IToolStackView tool, @NotNull Capability<T> capability) {
        if (capability == EmbossmentMaterialCapability.EMBOSSMENT_MATERIAL_CAPABILITY) {
            return LazyOptional.of(() -> embossmentMaterialCapability).cast();
        } else if(capability == ForgeCapabilities.ENERGY && !tool.getModifierList().stream().filter(entry -> entry.getHook(TicEXModifierHooks.ENERGY).isEnabled()).toList().isEmpty()){
            return LazyOptional.of(() -> universalEnergyCapability).cast();
        }
        return LazyOptional.empty();
    }
}
