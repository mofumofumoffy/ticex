package moffy.ticex.client.modules.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import moffy.ticex.client.rendering.ticex.ItemArrowRenderer;
import moffy.ticex.entity.ItemArrow;
import moffy.ticex.lib.utils.TicEXSBUtils;
import moffy.ticex.lib.utils.TicEXTaczUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

public class ResonanceToolProjectileRenderer extends ItemArrowRenderer {
    private final ItemRenderer itemRenderer;

    public ResonanceToolProjectileRenderer(EntityRendererProvider.Context pContext, float pScale) {
        super(pContext, pScale);
        this.itemRenderer = pContext.getItemRenderer();
    }

    @Override
    public void render(ItemArrow pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();

        pPoseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.yRotO, pEntity.getYRot()) - 90.0F));
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot())- 45.0F));


        pPoseStack.scale(2.5f, 2.5f, 2.5f);

        ItemStack toolStack = pEntity.getItem();

        boolean isNormalRender = true;
        if(ModList.get().isLoaded("slashblade")){
            isNormalRender = !TicEXSBUtils.renderBladeTool(toolStack, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
        }

        if(ModList.get().isLoaded("tacz")){
            isNormalRender = isNormalRender && !TicEXTaczUtils.renderGunTool(itemRenderer,toolStack, pPoseStack, pBuffer, pEntity, pPackedLight);
        }

        if(isNormalRender){

            itemRenderer.renderStatic(toolStack, ItemDisplayContext.GROUND, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, pEntity.level(), pEntity.getId());

        }
        pPoseStack.popPose();
    }
}
