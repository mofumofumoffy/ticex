package moffy.ticex.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import moffy.ticex.TicEXConfig;
import moffy.ticex.client.providers.renderer.ArmorContextRenderer;
import moffy.ticex.client.providers.context.RenderContext;
import moffy.ticex.client.providers.context.armor.RenderArmorPartContext;
import moffy.ticex.client.providers.ShaderProvider;
import moffy.ticex.client.CustomTinkerRenders;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Optional;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin {
    @Inject(
            method = "renderArmorPiece",
            at = @At(value = "INVOKE", target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V", remap = false)
    )
    private void setStackContext(
            PoseStack pPoseStack,
            MultiBufferSource pBuffer,
            LivingEntity pLivingEntity,
            EquipmentSlot pSlot,
            int pPackedLight,
            HumanoidModel<?> pModel,
            CallbackInfo ci,
            @Local ItemStack itemStack,
            @Share(value = "stackContext")LocalRef<ItemStack> stackContext
            ){
            stackContext.set(itemStack);
    }

    @Inject(method = "renderTrim(Lnet/minecraft/world/item/ArmorMaterial;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/armortrim/ArmorTrim;Lnet/minecraft/client/model/Model;Z)V",
            at = @At("TAIL"), remap = false)
    public void renderTrim(
            ArmorMaterial armorMaterial,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int pPackedLight,
            ArmorTrim armorTrim,
            Model model,
            boolean pInnerTexture,
            CallbackInfo ci,
            @Share(value = "stackContext")LocalRef<ItemStack> stackContext
    ) {
        Holder<TrimMaterial> material = armorTrim.material();
        Optional<ResourceKey<TrimMaterial>> trimMaterialKeyOpt = material.unwrapKey();
        if (trimMaterialKeyOpt.isEmpty()) {
            return;
        }
        ResourceKey<TrimMaterial> trimMaterialKey = trimMaterialKeyOpt.get();
        ResourceLocation trimMaterialId = trimMaterialKey.location();

        MaterialVariantId materialVariantId = MaterialVariantId.tryParse(trimMaterialId.toString());
        if(materialVariantId != null && TicEXConfig.USE_SHADER.get()) {
            MaterialId id = materialVariantId.getId();

            ShaderProvider.Armor shaderProvider = CustomTinkerRenders.ARMOR_SHADERS.getShaderProvider(id);

            if (shaderProvider != null) {
                Material textureMaterial = new Material(
                        Sheets.ARMOR_TRIMS_SHEET,
                        pInnerTexture ? armorTrim.innerTexture(armorMaterial) : armorTrim.outerTexture(armorMaterial)
                );

                RenderContext renderContext = new RenderContext(
                        bufferSource,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        poseStack, pPackedLight, OverlayTexture.NO_OVERLAY
                );
                shaderProvider.renderOverlay(new RenderArmorPartContext(
                        renderContext,
                        model,
                        textureMaterial,
                        ToolStack.from(stackContext.get()).getPersistentData(),
                        false
                ), ArmorContextRenderer.RENDERER);
            }
        }
    }
}
