package moffy.ticex.caps.curios;

import java.util.function.Supplier;

import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider.IToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class CuriosCapProvider implements IToolCapabilityProvider{
    
    public IncomparableCuriosCapability incomparable;

    public CuriosCapProvider(ItemStack stack, Supplier<? extends IToolStackView> toolSupplier){
        this.incomparable = new IncomparableCuriosCapability(stack, toolSupplier.get());
    }

    @Override
    public <T> LazyOptional<T> getCapability(IToolStackView tool, Capability<T> capability) {
        if(capability == IncomparableCuriosCapability.INCOMPARABLE_CAPABILITY && tool.getModifierLevel(TicEXRegistry.INCOMPARABLE_MODIFIER.get()) > 0){
            return LazyOptional.of(()->this.incomparable).cast();
        }
        return LazyOptional.empty();
    }
}
