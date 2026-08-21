package moffy.ticex.datagen.blockstate;

import moffy.ticex.TicEX;
import moffy.ticex.registry.TicEXBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.smeltery.block.component.SearedBlock;

public class TicEXBlockstateProvider extends BlockStateProvider {
    public TicEXBlockstateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, TicEX.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        activatableBlock(TicEXBlocks.CREATIVE_SEARED_RF_FURNACE.get(), "block/smeltery/seared_rf_furnace", 180);
        activatableBlock(TicEXBlocks.CREATIVE_SCORCHED_RF_FURNACE.get(), "block/foundry/scorched_rf_furnace", 180);

        activatableBlock(TicEXBlocks.SEARED_RF_FURNACE.get(), "block/smeltery/seared_rf_furnace", 180);
        activatableBlock(TicEXBlocks.SCORCHED_RF_FURNACE.get(), "block/foundry/scorched_rf_furnace", 180);

        activatableBlock(TicEXBlocks.FLUID_TRANSMUTER.get(), "block/foundry/fluid_transmuter", 180);

        simpleModelBlock(TicEXBlocks.ETHERIC_BLOCK.get(), "block/etheric_block");
        simpleModelBlock(TicEXBlocks.OD_BLOCK.get(), "block/od_block");
        simpleModelBlock(TicEXBlocks.ASTRAL_BLOCK.get(), "block/astral_block");
    }

    public void simpleModelBlock(Block block, String name) {
        simpleBlock(block, models().getExistingFile(modLoc(name)));
    }

    public void activatableBlock(Block block, String modelResource, int angleOffset) {
        activatableBlock(block, modelResource + "_active", modelResource + "_inactive", angleOffset);
    }

    public void activatableBlock(Block block, String active, String inactive, int angleOffset) {
        horizontalBlock(block,
                blockState -> models().getExistingFile(blockState.getValue(SearedBlock.IN_STRUCTURE) ?
                        modLoc(active): modLoc(inactive)),
                angleOffset);
    }
}
