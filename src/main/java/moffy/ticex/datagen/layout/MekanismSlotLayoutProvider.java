package moffy.ticex.datagen.layout;

import moffy.ticex.TicEX;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.data.tinkering.AbstractStationSlotLayoutProvider;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.tools.TinkerToolParts;

public class MekanismSlotLayoutProvider extends AbstractStationSlotLayoutProvider {
    public MekanismSlotLayoutProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void addLayouts() {
        if (TicEXRegistry.MEKA_EDGE != null) {
            defineModifiable(TicEXRegistry.MEKA_EDGE.get())
                    .icon(new Pattern(TicEXRegistry.MEKA_EDGE.getId()))
                    .addInputSlot(new Pattern(TinkerToolParts.broadBlade.getId()), "item.tconstruct.broad_blade", 42, 34,
                            Ingredient.of(TinkerToolParts.broadBlade))
                    .addInputSlot(new Pattern(TinkerToolParts.toughHandle.getId()), "item.tconstruct.tough_handle", 24, 56,
                            Ingredient.of(TinkerToolParts.toughHandle))
                    .addInputSlot(new Pattern(new ResourceLocation(TicEX.MODID, "catalyst")), "item.ticex.catalyst_meka_tool.json", 24, 34,
                            Ingredient.of(TicEXRegistry.CATALYST_MEKA_TOOL))
                    .sortIndex(14)
                    .translationKey("gui.ticex.meka_tool")
                    .build();
        }
    }

    @Override
    public @NotNull String getName() {
        return "Mekanism Tinker Station Slot Layouts";
    }
}
