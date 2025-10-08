package moffy.ticex.client.render.provider.context;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public record RenderContext(
        MultiBufferSource bufferSource,
        float red, float green, float blue, float alpha,
        PoseStack poseStack,
        int combinedLight,
        int combinedOverlay) {

    public VertexConsumer getBuffer(RenderType type) {
        return bufferSource.getBuffer(type);
    }
}
