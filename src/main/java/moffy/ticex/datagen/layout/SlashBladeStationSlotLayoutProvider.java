package moffy.ticex.datagen.layout;

import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.data.tinkering.AbstractStationSlotLayoutProvider;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.tools.TinkerToolParts;

public class SlashBladeStationSlotLayoutProvider extends AbstractStationSlotLayoutProvider {
    public SlashBladeStationSlotLayoutProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void addLayouts() {
        if (TicEXRegistry.REFORGED_SLASHBLADE != null) {
            defineModifiable((IModifiableDisplay) TicEXRegistry.REFORGED_SLASHBLADE.get())
                    .icon(new ItemStack(TicEXRegistry.REFORGED_SLASHBLADE.get()))
                    .addInputSlot(new Pattern(TicEXRegistry.SLASHBLADE_BLADE.getId()), "item.ticex.slashblade_blade", 21, 56,
                            Ingredient.of(TicEXRegistry.SLASHBLADE_BLADE))
                    .addInputSlot(new Pattern(TicEXRegistry.SLASHBLADE_SAYA.getId()), "item.ticex.slashblade_saya", 39, 44,
                            Ingredient.of(TicEXRegistry.SLASHBLADE_SAYA))
                    .addInputSlot(new Pattern(TinkerToolParts.toughHandle.getId()), "item.tconstruct.tough_handle", 21, 34,
                            Ingredient.of(TinkerToolParts.toughHandle))
                    .sortIndex(14)
                    .translationKey("gui.ticex.reforged_slashblade")
                    .build();
        }
    }

    @Override
    public @NotNull String getName() {
        return "SlashBlade Tinker Station Slot Layouts";
    }
}
