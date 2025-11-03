package moffy.ticex.client.render.slashblade;

import com.mojang.blaze3d.vertex.PoseStack;
import mods.flammpfeil.slashblade.client.renderer.SlashBladeTEISR;
import moffy.ticex.client.render.provider.context.ItemRenderContext;
import moffy.ticex.lib.context.ContextFrame;
import moffy.ticex.lib.context.TicEXContexts;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SBToolISTER extends SlashBladeTEISR {

    public SBToolISTER(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
    }

    @Override
    public void renderByItem(ItemStack itemStackIn, ItemDisplayContext type, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        ItemRenderContext itemRenderContext = new ItemRenderContext(
                itemStackIn,
                type,
                type == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ||
                        type == ItemDisplayContext.THIRD_PERSON_LEFT_HAND,
                matrixStack,
                bufferIn,
                combinedLightIn,
                combinedOverlayIn
        );

        try(ContextFrame<ItemRenderContext> local = TicEXContexts.RENDERING_CONTEXT.open(itemRenderContext)) {
            super.renderByItem(itemStackIn, type, matrixStack, bufferIn, combinedLightIn, combinedOverlayIn);
        }
    }
}
