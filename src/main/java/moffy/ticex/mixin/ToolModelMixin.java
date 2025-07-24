package moffy.ticex.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import moffy.ticex.client.rendering.shader.ShaderProvider;
import moffy.ticex.client.rendering.shader.ShaderToolQuad;
import moffy.ticex.client.rendering.ticex.TicEXRenders;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import slimeknights.tconstruct.library.client.model.tools.ToolModel;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(value = ToolModel.class, remap = false)
@Debug(export = true)
public class ToolModelMixin {
    @ModifyReturnValue(method = "bakeInternal", at = @At("RETURN"))
    private static BakedModel wrapModel(BakedModel model, @Local(argsOnly = true) @Nullable IToolStackView tool) {
        if (tool != null) {
            for (Item predicate : TicEXRenders.CUSTOM_MODELS.keySet()) {
                if (ForgeRegistries.ITEMS.getKey(tool.getItem()) == ForgeRegistries.ITEMS.getKey(predicate)) {
                    return TicEXRenders.CUSTOM_MODELS.get(predicate).apply(model);
                }
            }
        }
        return model;
    }

    @ModifyExpressionValue(method = "bakeInternal", at = @At(
            value = "INVOKE", target = "Lslimeknights/mantle/client/model/util/MantleItemLayerModel;getQuadsForSprite(IILnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lcom/mojang/math/Transformation;ILslimeknights/mantle/util/ItemLayerPixels;)Ljava/util/List;",
            ordinal = 0))
    private static List<BakedQuad> wrapMaterialSmallQuads(List<BakedQuad> smallQuads, @Local(argsOnly = true) @Nullable IToolStackView tool, @Local MaterialVariantId material) {
        ShaderProvider.Tool shaderProvider = TicEXRenders.TOOL_SHADERS.getShaderProvider(material);
        return smallQuads.stream()
                .map(bakedQuad -> (BakedQuad) new ShaderToolQuad.Material(bakedQuad, shaderProvider, material))
                .toList();
    }

    @ModifyExpressionValue(method = "bakeInternal", at = @At(value = "INVOKE", target = "Lslimeknights/tconstruct/library/client/model/tools/MaterialModel;getQuadsForMaterial(Ljava/util/function/Function;Lnet/minecraft/client/resources/model/Material;Lslimeknights/tconstruct/library/materials/definition/MaterialVariantId;ILcom/mojang/math/Transformation;Lslimeknights/mantle/util/ItemLayerPixels;)Ljava/util/List;"))
    private static List<BakedQuad> wrapMaterialLargeQuads(List<BakedQuad> largeQuads, @Local(argsOnly = true) @Nullable IToolStackView tool, @Local MaterialVariantId material) {
        ShaderProvider.Tool shaderProvider = TicEXRenders.TOOL_SHADERS.getShaderProvider(material);
        return largeQuads.stream()
                .map(bakedQuad -> (BakedQuad) new ShaderToolQuad.Material(bakedQuad, shaderProvider, material))
                .toList();
    }

    @ModifyReturnValue(method = "bakeInternal", at = @At("RETURN"))
    private static BakedModel wrapBakedModel(BakedModel original,
                                             @Local(argsOnly = true) @Nullable IToolStackView tool) {
        if (tool != null) {
            for (Item item : TicEXRenders.CUSTOM_MODELS.keySet()) {
                ResourceLocation toolItemKey = ForgeRegistries.ITEMS.getKey(tool.getItem());
                ResourceLocation customItem = ForgeRegistries.ITEMS.getKey(item);

                if (customItem != null && customItem.equals(toolItemKey)) {
                    return TicEXRenders.CUSTOM_MODELS.get(item).apply(original);
                }
            }
        }
        return original;
    }
}
