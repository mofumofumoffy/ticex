package moffy.ticex.datagen.layout;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayoutLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TicEXStationSlotLayoutProvider extends GenericDataProvider {
    private final CommonStationSlotLayoutProvider commonLayoutProvider;
    private final TaczStationSlotLayoutProvider taczLayoutProvider;
    private final SlashBladeStationSlotLayoutProvider slashBladeLayoutProvider;
    private final IronsStationSlotLayoutProvider ironsBladeLayoutProvider;
    private final MekanismSlotLayoutProvider mekanismLayoutProvider;

    public TicEXStationSlotLayoutProvider(PackOutput packOutput) {
        super(packOutput, PackOutput.Target.DATA_PACK, "tinkering/station_layouts", StationSlotLayoutLoader.GSON);
        this.commonLayoutProvider = new CommonStationSlotLayoutProvider(packOutput);
        this.taczLayoutProvider = new TaczStationSlotLayoutProvider(packOutput);
        this.slashBladeLayoutProvider = new SlashBladeStationSlotLayoutProvider(packOutput);
        this.ironsBladeLayoutProvider = new IronsStationSlotLayoutProvider(packOutput);
        this.mekanismLayoutProvider = new MekanismSlotLayoutProvider(packOutput);
    }

    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        List<CompletableFuture<?>> tasks = new ArrayList<>();

        tasks.add(commonLayoutProvider.run(cache));
        if (ModList.get().isLoaded("tacz")) tasks.add(taczLayoutProvider.run(cache));
        if (ModList.get().isLoaded("slashblade")) tasks.add(slashBladeLayoutProvider.run(cache));
        if (ModList.get().isLoaded("irons_spellbooks")) tasks.add(ironsBladeLayoutProvider.run(cache));
        if (ModList.get().isLoaded("mekanism")) tasks.add(mekanismLayoutProvider.run(cache));

        return allOf(tasks);
    }

    @Override
    public @NotNull String getName() {
        return "TiCEX Tinker Station Slot Layouts";
    }
}
