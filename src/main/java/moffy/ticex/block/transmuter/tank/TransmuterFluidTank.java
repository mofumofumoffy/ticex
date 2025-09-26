package moffy.ticex.block.transmuter.tank;

import moffy.ticex.lib.TicEXTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.block.entity.MantleBlockEntity;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

public class TransmuterFluidTank implements ITransmuterTank {
    private static final Direction[] VALID_DIRECTIONS = Arrays.stream(Direction.values())
            .filter(direction -> direction != Direction.DOWN)
            .toArray(Direction[]::new);

    private final MantleBlockEntity parent;
    private final IFluidHandler outputTank;
    private final Map<Direction, IFluidHandler> inputTanks;

    private int temperature = 0;
    private boolean needsUpdate;

    public TransmuterFluidTank(MantleBlockEntity parent, IFluidHandler outputTank) {
        this.parent = parent;
        this.outputTank = outputTank;
        this.inputTanks = new EnumMap<>(Direction.class);
        this.needsUpdate = true;
    }

    public void updateTanks() {
        if (inputTanks.size() != VALID_DIRECTIONS.length) needsUpdate = true;

        Level level = parent.getLevel();
        BlockPos pos = parent.getBlockPos();

        if (level == null) return;

        if (needsUpdate) {
            for (Direction direction : VALID_DIRECTIONS) {
                BlockPos targetPos = pos.relative(direction);
                if (level.getBlockState(targetPos).is(TicEXTags.Blocks.FLUID_TRANSMUTER_TANK)) {
                    BlockEntity blockEntity = level.getBlockEntity(targetPos);
                    if (blockEntity == null) continue;

                    blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite()).ifPresent(handler -> {
                        if (inputTanks.get(direction) == handler) {
                            return;
                        }

                        inputTanks.put(direction, handler);
                    });
                }
            }
        }
        needsUpdate = false;
    }

    public void refreshDirection(Direction direction) {
        inputTanks.remove(direction);
        needsUpdate = true;
    }


    @Nullable
    public IFluidHandler getFluidHandler(int tank) {
        updateTanks();

        if (tank > VALID_DIRECTIONS.length || tank < 0) return null;

        Direction direction = VALID_DIRECTIONS[tank];
        return inputTanks.get(direction);
    }

    @Override
    public int getFluidAmount(Fluid fluid) {
        updateTanks();

        int amount = 0;
        for (IFluidHandler handler : inputTanks.values()) {
            for (int i = 0; i < handler.getTanks(); i++) {
                FluidStack fluidInTank = handler.getFluidInTank(i);
                if (fluidInTank.getFluid() != fluid) continue;
                ;
                amount += fluidInTank.getAmount();
            }
        }
        return amount;
    }

    @Override
    public int getInputTanks() {
        updateTanks();

        return VALID_DIRECTIONS.length;
    }

    @Override
    public FluidStack drain(int tank, FluidStack fluidStack) {
        updateTanks();

        IFluidHandler fluidHandler = getFluidHandler(tank);
        if (fluidHandler != null) {
            return fluidHandler.drain(fluidStack, IFluidHandler.FluidAction.EXECUTE);
        }
        return fluidStack;
    }

    public boolean canFit(FluidStack fluid) {
        updateTanks();

        return outputTank.fill(fluid, IFluidHandler.FluidAction.SIMULATE) == fluid.getAmount();
    }

    @Override
    public int fill(FluidStack fluidStack) {
        updateTanks();

        return outputTank.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
    }

    public int getTemperature() {
        return this.temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }
}
