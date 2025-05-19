package moffy.ticex.datagen.general;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import moffy.ticex.TicEX;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class LootProvider extends LootTableProvider{

    private static final Set<ResourceLocation> REQUIRED_TABLES = Set.of();

    public LootProvider(PackOutput output) {
        super(output, REQUIRED_TABLES, List.of(
            new LootTableProvider.SubProviderEntry(BlockLootTableProvider::new, LootContextParamSets.BLOCK)
        ));
    }

    public static class BlockLootTableProvider extends BlockLootSubProvider{
        public BlockLootTableProvider() {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags());
        }

        @SuppressWarnings("deprecation")
        @Override
        protected Iterable<Block> getKnownBlocks() {
            return BuiltInRegistries.BLOCK.stream()
                                        .filter(block -> TicEX.MODID.equals(BuiltInRegistries.BLOCK.getKey(block).getNamespace()))
                                        .collect(Collectors.toList());
        }


        @Override
        protected void generate() {
            this.addCommon();
            this.addSmeltery();
        }

        private void addCommon() {
            //materials
            dropSelf(TicEXRegistry.ETHERIC_BLOCK.get());
        }

        private void addSmeltery() {
            dropSelf(TicEXRegistry.SEARED_RF_FURNACE.get());
            dropSelf(TicEXRegistry.CREATIVE_SEARED_RF_FURNACE.get());
            dropSelf(TicEXRegistry.SCORCHED_RF_FURNACE.get());
            dropSelf(TicEXRegistry.CREATIVE_SCORCHED_RF_FURNACE.get());
        }
    }
}
