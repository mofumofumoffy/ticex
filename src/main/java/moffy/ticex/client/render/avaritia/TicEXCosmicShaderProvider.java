package moffy.ticex.client.render.avaritia;

import com.mojang.blaze3d.vertex.VertexConsumer;
import moffy.ticex.client.render.provider.context.ItemRenderContext;
import moffy.ticex.client.render.provider.context.armor.RenderArmorPartContext;
import moffy.ticex.client.render.provider.context.tool.RenderGenericContext;
import moffy.ticex.client.render.provider.context.tool.RenderQuadContext;
import moffy.ticex.client.render.provider.renderer.IArmorPartContextRenderer;
import moffy.ticex.client.render.provider.renderer.IGenericRenderer;
import moffy.ticex.client.render.provider.renderer.IQuadContextRenderer;
import moffy.ticex.client.render.shader.ShaderProvider;
import moffy.ticex.client.render.ticex.TicEXToolRenders;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.Objects;

public class TicEXCosmicShaderProvider {
    private static TicEXCosmicShader shader;

    public static void init(IEventBus eventBus) {
        shader = new TicEXCosmicShader();
        eventBus.addListener(shader::registerShader);
    }

    public static class Material extends ShaderProvider.Tool {
        private VertexConsumer buffer;

        @Override
        public void renderOverlay(RenderQuadContext quadContext, IQuadContextRenderer renderer) {
            VertexConsumer consumer = Objects.requireNonNullElseGet(buffer, quadContext::getConsumer);

            renderer.render(quadContext.renderContext(), quadContext.quad(), consumer);
        }

        @Override
        public void renderUnderlay(RenderQuadContext quadContext, IQuadContextRenderer consumer) {
        }

        @Override
        public void prepareRenderItem(ItemRenderContext context) {
            buffer = null;
        }

        @Override
        public void startRenderBatch(ItemRenderContext context, TicEXToolRenders.RenderPhase phase) {
            RenderType renderType = shader.getCosmicRenderType();
            buffer = context.bufferSource().getBuffer(renderType);

            // setup uniform
            shader.setupUniform(InventoryMenu.BLOCK_ATLAS,
                    context.displayContext() == ItemDisplayContext.GUI);
        }

        @Override
        public void endRenderBatch(ItemRenderContext context, TicEXToolRenders.RenderPhase phase) {
        }

        @Override
        public ShaderInstance getShaderInstance() {
            return shader.getShaderInstance();
        }
    }

    public static class Armor extends ShaderProvider.Armor {
        @Override
        public void renderOverlay(RenderArmorPartContext quadContext, IArmorPartContextRenderer renderer) {
            VertexConsumer buffer = quadContext.material().buffer(
                    quadContext.renderContext().bufferSource(),
                    shader::getCosmicRenderTypeArmor
            );



            shader.setupUniform(quadContext.material().atlasLocation(), false);

            renderer.render(
                    quadContext.renderContext(),
                    quadContext.model(),
                    buffer
            );
        }

        @Override
        public void renderUnderlay(RenderArmorPartContext quadContext, IArmorPartContextRenderer bakedConsumer) {
        }

        @Override
        public ShaderInstance getShaderInstance() {
            return shader.getShaderInstance();
        }
    }

    public static TicEXCosmicShader getShader() {
        return shader;
    }

    public static class Generic extends ShaderProvider.Generic {

        @Override
        public void renderOverlay(RenderGenericContext context, IGenericRenderer renderer) {
            VertexConsumer vertexConsumer = context.bufferGetter().get(shader.getCosmicRenderType());

            shader.setupUniform(context.atlasLocation(), context.onGui());

            renderer.render(
                    vertexConsumer, context.renderContext(),
                    1.0f, 1.0f, 1.0f, 1.0f
            );
        }

        @Override
        public void renderUnderlay(RenderGenericContext quadContext, IGenericRenderer renderer) {
        }

        @Override
        public ShaderInstance getShaderInstance() {
            return shader.getShaderInstance();
        }
    }
}
