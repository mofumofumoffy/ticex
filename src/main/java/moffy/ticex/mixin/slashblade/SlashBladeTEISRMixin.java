package moffy.ticex.mixin.slashblade;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import mods.flammpfeil.slashblade.client.renderer.SlashBladeTEISR;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import moffy.ticex.client.render.provider.context.ItemRenderContext;
import moffy.ticex.lib.context.ContextFrame;
import moffy.ticex.lib.context.TicEXContexts;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@Mixin(value = SlashBladeTEISR.class, remap = false)
public abstract class SlashBladeTEISRMixin {
    @Shadow
    public abstract ResourceLocation stackDefaultModel(ItemStack stack);

    @ModifyVariable(method = "renderModel", at = @At(value = "STORE"), ordinal = 0)
    public ResourceLocation modifyModel(ResourceLocation modelLocation,
                                                @Local(argsOnly = true) ItemStack stack) {
        if(!(stack.getItem() instanceof IModifiable)) return modelLocation;
        ToolStack tool = ToolStack.from(stack);

        if (tool.getModifierLevel(TicEXRegistry.KOSHIRAE_MODIFIER.get()) > 0) {
            CompoundTag persistentTag = stack.getOrCreateTag().getCompound("bladeState");
            if (persistentTag.contains("ModelName")) {
                return ResourceLocation.tryParse(persistentTag.getString("ModelName"));
            }
        }
        return modelLocation;
    }

    @ModifyVariable(method = "renderModel", at = @At(value = "STORE"), ordinal = 1)
    public ResourceLocation modifyTexture(ResourceLocation textureLocation,
                                                @Local(argsOnly = true) ItemStack stack) {
        if(!(stack.getItem() instanceof IModifiable)) return textureLocation;
        ToolStack tool = ToolStack.from(stack);

        if (tool.getModifierLevel(TicEXRegistry.KOSHIRAE_MODIFIER.get()) > 0) {
            CompoundTag persistentTag = stack.getOrCreateTag().getCompound("bladeState");
            if (persistentTag.contains("ModelName")) {
                return ResourceLocation.tryParse(persistentTag.getString("TextureName"));
            } else {
                //noinspection OptionalGetWithoutIsPresent
                return stack
                        .getCapability(ItemSlashBlade.BLADESTATE)
                        .filter(s -> s.getTexture().isPresent())
                        .map(s -> s.getTexture().get())
                        .orElseGet(() -> stackDefaultModel(stack));
            }
        }
        return textureLocation;
    }

    @WrapOperation(method = "renderIcon(Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IFZ)V",
            at = {
                @At(value = "INVOKE", target = "Lmods/flammpfeil/slashblade/client/renderer/util/BladeRenderState;renderOverrided(Lnet/minecraft/world/item/ItemStack;Lmods/flammpfeil/slashblade/client/renderer/model/obj/WavefrontObject;Ljava/lang/String;Lnet/minecraft/resources/ResourceLocation;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", ordinal = 1),
                @At(value = "INVOKE", target = "Lmods/flammpfeil/slashblade/client/renderer/util/BladeRenderState;renderOverrided(Lnet/minecraft/world/item/ItemStack;Lmods/flammpfeil/slashblade/client/renderer/model/obj/WavefrontObject;Ljava/lang/String;Lnet/minecraft/resources/ResourceLocation;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", ordinal = 2)
            })
    public void renderIcon(ItemStack stack, WavefrontObject model, String target, ResourceLocation texture, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, Operation<Void> original) {
        try(ContextFrame<ItemRenderContext> frame = TicEXContexts.SB_RENDERING_CONTEXT.open(null)) {
            original.call(stack, model, target, texture, matrixStackIn, bufferIn, packedLightIn);
        }
    }
}
