package moffy.ticex.mixin;

import moffy.ticex.TicEXConfig;
import moffy.ticex.client.render.shader.ShaderProvider;
import moffy.ticex.client.render.shader.TintedShaderArmorTexture;
import moffy.ticex.client.render.ticex.TicEXRenders;
import net.minecraft.Util;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.client.armor.texture.ArmorTextureSupplier;
import slimeknights.tconstruct.library.client.armor.texture.ArmorTextureSupplier.ArmorTexture;
import slimeknights.tconstruct.library.client.armor.texture.MaterialArmorTextureSupplier;
import slimeknights.tconstruct.library.client.armor.texture.TintedArmorTexture;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import java.util.Optional;
import java.util.function.Function;

@Mixin(value = MaterialArmorTextureSupplier.class, remap = false)
@Debug(export = true)
public abstract class MaterialArmorTextureSupplierMixin {

    @Shadow
    private static ArmorTexture tryTexture(ResourceLocation name, int color, String material) {
        return null;
    }

    @Inject(method = "materialGetter", at = @At("HEAD"), cancellable = true)
    private static void materialGetterExtension(ResourceLocation name, CallbackInfoReturnable<Function<String, ArmorTexture>> cir) {
        // Ignore Validator
        cir.setReturnValue(
                Util.memoize(materialStr -> {
                    if (!materialStr.isEmpty()) {
                        MaterialVariantId material = MaterialVariantId.tryParse(materialStr);
                        ShaderProvider.Armor shaderProvider = TicEXRenders.ARMOR_SHADERS.getShaderProvider(material);
                        Material textureMaterial = new Material(InventoryMenu.BLOCK_ATLAS, ticex$getAtlasLocation(name));
                        int color = -1;
                        if (material != null) {
                            Optional<MaterialRenderInfo> infoOptional = MaterialRenderInfoLoader.INSTANCE.getRenderInfo(material);
                            if (infoOptional.isPresent()) {
                                MaterialRenderInfo info = infoOptional.get();
                                ResourceLocation untinted = info.texture();
                                if (untinted != null) {
                                    ArmorTexture texture = tryTexture(name, -1, '_' + untinted.getNamespace() + '_' + untinted.getPath());
                                    if (texture != ArmorTexture.EMPTY) {
                                        return ticex$getArmorTexture(texture, textureMaterial, color, shaderProvider, material);
                                    }
                                }
                                color = info.vertexColor();
                                for (String fallback : info.fallbacks()) {
                                    ArmorTexture texture = tryTexture(name, color, '_' + fallback);
                                    if (texture != ArmorTexture.EMPTY) {
                                        return ticex$getArmorTexture(texture, textureMaterial, color, shaderProvider, material);
                                    }
                                }
                            }


                            return ticex$getArmorTexture(
                                    new TintedArmorTexture(ArmorTextureSupplier.getTexturePath(name), -1),
                                    textureMaterial,
                                    -1,
                                    shaderProvider,
                                    material
                            );
                        }

                        // base material guaranteed to exist, else we would not be in this function
                        return new TintedArmorTexture(ArmorTextureSupplier.getTexturePath(name), color);
                    }
                    return ArmorTexture.EMPTY;
                })
        );
    }

    @Unique
    private static ArmorTexture ticex$getArmorTexture(ArmorTexture texture, Material textureMaterial, int color, ShaderProvider.Armor shaderProvider, MaterialVariantId material) {
        if (shaderProvider != null && TicEXConfig.USE_SHADER.get()) {
            return new TintedShaderArmorTexture(textureMaterial, color, shaderProvider, material);
        }
        return texture;
    }

    @Unique
    private static ResourceLocation ticex$getAtlasLocation(ResourceLocation name) {
        return new ResourceLocation(name.getNamespace(), "tinker_armor/" + name.getPath());
    }

    // Traveler's Updateでのみ動作
        /*
        @ModifyExpressionValue(method = "materialGetter", at = @At(value = "INVOKE", target = "Lslimeknights/mantle/data/listener/ResourceValidator;test(Lnet/minecraft/resources/ResourceLocation;)Z"))
        private static boolean ignoreValidator(boolean original) {
            return true;
    @@ -125,9 +70,9 @@ private static ArmorTexture materialGetterExtension(ArmorTexture original,
        }

        @Unique
        private static ArmorTexture ticex$getArmorTexture(ArmorTexture texture, Material textureMaterial, int color, ShaderProvider.Armor shaderProvider, MaterialVariantId material) {
        private static ArmorTexture ticex$getArmorTexture(ArmorTexture texture, Material material, int color, ShaderProvider.Armor shaderProvider) {
            if (shaderProvider != null && TicEXConfig.USE_SHADER.get()) {
                return new TintedShaderArmorTexture(textureMaterial, color, shaderProvider, material);
                return new TintedShaderArmorTexture(material, color, shaderProvider);
            }
            return texture;
        }
    }*/
}


