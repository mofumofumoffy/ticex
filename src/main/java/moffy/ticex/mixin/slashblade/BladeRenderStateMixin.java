package moffy.ticex.mixin.slashblade;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.util.BladeRenderState;
import moffy.ticex.client.render.slashblade.TicEXSBRenderers;
import moffy.ticex.lib.context.TicEXContexts;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;

@Mixin(value = BladeRenderState.class, remap = false)
@Debug(export = true)
public abstract class BladeRenderStateMixin {
    @WrapMethod(method = "renderOverrided(Lnet/minecraft/world/item/ItemStack;Lmods/flammpfeil/slashblade/client/renderer/model/obj/WavefrontObject;Ljava/lang/String;Lnet/minecraft/resources/ResourceLocation;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILjava/util/function/Function;Z)V")
    private static void renderOverridedWrapped(ItemStack stack, WavefrontObject model, String target, ResourceLocation texture, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, Function<ResourceLocation, RenderType> renderTypeGetter, boolean enableEffect, Operation<Void> original) {
        TicEXSBRenderers.renderWrapped(original::call, stack, model, target, texture, matrixStackIn, bufferIn, packedLightIn, renderTypeGetter, enableEffect);
    }

    @WrapOperation(
            method = "renderOverrided(Lnet/minecraft/world/item/ItemStack;Lmods/flammpfeil/slashblade/client/renderer/model/obj/WavefrontObject;Ljava/lang/String;Lnet/minecraft/resources/ResourceLocation;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILjava/util/function/Function;Z)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;", ordinal = 0))
    private static VertexConsumer swapBuffer(MultiBufferSource instance, RenderType renderType, Operation<VertexConsumer> original) {
        VertexConsumer vertexConsumer = TicEXContexts.SB_SWAP_VC.get();
        return vertexConsumer != null ? vertexConsumer : original.call(instance, renderType);
    }
}
