package moffy.ticex.datagen.modifier;

import moffy.ticex.TicEX;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierTagProvider;

import static slimeknights.tconstruct.common.TinkerTags.Modifiers.DEFENSE;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.GENERAL_UPGRADES;

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
        this.tag(GENERAL_UPGRADES).addOptional(
            new ResourceLocation(TicEX.MODID,"modem")
        );

        this.tag(DEFENSE).addOptional(
            new ResourceLocation(TicEX.MODID, "celestial")
        );
    }
    
}
