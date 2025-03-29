package moffy.ticex.client;

import java.util.function.Consumer;

import moffy.ticex.client.ShaderProvider.RenderQuadArgsWrapper;

public class ShaderToolRenderUtils {
    public enum RenderPhase {
        UNDERLAY(0), 
        MATERIAL_WITH_OVERLAY(1),
        OVERLAY_MATERIAL(2), 
        NORMAL_MATERIAL(3), 
        NORMAL_MODIFIER(4),
        MODIFIER_WITH_OVERLAY(5),
        OVERLAY_MODIFIER(6);

        private int index;

        private RenderPhase(int index){
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    public static class RenderTask {
        private final ShaderToolRenderUtils.RenderPhase phase;
        private final Consumer<RenderQuadArgsWrapper> renderMethod;
        private final RenderQuadArgsWrapper wrapper;

        public RenderTask(ShaderToolRenderUtils.RenderPhase phase, Consumer<RenderQuadArgsWrapper> renderMethod, RenderQuadArgsWrapper wrapper) {
            this.phase = phase;
            this.renderMethod = renderMethod;
            this.wrapper = wrapper;
        }

        public ShaderToolRenderUtils.RenderPhase getPhase() {
            return phase;
        }

        public void renderQuad(){
            renderMethod.accept(wrapper);
        }
    }
}
