package moffy.ticex.client.sprites;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record BaseSpriteSource(
        String source,
        String prefix
) implements SpriteSource {
    public static final Codec<BaseSpriteSource> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("source").forGetter(baseSpriteSource ->
                            baseSpriteSource.source
                    ),

                    Codec.STRING.fieldOf("prefix").forGetter(baseSpriteSource ->
                            baseSpriteSource.prefix
                    )
            ).apply(instance, BaseSpriteSource::new));

    @Override
    public void run(@NotNull ResourceManager resourceManager, @NotNull Output output) {
        FileToIdConverter filetoidconverter = new FileToIdConverter("textures/" + this.source, ".png");
        filetoidconverter.listMatchingResources(resourceManager).forEach((rl, resource) -> {
            String[] parsedPath = rl.getPath().split("/");
            String fileName = parsedPath[parsedPath.length - 1];
            String[] parsedFileName = fileName.split("_");
            if(parsedFileName.length < 2 || !ModList.get().isLoaded(parsedFileName[parsedFileName.length - 2])){
                ResourceLocation resourcelocation = filetoidconverter.fileToId(rl).withPrefix(this.prefix);
                output.add(resourcelocation, resource);
            }
        });
    }

    @Override
    public @NotNull SpriteSourceType type() {
        return TicEXSpriteSources.BASE_TEXTURE;
    }
}
