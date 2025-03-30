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
    private ToolInputStateCapability inputState;

    public SBItemCapabilityProvider(ItemStack stack, Supplier<? extends IToolStackView> toolSupplier){
        this.bladeState = new ToolBladeStateCapability(stack, toolSupplier.get());
        this.inputState = new ToolInputStateCapability(toolSupplier.get());
    }

    @Override
    public <T> LazyOptional<T> getCapability(IToolStackView tool, Capability<T> capability) {
        if(tool.getItem() instanceof ModifiableSlashBladeItem){
            if(capability == ItemSlashBlade.BLADESTATE){
                return LazyOptional.of(()->bladeState).cast();
            } else if (capability == ItemSlashBlade.INPUT_STATE){
                return LazyOptional.of(()->inputState).cast();
            }
        }
        return LazyOptional.empty();
    }
    
}
