package moffy.ticex.client.render.ticex;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import moffy.ticex.client.render.slashblade.SBItemEntityRenderUtils;
import moffy.ticex.item.modifiable.ModifiableGunItem;
import moffy.ticex.item.modifiable.ModifiableSlashBladeItem;
import moffy.ticex.lib.utils.TicEXTaczUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

public class TicEXRenderUtils {

    public static void renderTool(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer, ItemStack toolStack, PoseStack pPoseStack, MultiBufferSource pBuffer, Entity pEntity, int pPackedLight) {
        if(ModList.get().isLoaded("slashblade") && toolStack.getItem() instanceof ModifiableSlashBladeItem){
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(45f));
            pPoseStack.mulPose(Axis.YP.rotationDegrees(180f));
            SBItemEntityRenderUtils.render(entityRenderDispatcher, toolStack, pEntity.level(), pPoseStack, pBuffer, pPackedLight);
            return;
        }

        if(ModList.get().isLoaded("tacz") && toolStack.getItem() instanceof ModifiableGunItem){
            if(TicEXTaczUtils.renderGunTool(itemRenderer, toolStack, pPoseStack, pBuffer, pEntity, pPackedLight)) {
                return;
            }
        }

        itemRenderer.renderStatic(toolStack, ItemDisplayContext.GROUND, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, pEntity.level(), pEntity.getId());
    }
}
