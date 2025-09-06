package moffy.ticex.lib.modules.mekanism;

import mekanism.common.capabilities.ItemCapabilityWrapper;
import moffy.ticex.lib.modules.mekanism.interfaces.IMekaGear;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class MekaGearCapability implements IMekaGear {
    public static final Capability<IMekaGear> MEKA_GEAR_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public abstract static class Provider implements ICapabilityProvider {

        protected final IMekaGear mekaGearsCapability;
        private final ItemCapabilityWrapper itemCapabilityWrapper;

        public Provider(ItemStack stack){
            this.mekaGearsCapability = getMekaGearsCapability(stack);
            List<ItemCapabilityWrapper.ItemCapability> capabilities = new ArrayList<>();

            if(mekaGearsCapability.areCapabilityConfigsLoaded()){
                mekaGearsCapability.gatherCapabilities(stack, capabilities);
            }

            this.itemCapabilityWrapper = new ItemCapabilityWrapper(stack, capabilities.toArray(ItemCapabilityWrapper.ItemCapability[]::new));
        }

        public abstract IMekaGear getMekaGearsCapability(ItemStack stack);

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
            if(capability == MEKA_GEAR_CAPABILITY){
                return LazyOptional.of(()->this.mekaGearsCapability).cast();
            } else {
                return this.itemCapabilityWrapper.getCapability(capability, direction);
            }
        }
    }
}
