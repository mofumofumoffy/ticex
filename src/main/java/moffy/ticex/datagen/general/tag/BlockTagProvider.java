package moffy.ticex.datagen.general.tag;

import moffy.ticex.TicEX;
import moffy.ticex.lib.TicEXTags;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.common.TinkerTags;

import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends BlockTagsProvider {

    public BlockTagProvider(
        PackOutput output,
        CompletableFuture<Provider> lookupProvider,
        @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(output, lookupProvider, TicEX.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(Provider pProvider) {
        this.addCommon();
        this.addSmeltery();
    }

    private void addCommon() {
        addMetalTags(TicEXTags.Blocks.INFINITY, ResourceLocation.fromNamespaceAndPath("avaritia", "infinity"), true);
        addMetalTags(TicEXTags.Blocks.NEUTRON, ResourceLocation.fromNamespaceAndPath("avaritia", "neutron"), true);
        addMetalTags(TicEXTags.Blocks.CRYSTAL_MATRIX, ResourceLocation.fromNamespaceAndPath("avaritia", "crystal_matrix"), true);

        addMetalTags(TicEXTags.Blocks.ETHERIC, TicEX.getResource("etheric_block"), true);

        tag(TicEXTags.Blocks.FLUID_TRANSMUTER_TANK)
                .addOptionalTag(TinkerTags.Blocks.ALLOYER_TANKS);

        addPickaxeBlock(BlockTags.NEEDS_IRON_TOOL, TicEX.getResource("etheric_block"));

        addPickaxeBlock(BlockTags.NEEDS_STONE_TOOL,
            TicEX.getResource("seared_rf_furnace"),
            TicEX.getResource("scorched_rf_furnace"),
            TicEX.getResource("creative_seared_rf_furnace"),
            TicEX.getResource("creative_scorched_rf_furnace")
        );
    }

    private void addPickaxeBlock(TagKey<Block> tool, ResourceLocation ...blocks){
        for(ResourceLocation block : blocks){
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE).addOptional(block);
            this.tag(tool).addOptional(block);
        }
    }

    private void addSmeltery() {
        this.tag(TinkerTags.Blocks.SEARED_TANKS).add(
                TicEXRegistry.SEARED_RF_FURNACE.get(),
                TicEXRegistry.CREATIVE_SEARED_RF_FURNACE.get()
            );
        this.tag(TinkerTags.Blocks.SCORCHED_TANKS).add(
                TicEXRegistry.SCORCHED_RF_FURNACE.get(),
                TicEXRegistry.CREATIVE_SCORCHED_RF_FURNACE.get()
            );
    }

    private void addMetalTags(TagKey<Block> tagKey, ResourceLocation location, boolean beacon) {
        if (beacon) {
            this.tag(BlockTags.BEACON_BASE_BLOCKS).addOptional(location);
        }
        this.tag(tagKey).addOptional(location);
    }
}
