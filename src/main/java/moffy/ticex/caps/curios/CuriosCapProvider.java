package moffy.ticex.caps.curios;

import java.util.function.Supplier;

import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider.IToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import top.theillusivec4.curios.api.CuriosCapability;

public class CuriosCapProvider implements IToolCapabilityProvider {

    protected GauntletItemHandler gauntletItemHandler;

    public CuriosCapProvider(ItemStack stack, Supplier<? extends IToolStackView> toolSupplier) {
        gauntletItemHandler = new GauntletItemHandler(stack, toolSupplier);
    }

    @Override
    public <T> LazyOptional<T> getCapability(IToolStackView tool, Capability<T> capability) {
        if(tool.getModifierLevel(TicEXRegistry.INCOMPARABLE_MODIFIER.get()) > 0 && (capability == ForgeCapabilities.ITEM_HANDLER || capability == CuriosCapability.ITEM)){
            return LazyOptional.of(()->gauntletItemHandler).cast();
        }
        return LazyOptional.empty();
    }
}
