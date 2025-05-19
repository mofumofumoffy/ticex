package moffy.ticex.mixin;

import java.util.Optional;
import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import moffy.ticex.TicEXConfig;
import moffy.ticex.client.PartPredicate;
import moffy.ticex.client.TintedShaderArmorTexture;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.client.armor.texture.ArmorTextureSupplier;
import slimeknights.tconstruct.library.client.armor.texture.ArmorTextureSupplier.ArmorTexture;
import slimeknights.tconstruct.library.client.armor.texture.MaterialArmorTextureSupplier;
import slimeknights.tconstruct.library.client.armor.texture.TintedArmorTexture;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

@Mixin(MaterialArmorTextureSupplier.class)
public class MaterialArmorTextureSupplierMixin {

    @Shadow( remap = false )
    private static ArmorTexture tryTexture(ResourceLocation name, int color, String material) {
        return null;
    }

    @Inject(
        at=@At("tail"),
        method = "materialGetter",
        cancellable = true,
        remap = false
    )
    private static void materialGetterExtension(ResourceLocation name, CallbackInfoReturnable<Function<String,ArmorTexture>> cb) {
        cb.setReturnValue(Util.memoize(materialStr -> {
            if (materialStr instanceof String && !((String)materialStr).isEmpty()) {
                MaterialVariantId material = MaterialVariantId.tryParse((String)materialStr);
                int color = -1;
                PartPredicate predicate = TicEXRegistry.ARMOR_SHADERS.getPredicate(material);
                
                if (material != null) {
                    Optional<MaterialRenderInfo> infoOptional = MaterialRenderInfoLoader.INSTANCE.getRenderInfo(material);
                    if (infoOptional.isPresent()) {
                        MaterialRenderInfo info = infoOptional.get();
                        ResourceLocation untinted = info.texture();
                        if (untinted != null) {
                            ArmorTexture texture = tryTexture(name, -1, '_' + untinted.getNamespace() + '_' + untinted.getPath());
                            if (texture != ArmorTexture.EMPTY) {
                                return getArmorTexture(texture, getAtlasLocation(name), color, predicate);
                            }
                        }
                        color = info.vertexColor();
                        for (String fallback : info.fallbacks()) {
                            ArmorTexture texture = tryTexture(name, color, '_' + fallback);
                            if (texture != ArmorTexture.EMPTY) {
                                return getArmorTexture(texture, getAtlasLocation(name), color, predicate);
                            }
                        }
                    }
                }
                return getArmorTexture(new TintedArmorTexture(ArmorTextureSupplier.getTexturePath(name), color), getAtlasLocation(name), color, predicate);
          }
          return ArmorTexture.EMPTY;
        }));
    }

    private static ArmorTexture getArmorTexture(ArmorTexture texture, ResourceLocation path, int color, PartPredicate predicate){
        if(predicate != null && TicEXConfig.USE_SHADER.get()){
            return new TintedShaderArmorTexture(path, color, TicEXRegistry.ARMOR_SHADERS.getProvider(predicate));
        }
        return texture;
    }

    private static ResourceLocation getAtlasLocation(ResourceLocation name){
        return new ResourceLocation(name.getNamespace(), "tinker_armor/"+name.getPath());
    }
}
