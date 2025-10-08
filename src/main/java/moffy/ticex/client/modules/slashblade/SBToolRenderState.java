package moffy.ticex.client.modules.slashblade;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mods.flammpfeil.slashblade.client.renderer.model.obj.Face;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.util.BladeRenderState;
import mods.flammpfeil.slashblade.event.client.RenderOverrideEvent;
import moffy.ticex.client.modules.slashblade.SBToolRenderType.PartType;
import moffy.ticex.client.render.provider.context.ItemRenderContext;
import moffy.ticex.client.render.shader.ShaderProvider;
import moffy.ticex.client.render.slashblade.TicEXSBRenderers;
import moffy.ticex.client.render.ticex.TicEXRenders;
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
            String target
    ) {
        renderOverride(
                stack,
                itemRenderContext,
                tool,
                model,
                target,
                SBToolRenderType.instance::getSlashBladeBlend,
                true
        );
    }

    public static void renderOverrideLuminous(
            ItemStack stack,
            ItemRenderContext itemRenderContext,
            ToolStack tool,
            WavefrontObject model,
            String target
    ) {
        renderOverride(
                stack,
                itemRenderContext,
                tool,
                model,
                target,
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
            RenderGetter<MaterialVariantId, Runnable, RenderType> getRenderType,
            boolean enableEffect
    ) {
        MultiBufferSource bufferSource = itemRenderContext.bufferSource();
        PoseStack poseStack = itemRenderContext.poseStack();
        int packedLight = itemRenderContext.combinedLight();

        MaterialNBT materials = tool.getMaterials();
        for (int i = 0; i < materials.size(); i++) {
            MaterialVariantId material = materials.get(i).getVariant();


            SBToolRenderType.PartType partType = SBToolRenderType.PartType.byIndex(i);
            ShaderProvider.Generic shaderProvider = TicEXRenders.GENERIC_SHADERS.getShaderProvider(material);
            if (partType == null) continue;

            RenderType renderType = getRenderType.getRenderType(material, partType, () -> {
                Optional<MaterialRenderInfo> optional = MaterialRenderInfoLoader.INSTANCE.getRenderInfo(material);
                optional.ifPresent(materialRenderInfo -> col = new Color(materialRenderInfo.vertexColor()));
            });

            RenderOverrideEvent event = RenderOverrideEvent.onRenderOverride(
                    stack,
                    model,
                    target,
                    null,
                    poseStack,
                    bufferSource,
                    packedLight,
                    resourceLocation -> renderType,
                    enableEffect
            );

            renderVC(
                    stack,
                    itemRenderContext,
                    shaderProvider,
                    renderType,
                    event,
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
            ShaderProvider.Generic shaderProvider,
            RenderType rt,
            RenderOverrideEvent event,
            boolean enableEffect,
            String target,
            MaterialVariantId material,
            PartType partType
    ) {
        MultiBufferSource bufferSource = itemRenderContext.bufferSource();

        Face.setCol(col);
        Face.setLightMap(itemRenderContext.combinedLight());
        Face.setMatrix(itemRenderContext.poseStack());
        TicEXSBRenderers.tessellateWithShader(itemRenderContext, rt, shaderProvider, material, event.getModel(), partType, event.getTarget());

        if (stack.hasFoil() && enableEffect) {
            VertexConsumer consumer = bufferSource.getBuffer(
                    target.startsWith("item_") ? BladeRenderState.SLASHBLADE_ITEM_GLINT : BladeRenderState.SLASHBLADE_GLINT
            );
            event.getModel().tessellateOnly(consumer, event.getTarget());
        }

        if (stack.hasFoil() && enableEffect) {
            VertexConsumer vertexConsumer = bufferSource.getBuffer(
                    target.startsWith("item_") ? BladeRenderState.SLASHBLADE_ITEM_GLINT : BladeRenderState.SLASHBLADE_GLINT
            );
            event.getModel().tessellateOnly(vertexConsumer, event.getTarget());
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
