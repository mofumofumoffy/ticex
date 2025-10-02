package moffy.ticex.mixin;


import com.brandon3055.brandonscore.api.TechLevel;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moffy.ticex.client.modules.avaritia.TicEXCosmicShaderProvider;
import moffy.ticex.client.modules.draconicevolution.TicEXDEShader;
import moffy.ticex.client.modules.draconicevolution.TicEXDEShaderProvider;
import moffy.ticex.client.rendering.shader.TintedShaderArmorTexture;
import moffy.ticex.client.rendering.ticex.TicEXRenders;
import moffy.ticex.lib.TicEXMaterials;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.armortrim.TrimMaterial;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import slimeknights.tconstruct.library.client.armor.texture.ArmorTextureSupplier;
import slimeknights.tconstruct.library.client.armor.texture.TrimArmorTextureSupplier;

import java.util.Objects;


@Mixin(value = TrimArmorTextureSupplier.class, remap = false)
@Debug(export = true)
public abstract class TrimArmorTextureSupplierMixin {
    @WrapOperation(
            method = "getArmorTexture",
            at = @At(value = "INVOKE", target = "Lslimeknights/tconstruct/library/client/armor/texture/TrimArmorTextureSupplier$TrimArmorTexture;create(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/item/armortrim/TrimMaterial;)Lslimeknights/tconstruct/library/client/armor/texture/ArmorTextureSupplier$ArmorTexture;")
    )
    private ArmorTextureSupplier.ArmorTexture insertTexture(ResourceLocation root, TrimMaterial material, Operation<ArmorTextureSupplier.ArmorTexture> original) {
        TicEXDEShader shader = Objects.requireNonNull(TicEXDEShaderProvider.getShader());
        TechLevel techLevel = TechLevel.VALUES[3];
//        return original.call(root, material);
        TextureAtlas atlas = Minecraft.getInstance().getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET);
        TextureAtlasSprite sprite = atlas.getSprite(root.withSuffix('_' + material.assetName()));
        return new TintedShaderArmorTexture(
                sprite,
                -1,
                TicEXRenders.ARMOR_SHADERS.getShaderProvider(TicEXMaterials.INFINITY)
//                new TicEXDEShaderProvider.Armor(
//                        shader.createArmorsRenderType(root, techLevel),
//                        techLevel
//                )
        );

    }
}
