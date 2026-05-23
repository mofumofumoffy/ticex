package moffy.ticex.mixin.slashblade;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import jp.nyatla.nymmd.MmdMotionPlayerGL2;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.client.renderer.layers.LayerMainBlade;
import moffy.overloaded_tinkering_lib.client.provider.context.ItemRenderContext;
import moffy.ticex.lib.context.ContextFrame;
import moffy.ticex.lib.context.TicEXContexts;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import slimeknights.tconstruct.library.tools.item.IModifiable;

@Mixin(value = LayerMainBlade.class, remap = false)
public class LayerMainBladeMixin {
    @WrapMethod(method = "lambda$render$4")
    private void renderWith(ISlashBladeState s, LivingEntity entity, float partialTicks, PoseStack matrixStack, float motionYOffset, double motionScale, double modelScaleBase, ItemStack stack, MultiBufferSource bufferIn, int lightIn, MmdMotionPlayerGL2 mmp, Operation<Void> original) {
        if(!(stack.getItem() instanceof IModifiable)) {
            original.call(s, entity, partialTicks, matrixStack, motionYOffset, motionScale, modelScaleBase, stack, bufferIn, lightIn, mmp);
            return;
        }

        ItemRenderContext itemRenderContext = new ItemRenderContext(
                stack,
                ItemDisplayContext.FIXED,
                false,
                matrixStack,
                bufferIn,
                lightIn,
                OverlayTexture.NO_OVERLAY
        );

        try(ContextFrame<ItemRenderContext> local = TicEXContexts.SB_RENDERING_CONTEXT.open(itemRenderContext)) {
            original.call(s, entity, partialTicks, matrixStack, motionYOffset, motionScale, modelScaleBase, stack, bufferIn, lightIn, mmp);
        }
    }
}
