package moffy.ticex.datagen.modifier;

import moffy.ticex.TicEX;
import moffy.ticex.lib.TicEXTags;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
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
        this.tag(GENERAL_UPGRADES).addOptional(ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "modem"));

        this.tag(DEFENSE).addOptional(ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "celestial"));

        this.tag(BONUS_SLOTLESS).addOptional(ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "inject"));

        this.tag(ABILITIES).addOptional(ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "embossment"));

        this.tag(EXTRACT_SLOTLESS_BLACKLIST).addOptional(
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "embossment"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "mekanic"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "konpaku"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "koshirae"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "hidden_proud"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "overload"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "override"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "reactive"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "apoth_supplier"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "enchantment_supplier")
            );

        this.tag(EXTRACT_MODIFIER_BLACKLIST).addOptional(
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "embossment"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "mekanic"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "konpaku"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "koshirae"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "hidden_proud"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "overload"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "override"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "reactive"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "apoth_supplier"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "enchantment_supplier")
            );

        this.tag(TicEXTags.Modifiers.REMOVAL_BLACKLIST).addOptional(
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "embossment"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "mekanic"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "konpaku"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "koshirae"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "hidden_proud"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "overload"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "override"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "reactive"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "apoth_supplier"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "enchantment_supplier")
        );

        this.tag(TicEXTags.Modifiers.REBIRTH).addOptional(
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "rebirth")
        );

        this.tag(TicEXTags.Modifiers.REBIRTH_BASED).addOptional(
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "mekanic"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "koshirae"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "abyssal"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "infernal"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "gravity"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "hurricane"),
                ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "overcasting")
        );
    }
}
