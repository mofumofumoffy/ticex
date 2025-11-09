package moffy.ticex.client.render.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import moffy.ticex.client.render.ticex.ItemArrowRenderer;
import moffy.ticex.client.render.ticex.TicEXRenderUtils;
import moffy.ticex.entity.ItemArrow;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ResonanceToolProjectileRenderer extends ItemArrowRenderer {
    private final ItemRenderer itemRenderer;

    public ResonanceToolProjectileRenderer(EntityRendererProvider.Context pContext, float pScale) {
        super(pContext, pScale);
        this.itemRenderer = pContext.getItemRenderer();
    }

    @Override
    public void render(ItemArrow pEntity, float pEntityYaw, float pPartialTicks, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();

        pPoseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.yRotO, pEntity.getYRot()) - 90.0F));
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot())- 45.0F));


        pPoseStack.scale(2.5f, 2.5f, 2.5f);

        ItemStack toolStack = pEntity.getItem();

        TicEXRenderUtils.renderTool(entityRenderDispatcher, itemRenderer, toolStack, pPoseStack, pBuffer, pEntity, pPackedLight);

        pPoseStack.popPose();
    }
}
