package moffy.ticex.datagen.tool;

import moffy.ticex.TicEX;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.common.TinkerTags;

public class MaterialTagProvider extends slimeknights.tconstruct.common.data.tags.MaterialTagProvider {

    public MaterialTagProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(TinkerTags.Materials.EXCLUDE_FROM_LOOT).addOptional(
                new ResourceLocation(TicEX.MODID, "infinity"),
                new ResourceLocation(TicEX.MODID, "etheric")
            );
    }
}
