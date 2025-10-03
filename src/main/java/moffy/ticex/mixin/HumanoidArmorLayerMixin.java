package moffy.ticex.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import moffy.ticex.TicEXConfig;
import moffy.ticex.client.rendering.QuadRenderContext;
import moffy.ticex.client.rendering.shader.ShaderProvider;
import moffy.ticex.client.rendering.ticex.TicEXRenders;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import java.util.Optional;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin {
    @Inject(method = "renderTrim(Lnet/minecraft/world/item/ArmorMaterial;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/armortrim/ArmorTrim;Lnet/minecraft/client/model/Model;Z)V",
            at = @At("HEAD"), cancellable = true, remap = false)
    public void renderTrim(ArmorMaterial armorMaterial, PoseStack poseStack, MultiBufferSource bufferSource, int pPackedLight, ArmorTrim armorTrim, Model model, boolean pInnerTexture, CallbackInfo ci) {
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

            ShaderProvider.Armor shaderProvider = TicEXRenders.ARMOR_SHADERS.getShaderProvider(id);

            if (shaderProvider != null) {
                Material textureMaterial = new Material(
                        Sheets.ARMOR_TRIMS_SHEET,
                        pInnerTexture ? armorTrim.innerTexture(armorMaterial) : armorTrim.outerTexture(armorMaterial)
                );

                shaderProvider.renderQuadOverlay(new QuadRenderContext.ArmorPartRenderContext(
                        model,
                        poseStack,
                        bufferSource,
                        pPackedLight,
                        OverlayTexture.NO_OVERLAY,
                        1.0f,
                        1.0f,
                        1.0f,
                        1.0f,
                        false,
                        textureMaterial,
                        -1
                ));
            }

            ci.cancel();
        }
    }
}
