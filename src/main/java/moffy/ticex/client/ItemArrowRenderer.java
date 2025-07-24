package moffy.ticex.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import moffy.ticex.entity.ItemArrow;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

public class ItemArrowRenderer extends ArrowRenderer<ItemArrow> {

    private final ItemRenderer itemRenderer;
    private final float scale;

    public ItemArrowRenderer(Context pContext, float pScale) {
        super(pContext);
        this.itemRenderer = pContext.getItemRenderer();
        this.scale = pScale;
    }

    @Override
    public void render(
        ItemArrow pEntity,
        float pEntityYaw,
        float pPartialTicks,
        PoseStack pPoseStack,
        MultiBufferSource pBuffer,
        int pPackedLight
    ) {
        if (
            pEntity.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(pEntity) < 12.25D)
        ) {
            pPoseStack.pushPose();
            pPoseStack.scale(this.scale, this.scale, this.scale);
            pPoseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            this.itemRenderer.renderStatic(
                    pEntity.getItem(),
                    ItemDisplayContext.GROUND,
                    pPackedLight,
                    OverlayTexture.NO_OVERLAY,
                    pPoseStack,
                    pBuffer,
                    pEntity.level(),
                    pEntity.getId()
                );
            pPoseStack.popPose();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public ResourceLocation getTextureLocation(ItemArrow pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
