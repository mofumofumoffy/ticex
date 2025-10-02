package moffy.ticex.mixin;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import moffy.ticex.client.rendering.shader.ShaderProvider;
import moffy.ticex.client.rendering.shader.TintedShaderArmorTexture;
import moffy.ticex.client.rendering.ticex.TicEXRenders;
import moffy.ticex.lib.TicEXMaterials;
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
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

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
        if(!(stack.getItem() instanceof IModifiable)) {
            return original.call(root, material);
        }

        ToolStack tool = ToolStack.from(stack);
        Material textureMaterial = new Material(
                Sheets.ARMOR_TRIMS_SHEET,
                root.withSuffix('_' + material.assetName())
        );

        for (MaterialVariant materialVariant : tool.getMaterials().getList()) {
            if(materialVariant.getId().getPath().equals(materialId)) {
                // マテリアル一致時の処理
            }
        }

        MaterialId chaotic = TicEXMaterials.CHAOTIC;

        ShaderProvider.Armor shaderProvider = TicEXRenders.ARMOR_SHADERS.getShaderProvider(chaotic);
        Optional<MaterialRenderInfo> infoOptional = MaterialRenderInfoLoader.INSTANCE.getRenderInfo(chaotic);

        int color = -1;
        if (infoOptional.isPresent()) {
            color = infoOptional.get().vertexColor();
        }

        if(shaderProvider != null) {
            return new TintedShaderArmorTexture(
                    textureMaterial,
                    color,
                    shaderProvider
            );
        }

        return original.call(root, material);
    }
}
