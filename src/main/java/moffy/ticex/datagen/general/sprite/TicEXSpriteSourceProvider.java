package moffy.ticex.datagen.general.sprite;

import moffy.ticex.TicEX;
import moffy.ticex.lib.TicEXMaterials;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;
import slimeknights.tconstruct.library.client.modifiers.TrimModifierModel;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TicEXSpriteSourceProvider extends SpriteSourceProvider {
    private static final String[] TRIMS = {
            "coast", "sentry", "dune", "wild", "ward", "eye", "vex", "tide", "snout",
            "rib", "spire", "wayfinder", "shaper", "silence", "raiser", "host"
    };

    public TicEXSpriteSourceProvider(PackOutput output, ExistingFileHelper fileHelper) {
        super(output, fileHelper, TicEX.MODID);
    }

    @Override
    protected void addSources() {

        String paletteFolder = "trims/color_palettes/";
        String trimFolder = "trims/models/armor/";
        ResourceLocation trimPalette = new ResourceLocation(paletteFolder + "trim_palette");
        Map<String,ResourceLocation> materialMap = Arrays.stream(TicEXMaterials.TRIM_MATERIALS)
                .collect(Collectors.toMap(
                        id -> id.getNamespace() + "_" + id.getPath(),
                        id -> id.withPrefix(paletteFolder))
                );



        atlas(BLOCKS_ATLAS)
                .addSource(new DirectoryLister("entity", "entity/"))
                .addSource(new DirectoryLister("tinker_armor", "tinker_armor/"))
                .addSource(new DirectoryLister("obj_tool", "obj_tool/"))
                .addSource(new PalettedPermutations(
                        List.of(TrimModifierModel.TRIM_TEXTURES),
                        trimPalette, materialMap));

        atlas(new ResourceLocation("armor_trims"))
                .addSource(new PalettedPermutations(
                        Arrays.stream(TRIMS).flatMap(name -> Stream.of(
                                new ResourceLocation(trimFolder + name),
                                new ResourceLocation(trimFolder + name + "_leggings"))
                        ).toList(),
                        trimPalette, materialMap)
                )
                .addSource(new DirectoryLister("misc", "misc/"));
    }
}
