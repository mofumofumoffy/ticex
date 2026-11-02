package moffy.ticex.registry;

import moffy.ticex.block.furnace.entity.RFFurnaceBlockEntity;
import moffy.ticex.block.transmuter.entity.FluidTransmuterBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.tconstruct.smeltery.block.component.SearedBlock;

import java.util.function.IntFunction;

public class TicEXBlocks {
    public static final BlockBehaviour.Properties SEARED;

    static {
        IntFunction<BlockBehaviour.Properties> solidProps = factor ->
                builder(MapColor.COLOR_GRAY, SoundType.METAL)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops()
                        .strength(3.0F * factor, 9.0F * factor)
                        .isValidSpawn(
                                (s, r, p, e) -> !s.hasProperty(SearedBlock.IN_STRUCTURE) || !s.getValue(SearedBlock.IN_STRUCTURE)
                        );
        SEARED = solidProps.apply(1);
    }

    public static final BlockBehaviour.Properties SCORCHED;

    static {
        IntFunction<BlockBehaviour.Properties> solidProps = factor ->
                builder(MapColor.TERRACOTTA_BROWN, SoundType.BASALT)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops()
                        .strength(2.5F * factor, 8.0F * factor)
                        .isValidSpawn(
                                (s, r, p, e) -> !s.hasProperty(SearedBlock.IN_STRUCTURE) || !s.getValue(SearedBlock.IN_STRUCTURE)
                        );
        SCORCHED = solidProps.apply(1);
    }

    public static RegistryObject<Block> ETHERIC_BLOCK = null;
    public static RegistryObject<Block> OD_BLOCK = null;
    public static RegistryObject<Block> ASTRAL_BLOCK = null;
    public static RegistryObject<Block> SEARED_RF_FURNACE = null;
    public static RegistryObject<Block> CREATIVE_SEARED_RF_FURNACE = null;
    public static RegistryObject<Block> SCORCHED_RF_FURNACE = null;
    public static RegistryObject<Block> CREATIVE_SCORCHED_RF_FURNACE = null;
    public static RegistryObject<Block> FLUID_TRANSMUTER = null;

    public static RegistryObject<BlockEntityType<RFFurnaceBlockEntity>> RF_FURNACE_ENTITY = null;
    public static RegistryObject<BlockEntityType<FluidTransmuterBlockEntity>> FLUID_TRANSMUTER_ENTITY = null;

    private static BlockBehaviour.Properties builder(MapColor color, SoundType soundType) {
        return BlockBehaviour.Properties.of().sound(soundType).mapColor(color);
    }
}
