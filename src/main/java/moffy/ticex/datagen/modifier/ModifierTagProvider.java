package moffy.ticex.datagen.modifier;

import moffy.ticex.TicEX;
import moffy.ticex.lib.TicEXTags;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierTagProvider;

import static slimeknights.tconstruct.common.TinkerTags.Modifiers.*;

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
        this.tag(GENERAL_UPGRADES).addOptional(TicEX.getResource("modem"));

        this.tag(DEFENSE).addOptional(TicEX.getResource("celestial"));

        this.tag(BONUS_SLOTLESS).addOptional(TicEX.getResource("inject"));

        this.tag(ABILITIES).addOptional(TicEX.getResource("embossment"));

        this.tag(EXTRACT_SLOTLESS_BLACKLIST).addOptional(
                TicEX.getResource("rebirth"),
                TicEX.getResource("embossment"),
                TicEX.getResource("mekanic"),
                TicEX.getResource("konpaku"),
                TicEX.getResource("koshirae"),
                TicEX.getResource("hidden_proud"),
                TicEX.getResource("overload"),
                TicEX.getResource("override"),
                TicEX.getResource("reactive"),
                TicEX.getResource("apoth_supplier"),
                TicEX.getResource("enchantment_supplier")
            );

        this.tag(EXTRACT_MODIFIER_BLACKLIST).addOptional(
                TicEX.getResource("rebirth"),
                TicEX.getResource("embossment"),
                TicEX.getResource("mekanic"),
                TicEX.getResource("konpaku"),
                TicEX.getResource("koshirae"),
                TicEX.getResource("hidden_proud"),
                TicEX.getResource("overload"),
                TicEX.getResource("override"),
                TicEX.getResource("reactive"),
                TicEX.getResource("apoth_supplier"),
                TicEX.getResource("enchantment_supplier")
            );

        this.tag(TicEXTags.Modifiers.REMOVAL_BLACKLIST).addOptional(
                TicEX.getResource("rebirth"),
                TicEX.getResource("embossment"),
                TicEX.getResource("mekanic"),
                TicEX.getResource("konpaku"),
                TicEX.getResource("koshirae"),
                TicEX.getResource("hidden_proud"),
                TicEX.getResource("overload"),
                TicEX.getResource("override"),
                TicEX.getResource("reactive"),
                TicEX.getResource("apoth_supplier"),
                TicEX.getResource("enchantment_supplier")
        );

        this.tag(TicEXTags.Modifiers.REBIRTH).addOptional(
                TicEX.getResource("rebirth")
        );

        this.tag(TicEXTags.Modifiers.REBIRTH_BASED).addOptional(
                TicEX.getResource("mekanic"),
                TicEX.getResource("koshirae"),
                TicEX.getResource("abyssal"),
                TicEX.getResource("infernal"),
                TicEX.getResource("gravity"),
                TicEX.getResource("hurricane"),
                TicEX.getResource("overcasting")
        );
    }
}
