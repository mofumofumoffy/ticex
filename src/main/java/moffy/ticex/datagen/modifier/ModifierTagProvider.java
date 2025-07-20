package moffy.ticex.datagen.modifier;

import static slimeknights.tconstruct.common.TinkerTags.Modifiers.ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.BONUS_SLOTLESS;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.DEFENSE;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.EXTRACT_MODIFIER_BLACKLIST;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.EXTRACT_SLOTLESS_BLACKLIST;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.GENERAL_UPGRADES;

import moffy.ticex.TicEX;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierTagProvider;

public class ModifierTagProvider extends AbstractModifierTagProvider {

    public ModifierTagProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, TicEX.MODID, existingFileHelper);
    }

    @Override
    public String getName() {
        return "TiCEX Modifier Tags";
    }

    @Override
    protected void addTags() {
        this.tag(GENERAL_UPGRADES).addOptional(new ResourceLocation(TicEX.MODID, "modem"));

        this.tag(DEFENSE).addOptional(new ResourceLocation(TicEX.MODID, "celestial"));

        this.tag(BONUS_SLOTLESS).addOptional(new ResourceLocation(TicEX.MODID, "inject"));

        this.tag(ABILITIES).addOptional(new ResourceLocation(TicEX.MODID, "embossment"));

        this.tag(EXTRACT_SLOTLESS_BLACKLIST).addOptional(
                new ResourceLocation(TicEX.MODID, "embossment"),
                new ResourceLocation(TicEX.MODID, "mekanic"),
                new ResourceLocation(TicEX.MODID, "konpaku"),
                new ResourceLocation(TicEX.MODID, "koshirae"),
                new ResourceLocation(TicEX.MODID, "hidden_proud"),
                new ResourceLocation(TicEX.MODID, "overload"),
                new ResourceLocation(TicEX.MODID, "override"),
                new ResourceLocation(TicEX.MODID, "lamellar"),
                new ResourceLocation(TicEX.MODID, "flowerstorm")
            );

        this.tag(EXTRACT_MODIFIER_BLACKLIST).addOptional(
                new ResourceLocation(TicEX.MODID, "embossment"),
                new ResourceLocation(TicEX.MODID, "mekanic"),
                new ResourceLocation(TicEX.MODID, "konpaku"),
                new ResourceLocation(TicEX.MODID, "koshirae"),
                new ResourceLocation(TicEX.MODID, "hidden_proud"),
                new ResourceLocation(TicEX.MODID, "overload"),
                new ResourceLocation(TicEX.MODID, "override"),
                new ResourceLocation(TicEX.MODID, "lamellar"),
                new ResourceLocation(TicEX.MODID, "flowerstorm")
            );
    }
}
