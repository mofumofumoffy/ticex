package moffy.ticex.caps.psi;

import java.util.function.Supplier;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider.IToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import vazkii.psi.api.PsiAPI;

public class PsiItemCapabilityProvider implements IToolCapabilityProvider {

    protected ToolCADData toolCADData;

    public PsiItemCapabilityProvider(ItemStack stack, Supplier<? extends IToolStackView> toolSupplier) {
        this.toolCADData = new ToolCADData(stack, toolSupplier.get());
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(IToolStackView tool, Capability<T> capability) {
        if (
            (capability == PsiAPI.SOCKETABLE_CAPABILITY ||
                capability == PsiAPI.CAD_DATA_CAPABILITY ||
                capability == PsiAPI.PSI_BAR_DISPLAY_CAPABILITY ||
                capability == PsiAPI.SPELL_ACCEPTOR_CAPABILITY) &&
            tool.getModifierLevel(TicEXRegistry.PSIONIZING_RADIATION_MODIFIER.get()) > 0
        ) {
            return LazyOptional.of(() -> toolCADData).cast();
        }
        return LazyOptional.empty();
    }
}
