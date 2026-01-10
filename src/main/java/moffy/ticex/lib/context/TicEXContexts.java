package moffy.ticex.lib.context;

import com.mojang.blaze3d.vertex.VertexConsumer;
import moffy.ticex.client.render.provider.context.ItemRenderContext;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.awt.*;

public class TicEXContexts {
    public static final ContextStack<ItemRenderContext> SB_RENDERING_CONTEXT = new ContextStack<>();
    public static final ContextStack<VertexConsumer> SB_SWAP_VC = new ContextStack<>();
    public static final ContextStack<TextureAtlasSprite> SB_FACE_SPRITE = new ContextStack<>();
    public static final ContextStack<Color> SB_COLOR_OVERRIDE = new ContextStack<>();
}
