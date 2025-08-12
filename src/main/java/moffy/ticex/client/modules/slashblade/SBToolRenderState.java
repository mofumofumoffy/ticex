package moffy.ticex.client.modules.slashblade;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mods.flammpfeil.slashblade.client.renderer.model.obj.Face;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.util.BladeRenderState;
import mods.flammpfeil.slashblade.event.client.RenderOverrideEvent;
import moffy.ticex.client.modules.slashblade.SBToolRenderType.PartType;
import moffy.ticex.client.rendering.ItemRenderContext;
import moffy.ticex.client.rendering.shader.ShaderProvider;
import moffy.ticex.client.rendering.ticex.TicEXRenders;
import moffy.ticex.lib.utils.TicEXSBUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.awt.*;
import java.util.Optional;

public class SBToolRenderState {

    private static final Color defaultColor = Color.white;
    private static Color col = defaultColor;

    public static void renderOverride(
            ItemStack stack,
            ItemRenderContext itemRenderContext,
            ToolStack tool,
            WavefrontObject model,
            String target,
            PoseStack matrixStackIn,
            MultiBufferSource bufferIn,
            int packedLightIn
    ) {
        renderOverride(
                stack,
                itemRenderContext,
                tool,
                model,
                target,
                matrixStackIn,
                bufferIn,
                packedLightIn,
                SBToolRenderType.instance::getSlashBladeBlend,
                true
        );
    }

    public static void renderOverrideLuminous(
            ItemStack stack,
            ItemRenderContext itemRenderContext,
            ToolStack tool,
            WavefrontObject model,
            String target,
            PoseStack matrixStackIn,
            MultiBufferSource bufferIn,
            int packedLightIn
    ) {
        renderOverride(
                stack,
                itemRenderContext,
                tool,
                model,
                target,
                matrixStackIn,
                bufferIn,
                packedLightIn,
                SBToolRenderType.instance::getSlashBladeLuminousBlend,
                false
        );
    }

    public static void renderOverride(
            ItemStack stack,
            ItemRenderContext itemRenderContext,
            ToolStack tool,
            WavefrontObject model,
            String target,
            PoseStack matrixStackIn,
            MultiBufferSource bufferIn,
            int packedLightIn,
            RenderGetter<MaterialVariantId, Runnable, RenderType> getRenderType,
            boolean enableEffect
    ) {
        MaterialNBT materials = tool.getMaterials();
        for (int i = 0; i < materials.size(); i++) {
            MaterialVariantId material = materials.get(i).getVariant();


            SBToolRenderType.PartType partType = SBToolRenderType.PartType.byIndex(i);
            ShaderProvider.Tool shaderProvider = TicEXRenders.TOOL_SHADERS.getShaderProvider(material.getId());
            if (partType == null) continue;

            RenderType rt = getRenderType.getRenderType(material, partType, () -> {
                Optional<MaterialRenderInfo> optional = MaterialRenderInfoLoader.INSTANCE.getRenderInfo(material);
                optional.ifPresent(materialRenderInfo -> col = new Color(materialRenderInfo.vertexColor()));
            });

            RenderOverrideEvent event = RenderOverrideEvent.onRenderOverride(
                    stack,
                    model,
                    target,
                    null,
                    matrixStackIn,
                    bufferIn,
                    packedLightIn,
                    resourceLocation -> rt,
                    enableEffect
            );

            renderVC(
                    stack,
                    itemRenderContext,
                    shaderProvider,
                    bufferIn,
                    rt,
                    matrixStackIn,
                    event,
                    packedLightIn,
                    enableEffect,
                    target,
                    material,
                    partType
            );
        }
    }

    public static void renderVC(
            ItemStack stack,
            ItemRenderContext itemRenderContext,
            ShaderProvider.Tool shaderProvider,
            MultiBufferSource bufferIn,
            RenderType rt,
            PoseStack matrixStackIn,
            RenderOverrideEvent event,
            int packedLightIn,
            boolean enableEffect,
            String target,
            MaterialVariantId material,
            PartType partType
    ) {
        VertexConsumer vb = bufferIn.getBuffer(rt);

        Face.setCol(col);
        Face.setLightMap(packedLightIn);
        Face.setMatrix(matrixStackIn);
        TicEXSBUtils.tessellateWithShader(stack, itemRenderContext, shaderProvider, material, event.getModel(), vb, bufferIn, partType, event.getTarget());

        if (stack.hasFoil() && enableEffect) {
            vb = bufferIn.getBuffer(
                    target.startsWith("item_") ? BladeRenderState.SLASHBLADE_ITEM_GLINT : BladeRenderState.SLASHBLADE_GLINT
            );
            event.getModel().tessellateOnly(vb, event.getTarget());
        }

        Face.resetMatrix();
        Face.resetLightMap();
        Face.resetCol();

        Face.resetAlphaOverride();
        Face.resetUvOperator();

        col = defaultColor;
    }

    @FunctionalInterface
    public interface RenderGetter<P extends MaterialVariantId, Q extends Runnable, R extends RenderType> {
        R getRenderType(P material, SBToolRenderType.PartType type, Q whenIsDefault);
    }
}
