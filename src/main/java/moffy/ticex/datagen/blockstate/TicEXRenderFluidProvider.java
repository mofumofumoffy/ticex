package moffy.ticex.datagen.blockstate;

import moffy.ticex.TicEX;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.render.FluidCuboid;
import slimeknights.mantle.data.datamap.BlockStateDataMapProvider;

import java.util.List;

public class TicEXRenderFluidProvider extends BlockStateDataMapProvider<List<FluidCuboid>> {
    public TicEXRenderFluidProvider(PackOutput output) {
        super(output, PackOutput.Target.RESOURCE_PACK, FluidCuboid.REGISTRY, TicEX.MODID);
    }

    @Override
    protected void addEntries() {
        block(TicEXRegistry.FLUID_TRANSMUTER).variant(List.of(
                FluidCuboid.builder()
                        .from(0.08f, 6.08f, 0.08f)
                        .to(15.92f, 15.92f, 15.92f)
                        .build()));
    }

    @Override
    public @NotNull String getName() {
        return "TicEX block render fluid provider";
    }
}
