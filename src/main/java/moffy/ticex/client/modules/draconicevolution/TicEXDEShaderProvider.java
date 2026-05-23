package moffy.ticex.client.modules.draconicevolution;

import com.brandon3055.brandonscore.api.TechLevel;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moffy.overloaded_tinkering_lib.client.provider.ShaderProvider;
import moffy.overloaded_tinkering_lib.client.provider.context.ItemRenderContext;
import moffy.overloaded_tinkering_lib.client.provider.context.RenderContext;
import moffy.overloaded_tinkering_lib.client.provider.context.RenderQuadContext;
import moffy.overloaded_tinkering_lib.client.provider.context.armor.RenderArmorPartContext;
import moffy.overloaded_tinkering_lib.client.provider.context.armor.RenderGenericContext;
import moffy.overloaded_tinkering_lib.client.provider.renderer.IArmorContextRenderer;
import moffy.overloaded_tinkering_lib.client.provider.renderer.IGenericRenderer;
import moffy.overloaded_tinkering_lib.client.provider.renderer.IQuadContextRenderer;
import moffy.overloaded_tinkering_lib.client.render.ToolRenders;
import moffy.ticex.lib.utils.TicEXDEUtils;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.eventbus.api.IEventBus;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;

public class TicEXDEShaderProvider {
    private static TicEXDEShader shader;

    public static void init(IEventBus eventBus) {
        shader = new TicEXDEShader();

        // register shaders
        shader.register(eventBus);
    }

    @Nullable
    public static TicEXDEShader getShader() {
        return shader;
    }

    public static class Material extends ShaderProvider.Tool {

        private final RenderType renderType;
        @NotNull
        private final TechLevel techLevel;
        @Nullable
        private VertexConsumer vertexConsumer;

        public Material(RenderType renderType, @NotNull TechLevel techLevel) {
            this.renderType = renderType;
            this.techLevel = techLevel;
        }

        @Override
        public void renderOverlay(RenderQuadContext quadContext, IQuadContextRenderer renderer) {

            if (vertexConsumer == null) {
                return;
            }

            RenderContext renderContext = quadContext.renderContext();
            renderer.render(
                    renderContext,
                    quadContext.quad(),
                    vertexConsumer,
                    techLevel == TechLevel.CHAOTIC ? 0.9f : renderContext.red(),
                    renderContext.green(),
                    renderContext.blue(),
                    renderContext.alpha()
            );
        }

        @Override
        public void renderUnderlay(RenderQuadContext quadContext, IQuadContextRenderer renderer) {

        }

        @Override
        public ShaderInstance getShaderInstance() {
            return shader.getShaderInstance();
        }

        @Override
        public void prepareRenderItem(ItemRenderContext itemRenderContext) {
        }

        @Override
        public void startRenderBatch(ItemRenderContext context, ToolRenders.RenderPhase phase) {
            vertexConsumer = context.bufferSource().getBuffer(renderType);

            // setup uniform
            shader.setupUniforms(techLevel, context.displayContext() == ItemDisplayContext.GUI ? 0.1f : 1.0f);
        }

        @Override
        public void endRenderBatch(ItemRenderContext context, ToolRenders.RenderPhase phase) {
            vertexConsumer = null;
        }

        @Override
        public void preRenderMaterial(ItemRenderContext context, MaterialVariantId materialId) {
            shader.setupUniforms(techLevel, context.displayContext() == ItemDisplayContext.GUI ? 0.1f : 1.0f);
        }
    }

    public static class Modifier extends ShaderProvider.Tool {

        private final RenderType renderType;
        @Nullable
        private TechLevel techLevel;
        @Nullable
        private VertexConsumer vertexConsumer;

        public Modifier() {
            renderType = shader.getModifierRenderType();
        }

        @Override
        public void renderOverlay(RenderQuadContext quadContext, IQuadContextRenderer renderer) {

            if (vertexConsumer == null) {
                return;
            }

            RenderContext renderContext = quadContext.renderContext();
            if (techLevel != null) {
                renderer.render(
                        renderContext,
                        quadContext.quad(),
                        vertexConsumer,
                        techLevel == TechLevel.CHAOTIC ? 0.9f : renderContext.red(),
                        renderContext.green(),
                        renderContext.blue(),
                        renderContext.alpha()
                );
            } else {
                renderer.render(
                        renderContext,
                        quadContext.quad(),
                        vertexConsumer
                );
            }
        }

        @Override
        public void renderUnderlay(RenderQuadContext quadContext, IQuadContextRenderer renderer) {

        }

        @Override
        public ShaderInstance getShaderInstance() {
            return shader.getShaderInstance();
        }

        @Override
        public void prepareRenderItem(ItemRenderContext context) {
            techLevel = null;
        }

        @Override
        public void startRenderBatch(ItemRenderContext context, ToolRenders.RenderPhase phase) {
            if (techLevel == null) {
                return;
            }

            vertexConsumer = context.bufferSource().getBuffer(renderType);

            // setup uniform
            shader.setupUniforms(techLevel, context.displayContext() == ItemDisplayContext.GUI ? 0.1f : 1.0f);

            if (context.displayContext() == ItemDisplayContext.GUI) {
                shader.getScaleUniform().glUniform1f(0.1f);
            }
        }

        @Override
        public void endRenderBatch(ItemRenderContext context, ToolRenders.RenderPhase phase) {
            vertexConsumer = null;
        }

        @Override
        public void preRenderModifier(ToolStack toolStack, ModifierId materialId) {
            techLevel = TicEXDEUtils.getTechLevel(toolStack);
        }

    }

    public static class Armor extends ShaderProvider.Armor {

        private final TechLevel techLevel;

        public Armor(@NotNull TechLevel techLevel) {
            this.techLevel = techLevel;
        }

        @Override
        public void renderOverlay(RenderArmorPartContext quadContext, IArmorContextRenderer renderer) {
            VertexConsumer buffer = quadContext.material().buffer(
                    quadContext.renderContext().bufferSource(),
                    shader::createArmorRenderType
            );
            shader.setupUniforms(techLevel, 0.1f);

            renderer.render(
                    quadContext.renderContext(),
                    quadContext.model(),
                    buffer
            );
        }

        @Override
        public void renderUnderlay(RenderArmorPartContext quadContext, IArmorContextRenderer renderer) {
        }

        @Override
        public ShaderInstance getShaderInstance() {
            return shader.getShaderInstance();
        }
    }

    public static class Generic extends ShaderProvider.Generic {

        private final RenderType renderType;
        private final TechLevel techLevel;

        public Generic(RenderType renderType, TechLevel techLevel) {
            this.techLevel = techLevel;
            this.renderType = renderType;
        }

        @Override
        public void renderOverlay(RenderGenericContext context, IGenericRenderer renderer) {
            VertexConsumer buffer = context.bufferGetter().get(renderType);

            shader.setupUniforms(techLevel, context.onGui() ? 0.1f : 1.0f);

            renderer.render(
                    buffer, context.renderContext()
            );
        }

        @Override
        public void renderUnderlay(RenderGenericContext context, IGenericRenderer renderer) {

        }

        @Override
        public ShaderInstance getShaderInstance() {
            return shader.getShaderInstance();
        }
    }
}
