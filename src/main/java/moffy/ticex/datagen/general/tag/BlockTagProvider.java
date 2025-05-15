package moffy.ticex.datagen.general.tag;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import moffy.ticex.TicEX;
import moffy.ticex.lib.TicEXTags;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.common.TinkerTags;

public class BlockTagProvider extends BlockTagsProvider{

    public BlockTagProvider(PackOutput output, CompletableFuture<Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, TicEX.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(Provider pProvider) {
        this.addCommon();
        this.addSmeltery();
    }
    
    private void addCommon() {
        addMetalTags(TicEXTags.Blocks.INFINITY, new ResourceLocation("avaritia", "infinity_block"), true);
        addMetalTags(TicEXTags.Blocks.NEUTRON, new ResourceLocation("avaritia", "neutron_block"), true);
        addMetalTags(TicEXTags.Blocks.CRYSTAL_MATRIX, new ResourceLocation("avaritia", "crystal_matrix_block"), true);

        addMetalTags(TicEXTags.Blocks.ETHERIC, new ResourceLocation(TicEX.MODID, "etheric_block"), true);
    }

    private void addSmeltery() {
        this.tag(TinkerTags.Blocks.SEARED_TANKS).add(TicEXRegistry.SEARED_RF_FURNACE.get(), TicEXRegistry.CREATIVE_SEARED_RF_FURNACE.get());
        this.tag(TinkerTags.Blocks.SCORCHED_TANKS).add(TicEXRegistry.SCORCHED_RF_FURNACE.get(), TicEXRegistry.CREATIVE_SCORCHED_RF_FURNACE.get());
    }

    private void addMetalTags(TagKey<Block>tagKey, ResourceLocation location, boolean beacon) {
        if (beacon) {
            this.tag(BlockTags.BEACON_BASE_BLOCKS).addOptional(location);
        }
        this.tag(tagKey).addOptional(location);
    }
}
