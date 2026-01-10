package moffy.ticex.datagen.general.tag;

import java.util.concurrent.CompletableFuture;
import moffy.ticex.TicEX;
import moffy.ticex.lib.TicEXTags;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.common.TinkerTags;

public class FluidTagProvider extends FluidTagsProvider {

    public FluidTagProvider(
        PackOutput pOutput,
        CompletableFuture<Provider> pProvider,
        @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(pOutput, pProvider, TicEX.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(Provider pProvider) {
        fluidTag(TicEXTags.Fluids.INFINITY, ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "molten_infinity"));
        fluidTag(TicEXTags.Fluids.NEUTRON, ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "molten_neutron"));
        fluidTag(TicEXTags.Fluids.CRYSTAL_MATRIX, ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "molten_crystal_matrix"));
        fluidTag(TicEXTags.Fluids.BLAZING, ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "molten_blazing"));

        fluidTag(TicEXTags.Fluids.ETHERIC, ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "molten_etheric"));

        fluidTag(TicEXTags.Fluids.RECONSTRUCTION_CORE, ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "molten_reconstruction_core"));

        fluidTag(
            TinkerTags.Fluids.METAL_TOOLTIPS,
            ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "molten_infinity"),
            ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "molten_neutron"),
            ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "molten_crystal_matrix"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "molten_blazing"),
            ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "molten_etheric")
        );
    }

    private void fluidTag(TagKey<Fluid> tagKey, ResourceLocation... rls) {
        for (ResourceLocation rl : rls) {
            this.tag(tagKey).addOptional(rl);
        }
    }
}
