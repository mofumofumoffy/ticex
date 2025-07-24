package moffy.ticex.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import moffy.ticex.client.rendering.shader.ShaderProvider;
import moffy.ticex.client.rendering.shader.ShaderToolQuad;
import moffy.ticex.client.rendering.ticex.TicEXRenders;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import slimeknights.tconstruct.library.client.modifiers.NormalModifierModel;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

@Mixin(value = NormalModifierModel.class, remap = false)
public class NormalModifierModelMixin {
    @ModifyExpressionValue(method = "addQuads", at = @At(value = "INVOKE", target = "Lslimeknights/mantle/client/model/util/MantleItemLayerModel;getQuadsForSprite(IILnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lcom/mojang/math/Transformation;ILslimeknights/mantle/util/ItemLayerPixels;)Ljava/util/List;"))
    public List<BakedQuad> addQuadsExtension(List<BakedQuad> original,
                                             @Local(argsOnly = true) IToolStackView tool,
                                             @Local Material spriteName,
                                             @Local(argsOnly = true) ModifierEntry entry) {
        if (TicEXRenders.TOOL_SHADERS.isToolTarget(tool)) {
            ModifierId modifierId = entry.getId();
            ShaderProvider.Tool shaderProvider = TicEXRenders.TOOL_SHADERS.getShaderProvider(modifierId);

            return original
                    .stream()
                    .map(quad ->
                            quad == null ? null : (BakedQuad) new ShaderToolQuad.Modifier(quad, shaderProvider, modifierId)
                    )
                    .toList();
        }
        return original;
    }
}
