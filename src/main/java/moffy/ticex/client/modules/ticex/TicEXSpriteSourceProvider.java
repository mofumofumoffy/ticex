package moffy.ticex.client.modules.ticex;

import moffy.ticex.TicEX;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;

public class TicEXSpriteSourceProvider extends SpriteSourceProvider {
    public TicEXSpriteSourceProvider(PackOutput output, ExistingFileHelper fileHelper) {
        super(output, fileHelper, TicEX.MODID);
    }

    @Override
    protected void addSources() {
        SourceList atlas = atlas(BLOCKS_ATLAS);
        atlas.addSource(new DirectoryLister("entity", "entity/"));
        atlas.addSource(new DirectoryLister("tinker_armor", "tinker_armor/"));
        atlas.addSource(new DirectoryLister("obj_tool", "obj_tool/"));
    }
}
