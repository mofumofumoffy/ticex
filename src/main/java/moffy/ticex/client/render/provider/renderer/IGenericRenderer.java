package moffy.ticex.client.render.provider.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moffy.ticex.client.render.provider.context.RenderContext;

public interface IGenericRenderer {
    default void render(VertexConsumer vertexConsumer, RenderContext context) {
        this.render(
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

    default void render(VertexConsumer vertexConsumer, RenderContext context,
                        float red, float green, float blue, float alpha) {
        this.render(
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

    void render(VertexConsumer vertexConsumer,
                float red, float green, float blue, float alpha,
                PoseStack poseStack,
                int combinedLight, int combinedOverlay);
}
