package moffy.ticex.client.sprites;

import moffy.ticex.TicEX;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;

public class TicEXSpriteSources {
    public static SpriteSourceType BASE_TEXTURE;

    public static void register() {
        BASE_TEXTURE = SpriteSources.register(
                TicEX.getResource("base_texture").toString(),
                BaseSpriteSource.CODEC
        );
    }
}
