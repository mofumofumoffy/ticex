package moffy.ticex.lib.modules.mekanism.interfaces;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface IFluidTankItem {
    FluidStack getContainedFluid(ItemStack stack, FluidStack type);
}
