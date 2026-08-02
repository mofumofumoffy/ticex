package moffy.ticex.mixin.slashblade;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import jp.nyatla.nymmd.MmdMotionPlayerGL2;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.client.renderer.layers.LayerMainBlade;
import moffy.ticex.client.render.provider.context.ItemRenderContext;
import moffy.ticex.lib.context.ContextFrame;
import moffy.ticex.lib.context.TicEXContexts;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.tools.item.IModifiable;

@Mixin(value = LayerMainBlade.class, remap = false, priority = 2000)
public class LayerMainBladeMixin {
    @WrapMethod(method = "renderOffhandItem")
    private void renderOffhandWith(PoseStack matrixStack, MultiBufferSource bufferIn, int lightIn, LivingEntity entity, Operation<Void> original){
        ItemStack offhandStack = entity.getItemInHand(InteractionHand.OFF_HAND);

        if(!(offhandStack.getItem() instanceof IModifiable)) {
            original.call(matrixStack, bufferIn, lightIn, entity);
            return;
        }

        ItemRenderContext itemRenderContext = new ItemRenderContext(
                offhandStack,
                ItemDisplayContext.FIXED,
                false,
                matrixStack,
                bufferIn,
                lightIn,
                OverlayTexture.NO_OVERLAY
        );

        try(ContextFrame<ItemRenderContext> local = TicEXContexts.SB_RENDERING_CONTEXT.open(itemRenderContext)) {
            original.call(matrixStack, bufferIn, lightIn, entity);
        }
    }

    @WrapMethod(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V")
    public void renderWith(@NotNull PoseStack matrixStack, @NotNull MultiBufferSource bufferIn, int lightIn, @NotNull LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, Operation<Void> original){
        ItemStack stack = entity.getItemInHand(InteractionHand.MAIN_HAND);
        if(!(stack.getItem() instanceof IModifiable)) {
            original.call(matrixStack, bufferIn, lightIn, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
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
            original.call(matrixStack, bufferIn, lightIn, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }
    }

/*    @WrapMethod(method = "lambda$render$4")
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
    }*/
}
