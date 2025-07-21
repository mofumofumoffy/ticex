package moffy.ticex.client.modules.draconicevolution;

import com.brandon3055.brandonscore.api.TechLevel;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moffy.ticex.client.rendering.ItemRenderContext;
import moffy.ticex.client.rendering.QuadRenderContext;
import moffy.ticex.client.rendering.shader.ShaderProvider;
import moffy.ticex.client.rendering.ticex.TicEXToolRenders;
import moffy.ticex.lib.utils.TicEXDEUtils;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
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
        public void renderQuadOverlay(QuadRenderContext.ToolQuadRenderContext quadContext) {
            if (vertexConsumer == null) {
                return;
            }

            quadContext.renderQuadOverrided(
                    vertexConsumer,
                    techLevel == TechLevel.CHAOTIC ? 0.9f : quadContext.red(),
                    quadContext.green(),
                    quadContext.blue(),
                    quadContext.quad()
            );
        }

        @Override
        public void renderQuadUnderlay(QuadRenderContext.ToolQuadRenderContext quadContext) {
        }

        @Override
        public ShaderInstance getShaderInstance() {
            return shader.getShaderInstance();
        }

        @Override
        public void beginRender(ItemStack stack, ItemRenderContext itemRenderContext) {
        }

        @Override
        public void startRenderBatch(ItemRenderContext context, TicEXToolRenders.RenderPhase phase) {
            vertexConsumer = context.bufferSource().getBuffer(renderType);

            // setup uniform
            shader.setupUniforms(techLevel);
            if (shader.hasScaleUniform()) {
                ItemDisplayContext itemDisplayContext = context.displayContext();
                shader.getScaleUniform().glUniform1f(itemDisplayContext == ItemDisplayContext.GUI ? 0.1f : 1.0f);
            }
        }

        @Override
        public void endRenderBatch(ItemRenderContext context, TicEXToolRenders.RenderPhase phase) {
            vertexConsumer = null;
        }

        @Override
        public void preRenderMaterial(ItemStack stack, MaterialVariantId materialId) {
            shader.setupUniforms(techLevel);
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
        public void renderQuadOverlay(QuadRenderContext.ToolQuadRenderContext quadContext) {
            if (vertexConsumer == null) {
                return;
            }

            if (techLevel != null) {
                quadContext.renderQuadOverrided(
                        vertexConsumer,
                        techLevel == TechLevel.CHAOTIC ? 0.9f : quadContext.red(),
                        quadContext.green(),
                        quadContext.blue(),
                        quadContext.quad()
                );
            } else {
                quadContext.renderQuadNaked();
            }
        }

        @Override
        public void renderQuadUnderlay(QuadRenderContext.ToolQuadRenderContext renderContext) {
        }

        @Override
        public ShaderInstance getShaderInstance() {
            return shader.getShaderInstance();
        }

        @Override
        public void beginRender(ItemStack stack, ItemRenderContext context) {
            techLevel = null;
        }

        @Override
        public void startRenderBatch(ItemRenderContext context, TicEXToolRenders.RenderPhase phase) {
            if (techLevel == null) {
                return;
            }

            vertexConsumer = context.bufferSource().getBuffer(renderType);

            // setup uniform
            shader.setupUniforms(techLevel);
            if (shader.hasScaleUniform()) {
                ItemDisplayContext itemDisplayContext = context.displayContext();
                shader.getScaleUniform().glUniform1f(itemDisplayContext == ItemDisplayContext.GUI ? 0.1f : 1.0f);
            }
        }

        @Override
        public void endRenderBatch(ItemRenderContext context, TicEXToolRenders.RenderPhase phase) {
            vertexConsumer = null;
        }

        @Override
        public void preRenderModifier(ToolStack toolStack, ModifierId materialId) {
            techLevel = TicEXDEUtils.getTechLevel(toolStack);
        }

    }
}
