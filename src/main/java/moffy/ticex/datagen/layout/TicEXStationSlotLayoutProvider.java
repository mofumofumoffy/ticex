package moffy.ticex.datagen.layout;

import net.minecraft.data.PackOutput;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.data.tinkering.AbstractStationSlotLayoutProvider;

public class TicEXStationSlotLayoutProvider extends AbstractStationSlotLayoutProvider {
    private final CommonStationSlotLayoutProvider commonLayoutProvider;
    private final TaczStationSlotLayoutProvider taczLayoutProvider;
    private final SlashBladeStationSlotLayoutProvider slashBladeLayoutProvider;
    private final IronsStationSlotLayoutProvider ironsBladeLayoutProvider;

    public TicEXStationSlotLayoutProvider(PackOutput packOutput) {
        super(packOutput);
        this.commonLayoutProvider = new CommonStationSlotLayoutProvider(packOutput);
        this.taczLayoutProvider = new TaczStationSlotLayoutProvider(packOutput);
        this.slashBladeLayoutProvider = new SlashBladeStationSlotLayoutProvider(packOutput);
        this.ironsBladeLayoutProvider = new IronsStationSlotLayoutProvider(packOutput);
    }

    @Override
    protected void addLayouts() {
        commonLayoutProvider.addLayouts();
        if (ModList.get().isLoaded("tacz")) taczLayoutProvider.addLayouts();
        if (ModList.get().isLoaded("slashblade")) slashBladeLayoutProvider.addLayouts();
        if (ModList.get().isLoaded("irons_spellbooks")) ironsBladeLayoutProvider.addLayouts();
    }

    @Override
    public @NotNull String getName() {
        return "TiCEX Tinker Station Slot Layouts";
    }
}
