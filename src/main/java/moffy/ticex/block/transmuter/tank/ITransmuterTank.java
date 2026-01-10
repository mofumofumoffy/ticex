package moffy.ticex.block.transmuter.tank;

import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.recipe.container.IEmptyContainer;

public interface ITransmuterTank extends IEmptyContainer, ITankMutable {
    @Nullable
    IFluidHandler getFluidHandler(int tank);

    int getFluidAmount(Fluid fluid);

    int getInputTanks();

    boolean canFit(FluidStack fluid);

    int getTemperature();

    void setTemperature(int temperature);
}
