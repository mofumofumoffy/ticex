package moffy.ticex.client.render.provider.context.tool;

import com.mojang.blaze3d.vertex.VertexConsumer;
import moffy.ticex.client.render.provider.context.RenderContext;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public record RenderGenericContext(
        RenderContext renderContext,
        ResourceLocation atlasLocation,
        RenderType renderType,
        BufferGetter bufferGetter,
        boolean onGui
) {
    public interface BufferGetter {
        VertexConsumer get(RenderType renderType);
    }
}
