package moffy.ticex.datagen.layout;

import moffy.ticex.TicEX;
import moffy.ticex.lib.TicEXTags;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.data.tinkering.AbstractStationSlotLayoutProvider;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.layout.Patterns;
import slimeknights.tconstruct.tools.TinkerToolParts;

public class CommonStationSlotLayoutProvider extends AbstractStationSlotLayoutProvider {

    public CommonStationSlotLayoutProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void addLayouts() {
        ResourceLocation seramGear = new ResourceLocation("ticex", "seram_gear");
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
                .addInputSlot(new Pattern(new ResourceLocation(TicEX.MODID, "catalyst")), "tagtype.ticex.catalyst", 13, 44,
                        Ingredient.of(TicEXTags.Items.CATALYSTS))
                .sortIndex(15)
                .translationKey("gui.ticex.seram_gear")
                .build();
    }

    @Override
    public @NotNull String getName() {
        return "TiCEX Tinker Station Slot Layouts";
    }
}
