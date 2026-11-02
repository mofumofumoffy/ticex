package moffy.ticex.client.shaders;

import com.mojang.blaze3d.vertex.VertexConsumer;
import moffy.ticex.client.providers.ShaderProvider;
import moffy.ticex.client.providers.context.ItemRenderContext;
import moffy.ticex.client.providers.context.tool.RenderQuadContext;
import moffy.ticex.client.providers.renderer.IQuadContextRenderer;
import moffy.ticex.client.render.ToolRenders;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import java.util.List;

public class TicEXRenderTasks {
    public static abstract class RenderTask {
        protected final ToolRenders.RenderPhase phase;

        protected RenderTask(ToolRenders.RenderPhase phase) {
            this.phase = phase;
        }

        public abstract void applyRenderTask();

        public ToolRenders.RenderPhase getPhase() {
            return phase;
        }
    }

    public static class InstantRenderTask extends RenderTask {
        private final Runnable renderingFunc;

        public InstantRenderTask(ToolRenders.RenderPhase phase, Runnable renderingFunc) {
            super(phase);
            this.renderingFunc = renderingFunc;
        }

        @Override
        public void applyRenderTask() {
            renderingFunc.run();
        }
    }

    public static class NakedRenderTask extends RenderTask {
        private final RenderQuadContext context;
        private final IQuadContextRenderer renderer;

        public NakedRenderTask(ToolRenders.RenderPhase phase, RenderQuadContext context, IQuadContextRenderer renderer) {
            super(phase);
            this.context = context;
            this.renderer = renderer;
        }

        @Override
        public void applyRenderTask() {
            renderer.render(
                    context.renderContext(),
                    context.quad(),
                    context.getConsumer()
            );
        }

        public void applyRenderTask(VertexConsumer consumer) {
            renderer.render(
                    context.renderContext(),
                    context.quad(),
                    consumer
            );
        }
    }

    public static class NakedBatchTask extends RenderBatchTask {
        private final ItemRenderContext itemRenderContext;
        private final RenderType renderType;

        public NakedBatchTask(ToolRenders.ToolRenderBatch renderBatch, ItemRenderContext itemRenderContext, RenderType renderType) {
            super(renderBatch, itemRenderContext);
            this.itemRenderContext = itemRenderContext;
            this.renderType = renderType;
        }

        @Override
        public void applyRenderTask() {
            MultiBufferSource bufferSource = itemRenderContext.bufferSource();
            VertexConsumer buffer = bufferSource.getBuffer(renderType);

            for (RenderTask renderTask : renderBatch.renderTasks()) {
                if (renderTask instanceof NakedRenderTask nakedRenderTask) {
                    nakedRenderTask.applyRenderTask(buffer);
                }
            }
        }
    }

    public static class RenderBatchTask extends RenderTask {
        protected final ToolRenders.ToolRenderBatch renderBatch;
        private final ItemRenderContext itemRenderContext;

        public RenderBatchTask(ToolRenders.ToolRenderBatch renderBatch, ItemRenderContext itemRenderContext) {
            super(renderBatch.renderPhase());
            this.renderBatch = renderBatch;
            this.itemRenderContext = itemRenderContext;
        }

        @Override
        public void applyRenderTask() {
            ShaderProvider.Tool shaderProvider = renderBatch.shaderProvider();
            List<RenderTask> renderTasks = renderBatch.renderTasks();

            if (shaderProvider != null) {
                shaderProvider.startRenderBatch(itemRenderContext, renderBatch.renderPhase());
            }

            for (RenderTask renderTask : renderTasks) {
                renderTask.applyRenderTask();
            }

            if (shaderProvider != null) {
                shaderProvider.endRenderBatch(itemRenderContext, renderBatch.renderPhase());
            }
        }
    }
}
