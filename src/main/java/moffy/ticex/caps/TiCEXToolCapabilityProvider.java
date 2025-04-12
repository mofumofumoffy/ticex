package moffy.ticex.caps;

import java.util.function.Supplier;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider.IToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class TiCEXToolCapabilityProvider implements IToolCapabilityProvider{

    private EmbossmentMaterialCapability embossmentMaterialCapability;

    public TiCEXToolCapabilityProvider(ItemStack stack, Supplier<? extends IToolStackView> toolSupplier){
        embossmentMaterialCapability = new EmbossmentMaterialCapability(toolSupplier.get());
    }

    @Override
    public <T> LazyOptional<T> getCapability(IToolStackView tool, Capability<T> capability) {
        if(capability == EmbossmentMaterialCapability.EMBOSSMENT_MATERIAL_CAPABILITY){
            return LazyOptional.of(()->embossmentMaterialCapability).cast();
        }
        return LazyOptional.empty();
    }
}
