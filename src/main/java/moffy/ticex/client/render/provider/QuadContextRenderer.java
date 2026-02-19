package moffy.ticex.client.render.provider;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moffy.ticex.client.render.provider.renderer.IQuadContextRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;

public class QuadContextRenderer implements IQuadContextRenderer {
    public static final QuadContextRenderer RENDERER = new QuadContextRenderer();

    @Override
    public void renderQuad(BakedQuad quad, VertexConsumer consumer, float red, float green, float blue, float alpha, PoseStack poseStack, int combinedLight, int combinedOverlay) {
        consumer.putBulkData(
                poseStack.last(),
                quad,
                red, green, blue,
                combinedLight, combinedOverlay
        );
    }
}
