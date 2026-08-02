package moffy.ticex.mixin.slashblade;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import mods.flammpfeil.slashblade.client.renderer.layers.LayerMainBlade;
import moffy.ticex.client.render.provider.context.ItemRenderContext;
import moffy.ticex.lib.context.ContextFrame;
import moffy.ticex.lib.context.TicEXContexts;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import slimeknights.tconstruct.library.tools.item.IModifiable;

@Mixin(value = LayerMainBlade.class, remap = false, priority = 2000)
public class LayerMainBladeMixin {
    @WrapOperation(
            method = "renderHotbarItem",
            at = @At(value = "INVOKE", target = "Lmods/flammpfeil/slashblade/client/renderer/layers/LayerMainBlade;renderStandbyBlade(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V")
    )
    private void renderHotbarWith(LayerMainBlade instance, PoseStack matrixStack, MultiBufferSource bufferIn, int lightIn, ItemStack blade, LivingEntity entity, Operation<Void> original){
        ticex_1_20_1$renderWithContext(blade, matrixStack, bufferIn, lightIn, ()->{
            original.call(instance, matrixStack, bufferIn, lightIn, blade, entity);
        });
    }

    @WrapMethod(method = "renderOffhandItem")
    private void renderOffhandWith(PoseStack matrixStack, MultiBufferSource bufferIn, int lightIn, LivingEntity entity, Operation<Void> original){
        ItemStack offhandStack = entity.getItemInHand(InteractionHand.OFF_HAND);
        ticex_1_20_1$renderWithContext(offhandStack, matrixStack, bufferIn, lightIn, ()->{
            original.call(matrixStack, bufferIn, lightIn, entity);
        });
    }

    @WrapMethod(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V")
    public void renderWith(@NotNull PoseStack matrixStack, @NotNull MultiBufferSource bufferIn, int lightIn, @NotNull LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, Operation<Void> original){
        ItemStack stack = entity.getItemInHand(InteractionHand.MAIN_HAND);
        ticex_1_20_1$renderWithContext(stack, matrixStack, bufferIn, lightIn, ()->{
            original.call(matrixStack, bufferIn, lightIn, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        });
    }

    @Unique
    private static void ticex_1_20_1$renderWithContext(ItemStack stack, PoseStack matrixStack, MultiBufferSource bufferIn, int lightIn, Runnable callOriginalMethod){
        if(!(stack.getItem() instanceof IModifiable)){
            callOriginalMethod.run();
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
            callOriginalMethod.run();
        }
    }
}
