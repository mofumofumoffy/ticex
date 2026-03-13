package moffy.ticex.client.render.provider.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moffy.ticex.client.render.provider.context.RenderContext;
import net.minecraft.client.model.Model;

public interface IArmorPartContextRenderer {
    default void render(RenderContext context, Model model, VertexConsumer vertexConsumer) {
        this.renderArmorPart(
                model,
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

    default void render(RenderContext context, Model model, VertexConsumer vertexConsumer,
                        float red, float green, float blue, float alpha) {
        this.renderArmorPart(
                model,
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

    void renderArmorPart(Model model, VertexConsumer consumer,
                         float red, float green, float blue, float alpha,
                         PoseStack poseStack,
                         int combinedLight, int combinedOverlay);
}
