package moffy.ticex.block.entity;

import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tile.interfaces.IHasMode;
import mekanism.common.tile.prefab.TileEntityConfigurableMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityTiCElectricAnvil extends TileEntityConfigurableMachine implements IHasMode {
    public TileEntityTiCElectricAnvil(BlockPos pos, BlockState state) {
        super(MekanismBlocks.FORMULAIC_ASSEMBLICATOR, pos, state);
    }

    @Override
    public void nextMode() {

    }

    @Override
    public void previousMode() {

    }
}
