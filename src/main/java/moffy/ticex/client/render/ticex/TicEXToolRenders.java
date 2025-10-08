package moffy.ticex.client.render.ticex;

import moffy.ticex.client.render.provider.QuadContextRenderer;
import moffy.ticex.client.render.provider.context.ItemRenderContext;
import moffy.ticex.client.render.provider.context.RenderContext;
import moffy.ticex.client.render.provider.context.tool.RenderQuadContext;
import moffy.ticex.client.render.shader.ShaderProvider;
import moffy.ticex.client.render.shader.ShaderToolQuad;
import moffy.ticex.client.render.shader.TicEXRenderTasks;
import moffy.ticex.client.render.shader.TicEXRenderTasks.InstantRenderTask;
import moffy.ticex.client.render.shader.TicEXRenderTasks.NakedRenderTask;
import moffy.ticex.client.render.shader.TicEXRenderTasks.RenderBatchTask;
import moffy.ticex.client.render.shader.TicEXRenderTasks.RenderTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

public class TicEXToolRenders {
    public static List<RenderTask> prepareRenderTasks(
            RenderType renderType,
            List<BakedQuad> pQuads,
            ItemRenderContext itemRenderContext,
            @Nullable ToolStack tool,
            List<ShaderProvider.Tool> seenList
    ) {
        List<RenderTask> renderTasks = new ArrayList<>();
        ItemColors itemColors = Minecraft.getInstance().getItemColors();

        ItemStack pItemStack = itemRenderContext.itemStack();

        List<RenderQuadContext> renderContexts = new ArrayList<>();

        // prepare contexts
        for (BakedQuad bakedQuad : pQuads) {
            int overridedColor = -1;

            if (!pItemStack.isEmpty() && bakedQuad.isTinted()) {
                overridedColor = itemColors.getColor(pItemStack, bakedQuad.getTintIndex());
            }

            // calculate quad color
            float r = overridedColor != -1 ? ((float) ((overridedColor >> 16) & 255) / 255.0F) : 1.0f;
            float g = overridedColor != -1 ? ((float) ((overridedColor >> 8) & 255) / 255.0F) : 1.0f;
            float b = overridedColor != -1 ? ((float) (overridedColor & 255) / 255.0F) : 1.0f;

            RenderContext renderContext = new RenderContext(
                    itemRenderContext.bufferSource(),
                    r, g, b, 1.0f,
                    itemRenderContext.poseStack(),
                    itemRenderContext.combinedLight(),
                    itemRenderContext.combinedOverlay()
            );
            RenderQuadContext context = new RenderQuadContext(
                    itemRenderContext.itemStack(),
                    renderType,
                    renderContext,
                    bakedQuad
            );

            renderContexts.add(context);
        }

        // add render batch task
        List<ToolRenderBatch> renderBatches = processRenderBatches(itemRenderContext, renderContexts, tool, seenList);

        for (ToolRenderBatch renderBatch : renderBatches) {
            if (renderBatch.shaderProvider == null) {
                renderTasks.add(new TicEXRenderTasks.NakedBatchTask(renderBatch, itemRenderContext, renderType));
            } else {
                renderTasks.add(new RenderBatchTask(renderBatch, itemRenderContext));
            }
        }

        return renderTasks;
    }

    public static List<ToolRenderBatch> processRenderBatches(ItemRenderContext itemRenderContext, List<RenderQuadContext> contexts, @Nullable ToolStack toolStack, List<ShaderProvider.Tool> seenList) {

        EnumMap<RenderPhase, ShaderRenderTasksMap> phaseTasksMap = new EnumMap<>(RenderPhase.class);

        for (RenderQuadContext context : contexts) {
            BakedQuad quad = context.quad();

            ShaderProvider.Tool shaderProvider;
            if (quad instanceof ShaderToolQuad shaderToolQuad) {
                shaderProvider = shaderToolQuad.getShaderProvider();
            } else {
                shaderProvider = null;
            }

            List<RenderTask> renderTasks = new ArrayList<>(
                    getMaterialQuadRenderTasks(context, itemRenderContext, seenList)
            );

            if (toolStack != null && context.itemStack().getItem() instanceof IModifiable) {
                renderTasks.addAll(getModifierQuadRenderTasks(context, toolStack, seenList));
            }

            for (RenderTask renderTask : renderTasks) {
                RenderPhase phase = renderTask.getPhase();

                // phaseTasksMap[phase][shaderProvider].add(renderTask)
                phaseTasksMap
                        .computeIfAbsent(phase, renderPhase -> new ShaderRenderTasksMap())
                        .computeIfAbsent(shaderProvider, sp -> new ArrayList<>())
                        .add(renderTask);
            }
        }

        List<ToolRenderBatch> batches = new ArrayList<>();

        for (RenderPhase renderPhase : phaseTasksMap.keySet()) {
            ShaderRenderTasksMap shaderRenderTasksMap = phaseTasksMap.get(renderPhase);

            shaderRenderTasksMap.forEach((shaderProvider, renderTasks) -> {
                ToolRenderBatch renderBatch = new ToolRenderBatch(
                        shaderProvider,
                        renderPhase,
                        renderTasks
                );

                batches.add(renderBatch);
            });
        }

        return batches;
    }

    public static List<RenderTask> getMaterialQuadRenderTasks(RenderQuadContext context, ItemRenderContext itemRenderContext, List<ShaderProvider.Tool> seenList) {
        List<RenderTask> renderTasks = new ArrayList<>();
        BakedQuad bakedQuad = context.quad();

        if (!(bakedQuad instanceof ShaderToolQuad.Material shaderToolQuad)) {
            return List.of(new NakedRenderTask(
                    RenderPhase.UNDERLAY_NORMAL,
                    context,
                    QuadContextRenderer.RENDERER
            ));
        }

        ShaderProvider.Tool provider = shaderToolQuad.getShaderProvider();

        if (provider == null) {
            return List.of(new NakedRenderTask(
                    RenderPhase.UNDERLAY_NORMAL,
                    context,
                    QuadContextRenderer.RENDERER
            ));
        }

        if (!seenList.contains(provider)) {
            renderTasks.add(new InstantRenderTask(
                    RenderPhase.UNDERLAY,
                    () -> {
                        provider.preRenderMaterial(itemRenderContext, shaderToolQuad.getMaterialId());
                        provider.renderUnderlay(context, QuadContextRenderer.RENDERER);
                    }
            ));
            seenList.add(provider);
        }

        //overlay
        renderTasks.add(new InstantRenderTask(
                RenderPhase.OVERLAY_MATERIAL,
                () -> {
                    provider.preRenderMaterial(itemRenderContext, shaderToolQuad.getMaterialId());
                    provider.renderOverlay(context, QuadContextRenderer.RENDERER);
                }
        ));

        return renderTasks;
    }

    public static List<RenderTask> getModifierQuadRenderTasks(RenderQuadContext context, ToolStack tool, List<ShaderProvider.Tool> seenList) {
        List<RenderTask> renderTasks = new ArrayList<>();
        BakedQuad bakedQuad = context.quad();

        if (!(bakedQuad instanceof ShaderToolQuad.Modifier shaderToolQuad)) {
            return List.of();
        }

        ShaderProvider.Tool provider = shaderToolQuad.getShaderProvider();

        if (provider == null) {
            //normal modifier
            return List.of(new NakedRenderTask(
                    RenderPhase.UNDERLAY_NORMAL,
                    context,
                    QuadContextRenderer.RENDERER
            ));
        }

        if (!seenList.contains(provider)) {
            renderTasks.add(new InstantRenderTask(
                    RenderPhase.UNDERLAY,
                    () -> {
                        provider.preRenderModifier(tool, shaderToolQuad.getModifierId());
                        provider.renderUnderlay(context, QuadContextRenderer.RENDERER);
                    }
            ));
            seenList.add(provider);
        }

        //overlay
        renderTasks.add(new InstantRenderTask(
                RenderPhase.OVERLAY_MODIFIER,
                () -> {
                    provider.preRenderModifier(tool, shaderToolQuad.getModifierId());
                    provider.renderOverlay(context, QuadContextRenderer.RENDERER);
                }
        ));

        return renderTasks;
    }

    public enum RenderPhase {
        UNDERLAY(0),
        UNDERLAY_NORMAL(1),
        OVERLAY_MATERIAL(2),
        OVERLAY_MODIFIER(3);

        private final int index;

        RenderPhase(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    public static final class ShaderRenderTasksMap extends HashMap<ShaderProvider.Tool, List<RenderTask>> {
    }

    public record ToolRenderBatch(
            @Nullable ShaderProvider.Tool shaderProvider,
            RenderPhase renderPhase,
            List<RenderTask> renderTasks
    ) {

    }
}
