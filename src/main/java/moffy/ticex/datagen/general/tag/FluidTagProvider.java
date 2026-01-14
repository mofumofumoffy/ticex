package moffy.ticex.datagen.general.tag;

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

import java.util.concurrent.CompletableFuture;

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
        fluidTag(TicEXTags.Fluids.INFINITY, TicEX.getResource("molten_infinity"));
        fluidTag(TicEXTags.Fluids.NEUTRON, TicEX.getResource("molten_neutron"));
        fluidTag(TicEXTags.Fluids.CRYSTAL_MATRIX, TicEX.getResource("molten_crystal_matrix"));
        fluidTag(TicEXTags.Fluids.BLAZING, TicEX.getResource("molten_blazing"));

        fluidTag(TicEXTags.Fluids.ETHERIC, TicEX.getResource("molten_etheric"));

        fluidTag(TicEXTags.Fluids.RECONSTRUCTION_CORE, TicEX.getResource("molten_reconstruction_core"));

        fluidTag(
            TinkerTags.Fluids.METAL_TOOLTIPS,
            TicEX.getResource("molten_infinity"),
            TicEX.getResource("molten_neutron"),
            TicEX.getResource("molten_crystal_matrix"),
                TicEX.getResource("molten_blazing"),
            TicEX.getResource("molten_etheric")
        );
    }

    private void fluidTag(TagKey<Fluid> tagKey, ResourceLocation... rls) {
        for (ResourceLocation rl : rls) {
            this.tag(tagKey).addOptional(rl);
        }
    }
}
