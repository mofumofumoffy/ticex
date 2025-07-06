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

public class IronsStationSlotLayoutProvider extends AbstractStationSlotLayoutProvider {
    public IronsStationSlotLayoutProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void addLayouts() {
        if (TicEXRegistry.REVIVAL_SPELLBOOK_IRONS != null) {
            defineModifiable((IModifiableDisplay) TicEXRegistry.REVIVAL_SPELLBOOK_IRONS.get())
                    .icon(new ItemStack(TicEXRegistry.BLITZ_GUN.get()))
                    .addInputSlot(new Pattern(TinkerToolParts.toughHandle.getId()), "item.tconstruct.tough_handle", 13, 29,
                            Ingredient.of(TinkerToolParts.toughHandle))
                    .addInputSlot(new Pattern(TinkerToolParts.toolBinding.getId()), "item.tconstruct.tool_binding", 13, 53,
                            Ingredient.of(TinkerToolParts.toolBinding))
                    .addInputSlot(new Pattern(TicEXRegistry.CATALYST_IRONS_SPELLBOOK.getId()), "item.ticex.catalyst_irons_spellbook", 33, 44,
                            Ingredient.of(TicEXRegistry.CATALYST_IRONS_SPELLBOOK))
                    .sortIndex(15)
                    .translationKey("gui.ticex.revival_spellbook_irons")
                    .build();
        }
    }

    @Override
    public @NotNull String getName() {
        return "Irons Spellbook Tinker Station Slot Layouts";
    }
}
