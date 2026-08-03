package moffy.ticex.modules.general;

import moffy.addonapi.AddonModule;
import moffy.ticex.block.furnace.RFFurnaceBlock;
import moffy.ticex.block.furnace.entity.RFFurnaceBlockEntity;
import moffy.ticex.block.transmuter.FluidTransmuterBlock;
import moffy.ticex.block.transmuter.entity.FluidTransmuterBlockEntity;
import moffy.ticex.lib.utils.TicEXFluidUtils;
import moffy.ticex.registry.TicEXBlocks;
import moffy.ticex.registry.TicEXFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class TicEXUtilityModule implements AddonModule {
    @Override
    public void init(FMLJavaModLoadingContext context) {

        TicEXBlocks.SEARED_RF_FURNACE = TicEXRegistry.BLOCKS.register("seared_rf_furnace", () ->
                new RFFurnaceBlock(TicEXBlocks.SEARED, false)
        );
        TicEXBlocks.SCORCHED_RF_FURNACE = TicEXRegistry.BLOCKS.register("scorched_rf_furnace", () ->
                new RFFurnaceBlock(TicEXBlocks.SCORCHED, false)
        );
        TicEXBlocks.CREATIVE_SEARED_RF_FURNACE = TicEXRegistry.BLOCKS.register("creative_seared_rf_furnace", () ->
                new RFFurnaceBlock(TicEXBlocks.SEARED, true)
        );
        TicEXBlocks.CREATIVE_SCORCHED_RF_FURNACE = TicEXRegistry.BLOCKS.register("creative_scorched_rf_furnace", () ->
                new RFFurnaceBlock(TicEXBlocks.SCORCHED, true)
        );
        TicEXBlocks.FLUID_TRANSMUTER = TicEXRegistry.BLOCKS.register("fluid_transmuter", () ->
                new FluidTransmuterBlock(BlockBehaviour.Properties.of().noOcclusion())
        );

        TicEXRegistry.ITEMS.register("seared_rf_furnace", () ->
                new BlockItem(TicEXBlocks.SEARED_RF_FURNACE.get(), new Item.Properties())
        );
        TicEXRegistry.ITEMS.register("scorched_rf_furnace", () ->
                new BlockItem(TicEXBlocks.SCORCHED_RF_FURNACE.get(), new Item.Properties())
        );
        TicEXRegistry.ITEMS.register("creative_seared_rf_furnace", () ->
                new BlockItem(TicEXBlocks.CREATIVE_SEARED_RF_FURNACE.get(), new Item.Properties())
        );
        TicEXRegistry.ITEMS.register("creative_scorched_rf_furnace", () ->
                new BlockItem(TicEXBlocks.CREATIVE_SCORCHED_RF_FURNACE.get(), new Item.Properties())
        );
        TicEXRegistry.ITEMS.register("fluid_transmuter", () ->
                new BlockItem(TicEXBlocks.FLUID_TRANSMUTER.get(), new Item.Properties())
        );

        TicEXBlocks.RF_FURNACE_ENTITY = TicEXRegistry.BLOCK_ENTITIES.register("rf_furnace_entity", () ->
                BlockEntityType.Builder.of(
                        (BlockPos pPos, BlockState pState) ->
                                new RFFurnaceBlockEntity(TicEXBlocks.RF_FURNACE_ENTITY.get(), pPos, pState, false),
                        TicEXBlocks.SEARED_RF_FURNACE.get(),
                        TicEXBlocks.SCORCHED_RF_FURNACE.get(),
                        TicEXBlocks.CREATIVE_SEARED_RF_FURNACE.get(),
                        TicEXBlocks.CREATIVE_SCORCHED_RF_FURNACE.get()
                ).build(null)
        );

        TicEXBlocks.FLUID_TRANSMUTER_ENTITY = TicEXRegistry.BLOCK_ENTITIES.register("fluid_transmuter", () ->
                BlockEntityType.Builder.of(
                        (BlockPos pPos, BlockState pState) ->
                                new FluidTransmuterBlockEntity(TicEXBlocks.FLUID_TRANSMUTER_ENTITY.get(), pPos, pState),
                        TicEXBlocks.FLUID_TRANSMUTER.get()
                ).build(null)
        );

        for (int i = 0; i < 20; i++) {
            TicEXFluids.RF_FURNACE_FUELS.add(
                    TicEXRegistry.FLUIDS.register("rf_furnace_fuel_" + i)
                            .type(TicEXFluidUtils.hot("rf_furnace_fuel_" + i).temperature(1000).density(-1600))
                            .unplacable()
            );
        }
    }
}
