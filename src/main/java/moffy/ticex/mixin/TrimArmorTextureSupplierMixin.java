package moffy.ticex.mixin;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import moffy.ticex.TicEXConfig;
import moffy.ticex.client.rendering.shader.ShaderProvider;
import moffy.ticex.client.rendering.shader.TintedShaderArmorTexture;
import moffy.ticex.client.rendering.ticex.TicEXRenders;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.TrimMaterial;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import slimeknights.tconstruct.library.client.armor.texture.ArmorTextureSupplier;
import slimeknights.tconstruct.library.client.armor.texture.TrimArmorTextureSupplier;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import java.util.Optional;


@Mixin(value = TrimArmorTextureSupplier.class, remap = false)
@Debug(export = true)
public abstract class TrimArmorTextureSupplierMixin {

    @WrapOperation(
            method = "getArmorTexture",
            at = @At(value = "INVOKE", target = "Lslimeknights/tconstruct/library/client/armor/texture/TrimArmorTextureSupplier$TrimArmorTexture;create(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/item/armortrim/TrimMaterial;)Lslimeknights/tconstruct/library/client/armor/texture/ArmorTextureSupplier$ArmorTexture;")
    )
    private ArmorTextureSupplier.ArmorTexture insertTexture(ResourceLocation root, TrimMaterial material, Operation<ArmorTextureSupplier.ArmorTexture> original,
                                                            @Local(argsOnly = true) ItemStack stack,
                                                            @Local(index = 5) String materialId) {
        Material textureMaterial = new Material(
                Sheets.ARMOR_TRIMS_SHEET,
                root.withSuffix('_' + material.assetName())
        );

        MaterialVariantId materialVariantId = MaterialVariantId.tryParse(materialId);
        if(materialVariantId != null && TicEXConfig.USE_SHADER.get()) {
            MaterialId id = materialVariantId.getId();

            ShaderProvider.Armor shaderProvider = TicEXRenders.ARMOR_SHADERS.getShaderProvider(id);
            Optional<MaterialRenderInfo> infoOptional = MaterialRenderInfoLoader.INSTANCE.getRenderInfo(id);

            int color = -1;
            if (infoOptional.isPresent()) {
                color = infoOptional.get().vertexColor();
            }

            if (shaderProvider != null) {
                return new TintedShaderArmorTexture(
                        textureMaterial,
                        color,
                        shaderProvider
                );
            }
        }

        return original.call(root, material);
    }
}
