package moffy.ticex.block.transmuter.entity;

import moffy.ticex.block.transmuter.container.FluidTransmuterContainerMenu;
import moffy.ticex.block.transmuter.module.TransmutationProcessor;
import moffy.ticex.block.transmuter.tank.TransmuterFluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.block.entity.NameableBlockEntity;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.library.utils.NBTTags;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock;
import slimeknights.tconstruct.smeltery.block.controller.ControllerBlock;
import slimeknights.tconstruct.smeltery.block.controller.MelterBlock;
import slimeknights.tconstruct.smeltery.block.entity.ITankBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.module.SolidFuelModule;

import javax.annotation.Nonnull;

public class FluidTransmuterBlockEntity extends NameableBlockEntity implements ITankBlockEntity {
    public static final BlockEntityTicker<FluidTransmuterBlockEntity> SERVER_TICKER =
            (level, blockPos, blockState, blockEntity) -> blockEntity.serverTick(level, blockPos, blockState);

    private static final Component NAME = TConstruct.makeTranslation("gui", "fluid_transmuter");
    private static final int TANK_CAPACITY = SearedTankBlock.TankType.INGOT_TANK.getCapacity();

    private final FluidTankAnimated tank;
    private final LazyOptional<IFluidHandler> tankHolder;
    private final TransmuterFluidTank transmuterTank;
    private final TransmutationProcessor processor;
    private final SolidFuelModule fuelModule;
    private int tick;

    public FluidTransmuterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, NAME);
        this.tank = new FluidTankAnimated(TANK_CAPACITY, this);
        this.tankHolder = LazyOptional.of(() -> this.tank);

        this.transmuterTank = new TransmuterFluidTank(this, this.tank);
        this.processor = new TransmutationProcessor(this.transmuterTank, 20);
        this.fuelModule = new SolidFuelModule(this, pos.below());
    }

    @Nonnull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @javax.annotation.Nullable Direction facing) {
        return capability == ForgeCapabilities.FLUID_HANDLER ? this.tankHolder.cast() : super.getCapability(capability, facing);
    }

    public void invalidateCaps() {
        super.invalidateCaps();
        this.tankHolder.invalidate();
    }

    private boolean isFormed() {
        BlockState state = this.getBlockState();
        return state.hasProperty(MelterBlock.IN_STRUCTURE) && state.getValue(MelterBlock.IN_STRUCTURE);
    }

    public void serverTick(Level level, BlockPos pos, BlockState blockState) {
        if (this.isFormed() && processor.getMaxRate() != 0) {
            switch (this.tick) {
                case 0:
                    this.transmuterTank.setTemperature(this.fuelModule.findFuel(false));
                    if (!this.fuelModule.hasFuel() && this.processor.canPerform()) {
                        this.fuelModule.findFuel(true);
                    }
                    break;
                case 2:
                    boolean hasFuel = this.fuelModule.hasFuel();
                    if (blockState.getValue(ControllerBlock.ACTIVE) != hasFuel) {
                        level.setBlockAndUpdate(pos, blockState.setValue(ControllerBlock.ACTIVE, hasFuel));
                        BlockPos down = pos.below();
                        BlockState downState = level.getBlockState(down);
                        if (downState.is(TinkerTags.Blocks.FUEL_TANKS) && downState.hasProperty(ControllerBlock.ACTIVE) && (Boolean) downState.getValue(ControllerBlock.ACTIVE) != hasFuel) {
                            level.setBlockAndUpdate(down, downState.setValue(ControllerBlock.ACTIVE, hasFuel));
                        }
                    }

                    if (hasFuel) {
                        this.transmuterTank.setTemperature(this.fuelModule.getTemperature());
                        this.processor.processPattern();
                        this.fuelModule.decreaseFuel(1);
                    }
            }

            this.tick = (this.tick + 1) % 4;
        }
    }

    public void neighborChanged(Direction side) {
        this.transmuterTank.refreshDirection(side);
    }

    public void saveSynced(@NotNull CompoundTag tag) {
        super.saveSynced(tag);
        tag.put(NBTTags.TANK, this.tank.writeToNBT(new CompoundTag()));
    }

    public void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        this.fuelModule.writeToTag(tag);
    }

    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.tank.readFromNBT(nbt.getCompound(NBTTags.TANK));
        this.fuelModule.readFromTag(nbt);
    }

    @Override
    protected boolean shouldSyncOnUpdate() {
        return true;
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return new FluidTransmuterContainerMenu(i, inventory, this);
    }

    @Override
    public @NotNull FluidTankAnimated getTank() {
        return this.tank;
    }

    public TransmuterFluidTank getTransmuterTank() {
        return transmuterTank;
    }

    public SolidFuelModule getFuelModule() {
        return fuelModule;
    }

    public TransmutationProcessor getTransmuterModule() {
        return processor;
    }

    @Override
    public int getLastStrength() {
        return 0;
    }

    @Override
    public void setLastStrength(int i) {

    }
}
