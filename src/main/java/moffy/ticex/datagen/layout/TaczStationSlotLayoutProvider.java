package moffy.ticex.datagen.layout;

import moffy.ticex.TicEX;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.data.tinkering.AbstractStationSlotLayoutProvider;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.tools.TinkerToolParts;

public class TaczStationSlotLayoutProvider extends AbstractStationSlotLayoutProvider {
    public TaczStationSlotLayoutProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void addLayouts() {
        if (TicEXRegistry.BLITZ_GUN != null) {
            defineModifiable((IModifiableDisplay) TicEXRegistry.BLITZ_GUN.get())
                    .icon(new Pattern(TicEXRegistry.BLITZ_GUN.getId()))
                    .addInputSlot(new Pattern(TinkerToolParts.largePlate.getId()), "item.tconstruct.large_plate", 42, 34,
                            Ingredient.of(TinkerToolParts.largePlate))
                    .addInputSlot(new Pattern(TinkerToolParts.toughHandle.getId()), "item.tconstruct.tough_handle", 42, 56,
                            Ingredient.of(TinkerToolParts.toughHandle))
                    .addInputSlot(new Pattern(TicEX.getResource("catalyst")), "item.ticex.catalyst_kinetic_gun", 24, 34,
                            Ingredient.of(TicEXRegistry.CATALYST_KINETIC_GUN))
                    .sortIndex(14)
                    .translationKey("gui.ticex.blitz_gun")
                    .build();
        }
    }

    @Override
    public @NotNull String getName() {
        return "TACZ Tinker Station Slot Layouts";
    }
}
