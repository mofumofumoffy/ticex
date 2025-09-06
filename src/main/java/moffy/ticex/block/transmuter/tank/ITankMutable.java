package moffy.ticex.block.transmuter.tank;

import net.minecraftforge.fluids.FluidStack;

public interface ITankMutable {
    FluidStack drain(int tank, FluidStack fluidStack);

    @SuppressWarnings("UnusedReturnValue")
    int fill(FluidStack fluidStack);
}
