package moffy.ticex.client.render.slashblade;

import com.mojang.blaze3d.vertex.PoseStack;
import mods.flammpfeil.slashblade.client.renderer.entity.BladeItemEntityRenderer;
import moffy.ticex.client.render.provider.context.ItemRenderContext;
import moffy.ticex.lib.context.ContextFrame;
import moffy.ticex.lib.context.TicEXContexts;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemDisplayContext;

public class SBToolBladeItemRenderer extends BladeItemEntityRenderer {

    public SBToolBladeItemRenderer(Context context) {
        super(context);
    }

    @Override
    public void render(ItemEntity itemIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        ItemRenderContext itemRenderContext = new ItemRenderContext(
                itemIn.getItem(),
                ItemDisplayContext.GROUND,
                false,
                matrixStackIn,
                bufferIn,
                packedLightIn,
                OverlayTexture.NO_OVERLAY
        );

        try(ContextFrame<ItemRenderContext> local = TicEXContexts.SB_RENDERING_CONTEXT.open(itemRenderContext)) {
            super.render(itemIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        }
    }
}
