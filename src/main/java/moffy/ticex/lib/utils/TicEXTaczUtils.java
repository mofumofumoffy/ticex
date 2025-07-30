package moffy.ticex.lib.utils;

/*
 * This file is part of the TicEXTaczModule.
 *
 * Licensed under the GNU General Public License v3.0.
 * See the LICENSES/GPL-3.0.md file for details.
 * 2025 Moffy
 */

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import moffy.ticex.item.modifiable.ModifiableGunItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TicEXTaczUtils {
    @OnlyIn(Dist.CLIENT)
    public static boolean renderGunTool(ItemRenderer itemRenderer, ItemStack stack, PoseStack pPoseStack, MultiBufferSource pBuffer, LivingEntity pLivingEntity, int pPackedLight){
        if(stack.getItem() instanceof ModifiableGunItem){
            pPoseStack.mulPose(Axis.YP.rotationDegrees(135f));
            itemRenderer.renderStatic(stack, ItemDisplayContext.GROUND, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, pLivingEntity.level(), pLivingEntity.getId());
            return true;
        }
        return false;
    }
}
