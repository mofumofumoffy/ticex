package moffy.ticex.caps.mekanism;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import mekanism.common.capabilities.ItemCapabilityWrapper;
import mekanism.common.capabilities.ItemCapabilityWrapper.ItemCapability;
import moffy.ticex.lib.modules.mekanism.MekaGearCapability;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider.IToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class MekItemCapabilityProvider implements IToolCapabilityProvider {

    private IToolStackView tool;
    private final MekaGearCapability mekaGearCapability;
    private final ItemCapabilityWrapper mekCapabilityWrapper;

    public MekItemCapabilityProvider(ItemStack stack, Supplier<? extends IToolStackView> toolSupplier) {
        this.tool = toolSupplier.get();

        if(stack.getItem() instanceof ArmorItem armorItem){
            mekaGearCapability = new MekaArmorGearCapability(armorItem);
        } else if (stack.is(TinkerTags.Items.MELEE_WEAPON)){
            mekaGearCapability = new MekaToolGearCapability();
        } else {
            mekaGearCapability = new MekaToolGearCapability();
        }
        List<ItemCapability> capabilities = new ArrayList<>();
        if(mekaGearCapability.areCapabilityConfigsLoaded()){
            mekaGearCapability.gatherCapabilities(stack, capabilities);
        }

        this.mekCapabilityWrapper = new ItemCapabilityWrapper(stack, capabilities.toArray(ItemCapability[]::new));
    }

    @Override
    public <T> LazyOptional<T> getCapability(IToolStackView tool, Capability<T> capability) {
        if (tool.getModifierLevel(TicEXRegistry.MEKANIC_MODIFIER.get()) > 0) {
            if(capability == MekaGearCapability.MEKA_GEAR_CAPABILITY){
                return LazyOptional.of(()->mekaGearCapability).cast();
            } else {
                return mekCapabilityWrapper.getCapability(capability, null);
            }
        }
        return LazyOptional.empty();
    }

    public IToolStackView getTool() {
        return tool;
    }

    public void setTool(IToolStackView tool) {
        this.tool = tool;
    }
}
