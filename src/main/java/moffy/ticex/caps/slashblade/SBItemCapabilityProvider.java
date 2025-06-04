package moffy.ticex.caps.slashblade;

import java.util.function.Supplier;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import moffy.ticex.item.modifiable.ModifiableSlashBladeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider.IToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class SBItemCapabilityProvider implements IToolCapabilityProvider{

    private ToolBladeStateCapability bladeState;

    public SBItemCapabilityProvider(ItemStack stack, Supplier<? extends IToolStackView> toolSupplier){
        this.bladeState = new ToolBladeStateCapability(stack, toolSupplier.get());
    }

    @Override
    public <T> LazyOptional<T> getCapability(IToolStackView tool, Capability<T> capability) {
        if(capability == ItemSlashBlade.BLADESTATE && tool.getItem() instanceof ModifiableSlashBladeItem){
            return LazyOptional.of(()->bladeState).cast();
        }
        return LazyOptional.empty();
    }


}
