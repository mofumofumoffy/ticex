package moffy.ticex.caps.mekanism;

import java.util.function.Supplier;

import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.ItemCapabilityWrapper;
import mekanism.common.capabilities.radiation.item.RadiationShieldingHandler;
import mekanism.common.item.gear.ItemHazmatSuitArmor;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider.IToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class RadiationShieldingCapabilityProvider implements IToolCapabilityProvider{
    private ItemCapabilityWrapper mekCapabilityWrapper;

    public RadiationShieldingCapabilityProvider(ItemStack stack, Supplier<? extends IToolStackView> toolSupplier){
        this.mekCapabilityWrapper = new ItemCapabilityWrapper(stack, RadiationShieldingHandler.create(item -> 
                                                                  ItemHazmatSuitArmor.getShieldingByArmor(((ArmorItem)item.getItem()).getType())));
    }

    @Override
    public <T> LazyOptional<T> getCapability(IToolStackView tool, Capability<T> capability) {
        if(capability == Capabilities.RADIATION_SHIELDING && tool.getModifierLevel(TicEXRegistry.RADIATION_SHIELDING_MODIFIER.get()) > 0){
            return mekCapabilityWrapper.getCapability(capability);
        }
        return LazyOptional.empty();
    }
}
