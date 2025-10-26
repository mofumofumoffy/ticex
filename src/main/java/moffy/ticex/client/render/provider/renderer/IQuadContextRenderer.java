package moffy.ticex.client.render.provider.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moffy.ticex.client.render.provider.context.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;

public interface IQuadContextRenderer {
    default void render(RenderContext context, BakedQuad bakedQuad, VertexConsumer vertexConsumer) {
        this.renderQuad(
                bakedQuad,
                vertexConsumer,
                context.red(),
                context.green(),
                context.blue(),
                context.alpha(),
                context.poseStack(),
                context.combinedLight(),
                context.combinedOverlay()
        );
    }

    default void render(RenderContext context, BakedQuad bakedQuad, VertexConsumer vertexConsumer,
                        float red, float green, float blue, float alpha) {
        this.renderQuad(
                bakedQuad,
                vertexConsumer,
                red,
                green,
                blue,
                alpha,
                context.poseStack(),
                context.combinedLight(),
                context.combinedOverlay()
        );
    }

    void renderQuad(BakedQuad quad, VertexConsumer consumer,
                    float red, float green, float blue, float alpha,
                    PoseStack poseStack,
                    int combinedLight, int combinedOverlay);
}
