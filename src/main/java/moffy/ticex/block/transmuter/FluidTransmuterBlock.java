package moffy.ticex.block.transmuter;

import com.google.common.collect.ImmutableMap;
import moffy.ticex.block.transmuter.entity.FluidTransmuterBlockEntity;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.smeltery.block.controller.TinyMultiblockControllerBlock;

import java.util.function.Function;

public class FluidTransmuterBlock extends TinyMultiblockControllerBlock {
    public FluidTransmuterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new FluidTransmuterBlockEntity(TicEXRegistry.FLUID_TRANSMUTER_ENTITY.get(), blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? null :
                BlockEntityHelper.castTicker(pBlockEntityType, TicEXRegistry.FLUID_TRANSMUTER_ENTITY.get(), FluidTransmuterBlockEntity.SERVER_TICKER);
    }

    @Override
    protected @NotNull ImmutableMap<BlockState, VoxelShape> getShapeForEachState(Function<BlockState, VoxelShape> pShapeGetter) {
        ImmutableMap<BlockState, VoxelShape> defaultShapes = super.getShapeForEachState(pShapeGetter);

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : this.getStateDefinition().getPossibleStates()) {
            VoxelShape defaultShape = defaultShapes.get(state);
            if (defaultShape == null) continue;

            builder.put(state, Shapes.or(
                    defaultShape,
                    Shapes.box(0, 16, 1, 6, 20, 7)
            ));
        }
        return builder.build();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Block pNeighborBlock, @NotNull BlockPos pNeighborPos, boolean pMovedByPiston) {
        Direction direction = Util.directionFromOffset(pPos, pNeighborPos);
        if (direction != Direction.DOWN) {
            BlockEntityHelper.get(FluidTransmuterBlockEntity.class, pLevel, pPos).ifPresent((te) -> te.neighborChanged(direction));
        }
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public float getShadeBrightness(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter reader, @NotNull BlockPos pos) {
        return true;
    }

    @Override
    public void animateTick(BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        if (state.getValue(ACTIVE)) {
            double x = pos.getX() + 0.5D;
            double y = (double) pos.getY() + (rand.nextFloat() * 4F) / 16F;
            double z = pos.getZ() + 0.5D;
            double frontOffset = 0.52D;
            double sideOffset = rand.nextDouble() * 0.6D - 0.3D;
            spawnFireParticles(world, state, x, y, z, frontOffset, sideOffset, ParticleTypes.SOUL_FIRE_FLAME);
        }
    }
}
