package moffy.ticex.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.client.armor.AbstractArmorModel;

public abstract class QuadRenderContext {
    public record ToolQuadRenderContext(
            RenderType renderType,
            BakedQuad quad,
            float red, float green, float blue, float alpha,
            ItemStack itemStack,
            ItemDisplayContext displayContext,
            boolean leftHand,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int combinedLight,
            int combinedOverlay) {

        public void renderQuadNaked() {
            renderQuadOverrided(bufferSource.getBuffer(renderType));
        }

        public void renderQuadOverrided(VertexConsumer vc) {
            renderQuadOverrided(vc, red, green, blue, quad);
        }

        public void renderQuadOverrided(VertexConsumer vc, float red, float green, float blue, BakedQuad quad) {
            renderQuadOverrided(vc, red, green, blue, combinedLight, combinedOverlay, quad, poseStack);
        }

        public void renderQuadOverrided(
                VertexConsumer vc,
                float red,
                float green,
                float blue,
                int light,
                int overlay,
                BakedQuad quad,
                PoseStack poseStack
        ) {
            vc.putBulkData(poseStack.last(), quad, red, green, blue, 1.0F, light, overlay, true);
        }
    }

    public record ArmorPartRenderContext(Model model, PoseStack matrices, MultiBufferSource bufferSource,
                                         int packedLight, int packedOverlay, float red, float green, float blue,
                                         float alpha, boolean hasGlint, Material material, int color) {

        public void renderArmorNaked() {
            renderArmorOverrided(
                    ItemRenderer.getArmorFoilBuffer(bufferSource, RenderType.armorCutoutNoCull(material.texture()), false, hasGlint)
            );
        }

        public void renderArmorOverrided(VertexConsumer buffer) {
            renderArmorOverrided(buffer, red, green, blue);
        }

        public void renderArmorOverrided(
                VertexConsumer buffer,
                float red,
                float green,
                float blue
        ) {
            renderArmorOverrided(
                    buffer,
                    model,
                    matrices,
                    packedLight,
                    packedOverlay,
                    red,
                    green,
                    blue,
                    blue
            );
        }

        public void renderArmorOverrided(
                VertexConsumer buffer,
                Model model,
                PoseStack matrices,
                int packedLight,
                int packedOverlay,
                float red,
                float green,
                float blue,
                float alpha
        ) {
            AbstractArmorModel.renderColored(
                    model,
                    matrices,
                    buffer,
                    packedLight,
                    packedOverlay,
                    color,
                    red,
                    green,
                    blue,
                    alpha
            );
        }
    }
}
