package moffy.ticex.datagen.layout;

import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.data.tinkering.AbstractStationSlotLayoutProvider;

public class CommonStationSlotLayoutProvider extends AbstractStationSlotLayoutProvider {

    public CommonStationSlotLayoutProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void addLayouts() {
        /*ResourceLocation seramGear = ResourceLocation.fromNamespaceAndPath("ticex", "seram_gear");
        define(seramGear)
                .icon(new Pattern(seramGear))
                .addInputSlot(Patterns.PLATING, "pattern.tconstruct.plating", 33, 29,
                        Ingredient.of(
                                TinkerToolParts.plating.get(ArmorItem.Type.HELMET),
                                TinkerToolParts.plating.get(ArmorItem.Type.CHESTPLATE),
                                TinkerToolParts.plating.get(ArmorItem.Type.LEGGINGS),
                                TinkerToolParts.plating.get(ArmorItem.Type.BOOTS)
                        ))
                .addInputSlot(new Pattern(TinkerToolParts.maille.getId()), "item.tconstruct.maille", 33, 53,
                        Ingredient.of(TinkerToolParts.maille))
                .addInputSlot(new Pattern(TicEX.id("catalyst")), "tagtype.ticex.catalyst", 13, 44,
                        Ingredient.of(TicEXTags.Items.CATALYSTS))
                .sortIndex(15)
                .translationKey("gui.ticex.seram_gear")
                .build();*/
    }

    @Override
    public @NotNull String getName() {
        return "TiCEX Tinker Station Slot Layouts";
    }
}
