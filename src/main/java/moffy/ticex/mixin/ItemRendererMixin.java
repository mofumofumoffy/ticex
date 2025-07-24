package moffy.ticex.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import moffy.ticex.client.rendering.ItemRenderContext;
import moffy.ticex.client.rendering.shader.ShaderProvider;
import moffy.ticex.client.rendering.ticex.TicEXRenders;
import moffy.ticex.client.rendering.ticex.TicEXToolRenders;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(value = ItemRenderer.class, priority = 1700)
public abstract class ItemRendererMixin {
    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    public void render(
        ItemStack pItemStack,
        ItemDisplayContext pDisplayContext,
        boolean pLeftHand,
        PoseStack pPoseStack,
        MultiBufferSource pBuffer,
        int pCombinedLight,
        int pCombinedOverlay,
        BakedModel pModel,
        CallbackInfo ci
    ) {
        if (!TicEXRenders.shouldRenderWithShader(pItemStack)) {
            return;
        }


        Map<MaterialVariantId, ShaderProvider.Tool> materialShaderProviderMap = TicEXRenders.collectMaterialShaderProviders(pItemStack);
        Map<ModifierId, ShaderProvider.Tool> modifierShaderProviders = TicEXRenders.collectModifierShaderProviders(pItemStack);

        if (materialShaderProviderMap.isEmpty() && modifierShaderProviders.isEmpty()) {
            return;
        }

        ItemRenderContext itemRenderContext = new ItemRenderContext(
                pItemStack,
                pDisplayContext,
                pLeftHand,
                pPoseStack,
                pBuffer,
                pCombinedLight,
                pCombinedOverlay
        );

        if (!materialShaderProviderMap.isEmpty()) {
            materialShaderProviderMap.forEach((materialVariantId, shaderProvider) -> {
                shaderProvider.beginRender(pItemStack, itemRenderContext);
                shaderProvider.beginRenderMaterial(pItemStack, materialVariantId);
            });
        }

        final ToolStack tool;
        if (!modifierShaderProviders.isEmpty()) {
            tool = ToolStack.from(pItemStack);
            modifierShaderProviders.forEach((modifierId, shaderProvider) -> {
                shaderProvider.beginRender(pItemStack, itemRenderContext);
                shaderProvider.beginRenderModifier(tool, modifierId);
            });
        } else {
            tool = null;
        }

        List<ShaderProvider.Tool> seenList = new ArrayList<>();

        TicEXRenders.renderQuadsTasks(pItemStack, pPoseStack, pModel, pDisplayContext, pLeftHand, (renderType, quads) ->
                TicEXToolRenders.prepareRenderTasks(
                        renderType,
                        quads,
                        itemRenderContext,
                        tool,
                        seenList
                ));

        ci.cancel();
    }
}
