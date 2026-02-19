package moffy.ticex.modules.general;

import moffy.addonapi.AddonModule;
import moffy.ticex.block.furnace.RFFurnaceBlock;
import moffy.ticex.block.furnace.entity.RFFurnaceBlockEntity;
import moffy.ticex.block.transmuter.FluidTransmuterBlock;
import moffy.ticex.block.transmuter.entity.FluidTransmuterBlockEntity;
import moffy.ticex.lib.utils.TicEXFluidUtils;
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

        TicEXRegistry.SEARED_RF_FURNACE = TicEXRegistry.BLOCKS.register("seared_rf_furnace", () ->
                new RFFurnaceBlock(TicEXRegistry.SEARED, false)
        );
        TicEXRegistry.SCORCHED_RF_FURNACE = TicEXRegistry.BLOCKS.register("scorched_rf_furnace", () ->
                new RFFurnaceBlock(TicEXRegistry.SCORCHED, false)
        );
        TicEXRegistry.CREATIVE_SEARED_RF_FURNACE = TicEXRegistry.BLOCKS.register("creative_seared_rf_furnace", () ->
                new RFFurnaceBlock(TicEXRegistry.SEARED, true)
        );
        TicEXRegistry.CREATIVE_SCORCHED_RF_FURNACE = TicEXRegistry.BLOCKS.register("creative_scorched_rf_furnace", () ->
                new RFFurnaceBlock(TicEXRegistry.SCORCHED, true)
        );
        TicEXRegistry.FLUID_TRANSMUTER = TicEXRegistry.BLOCKS.register("fluid_transmuter", () ->
                new FluidTransmuterBlock(BlockBehaviour.Properties.of().noOcclusion())
        );

        TicEXRegistry.ITEMS.register("seared_rf_furnace", () ->
                new BlockItem(TicEXRegistry.SEARED_RF_FURNACE.get(), new Item.Properties())
        );
        TicEXRegistry.ITEMS.register("scorched_rf_furnace", () ->
                new BlockItem(TicEXRegistry.SCORCHED_RF_FURNACE.get(), new Item.Properties())
        );
        TicEXRegistry.ITEMS.register("creative_seared_rf_furnace", () ->
                new BlockItem(TicEXRegistry.CREATIVE_SEARED_RF_FURNACE.get(), new Item.Properties())
        );
        TicEXRegistry.ITEMS.register("creative_scorched_rf_furnace", () ->
                new BlockItem(TicEXRegistry.CREATIVE_SCORCHED_RF_FURNACE.get(), new Item.Properties())
        );
        TicEXRegistry.ITEMS.register("fluid_transmuter", () ->
                new BlockItem(TicEXRegistry.FLUID_TRANSMUTER.get(), new Item.Properties())
        );

        TicEXRegistry.RF_FURNACE_ENTITY = TicEXRegistry.BLOCK_ENTITIES.register("rf_furnace_entity", () ->
                BlockEntityType.Builder.of(
                        (BlockPos pPos, BlockState pState) ->
                                new RFFurnaceBlockEntity(TicEXRegistry.RF_FURNACE_ENTITY.get(), pPos, pState, false),
                        TicEXRegistry.SEARED_RF_FURNACE.get(),
                        TicEXRegistry.SCORCHED_RF_FURNACE.get(),
                        TicEXRegistry.CREATIVE_SEARED_RF_FURNACE.get(),
                        TicEXRegistry.CREATIVE_SCORCHED_RF_FURNACE.get()
                ).build(null)
        );

        TicEXRegistry.FLUID_TRANSMUTER_ENTITY = TicEXRegistry.BLOCK_ENTITIES.register("fluid_transmuter", () ->
                BlockEntityType.Builder.of(
                        (BlockPos pPos, BlockState pState) ->
                                new FluidTransmuterBlockEntity(TicEXRegistry.FLUID_TRANSMUTER_ENTITY.get(), pPos, pState),
                        TicEXRegistry.FLUID_TRANSMUTER.get()
                ).build(null)
        );

        for (int i = 0; i < 20; i++) {
            TicEXRegistry.RF_FURNACE_FUELS.add(
                    TicEXRegistry.FLUIDS.register("rf_furnace_fuel_" + i)
                            .type(TicEXFluidUtils.hot("rf_furnace_fuel_" + i).temperature(1000).density(-1600))
                            .unplacable()
            );
        }
    }
}
