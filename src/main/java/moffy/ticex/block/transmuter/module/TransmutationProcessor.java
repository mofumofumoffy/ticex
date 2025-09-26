package moffy.ticex.block.transmuter.module;

import moffy.ticex.block.transmuter.pattern.FluidTransmutationPair;
import moffy.ticex.block.transmuter.pattern.FluidTransmutationResolver;
import moffy.ticex.block.transmuter.tank.ITransmuterTank;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;

public class TransmutationProcessor {
    private final ITransmuterTank transmuterTank;
    private final int maxRate;

    public TransmutationProcessor(ITransmuterTank transmuterTank, int maxRate) {
        this.transmuterTank = transmuterTank;
        this.maxRate = maxRate;
    }


    @Nullable
    private FluidTransmutationPair findMatchPair() {
        for (int tank = 0; tank < this.transmuterTank.getInputTanks(); tank++) {
            IFluidHandler fluidHandler = transmuterTank.getFluidHandler(tank);
            if (fluidHandler == null) continue;

            for (int i = 0; i < fluidHandler.getTanks(); i++) {
                FluidStack fluidInTank = fluidHandler.getFluidInTank(i);
                Fluid inputFluid = fluidInTank.getFluid();
                if (fluidInTank.isEmpty()) {
                    continue;
                }

                FluidTransmutationPair matchPair = FluidTransmutationResolver.INSTANCE.resolvePair(inputFluid);
                if (matchPair != null) {
                    return matchPair;
                }
            }
        }

        return null;
    }

    public boolean canPerform() {
        return this.findMatchPair() != null;
    }

    public void processPattern() {
        FluidTransmutationPair pair = this.findMatchPair();
        if (pair != null) {
            performPattern(pair);
        }
    }

    private void performPattern(FluidTransmutationPair pair) {
        for (int tank = 0; tank < this.transmuterTank.getInputTanks(); tank++) {
            IFluidHandler fluidHandler = transmuterTank.getFluidHandler(tank);
            if (fluidHandler == null) continue;

            for (int i = 0; i < fluidHandler.getTanks(); i++) {
                FluidStack fluidInTank = fluidHandler.getFluidInTank(i);
                if (fluidInTank.isEmpty()) {
                    continue;
                }

                Fluid inputFluid = fluidInTank.getFluid();
                if (!pair.inputFluid().isSame(inputFluid)) {
                    continue;
                }

                Fluid outputFluid = pair.outputFluid();


                FluidStack willDrain = fluidHandler.drain(new FluidStack(inputFluid, maxRate), IFluidHandler.FluidAction.SIMULATE);
                if (!willDrain.isEmpty()) {
                    FluidStack outputStack = new FluidStack(outputFluid, willDrain.getAmount());
                    if (!transmuterTank.canFit(outputStack.copy())) continue;

                    fluidHandler.drain(willDrain, IFluidHandler.FluidAction.EXECUTE);
                    this.transmuterTank.fill(outputStack.copy());
                    return;
                }
            }
        }
    }

    public int getMaxRate() {
        return maxRate;
    }
}
