package moffy.ticex.client.providers.context.tool;

import com.mojang.blaze3d.vertex.VertexConsumer;
import moffy.ticex.client.providers.context.RenderContext;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.world.item.ItemStack;

public record RenderQuadContext(
        ItemStack itemStack,
        RenderType renderType,
        RenderContext renderContext,
        BakedQuad quad
) {
    public VertexConsumer getConsumer() {
        return renderContext.getBuffer(renderType);
    }
}
