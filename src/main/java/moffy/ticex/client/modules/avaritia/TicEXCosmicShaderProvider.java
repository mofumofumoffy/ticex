package moffy.ticex.client.modules.avaritia;

import com.mojang.blaze3d.vertex.VertexConsumer;
import moffy.ticex.client.rendering.ItemRenderContext;
import moffy.ticex.client.rendering.QuadRenderContext;
import moffy.ticex.client.rendering.shader.ShaderProvider;
import moffy.ticex.client.rendering.ticex.TicEXToolRenders;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;

public class TicEXCosmicShaderProvider {
    private static TicEXCosmicShader shader;
    private static VertexConsumer buffer;

    public static void init(IEventBus eventBus) {
        shader = new TicEXCosmicShader();
        eventBus.addListener(shader::registerShader);
    }

    public static class Tool extends ShaderProvider.Tool {
        @Override
        public void renderQuadOverlay(QuadRenderContext.ToolQuadRenderContext quadContext) {
            if (buffer != null) {
                quadContext.renderQuadOverrided(buffer);
            } else {
                quadContext.renderQuadNaked();
            }
        }

        @Override
        public void renderQuadUnderlay(QuadRenderContext.ToolQuadRenderContext quadContext) {
        }

        @Override
        public void beginRender(ItemStack stack, ItemRenderContext context) {
            buffer = null;
            shader.setupUniform();

            GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
            Camera mainCamera = gameRenderer.getMainCamera();

            float yaw = 0.0f;
            float pitch = 0.0f;
            float scale = 1.0f;
            if (context.displayContext() == ItemDisplayContext.GUI) {
                scale = 100.0f;
            } else {
                yaw = (float) ((mainCamera.getYRot() * 2.0f * Math.PI) / 360.0);
                pitch = -(float) ((mainCamera.getXRot() * 2.0f * Math.PI) / 360.0);
            }

            shader.cosmicYaw.set(yaw);
            shader.cosmicPitch.set(pitch);
            shader.cosmicExternalScale.set(scale);
        }

        @Override
        public void startRenderBatch(ItemRenderContext context, TicEXToolRenders.RenderPhase phase) {
            RenderType renderType = shader.getCosmicToolRenderType();
            buffer = context.bufferSource().getBuffer(renderType);
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
        public void renderQuadOverlay(QuadRenderContext.ArmorPartRenderContext quadContext) {
            VertexConsumer buffer = quadContext.sprite().wrap(
                    quadContext.bufferSource().getBuffer(shader.getCosmicRenderTypeArmor(quadContext.sprite().contents().name()))
            );
            shader.setupUniform();
            shader.cosmicExternalScale.set(1.0f);

            quadContext.renderArmorOverrided(buffer);
        }

        @Override
        public void renderQuadUnderlay(QuadRenderContext.ArmorPartRenderContext quadContext) {
        }

        @Override
        public ShaderInstance getShaderInstance() {
            return shader.getShaderInstance();
        }
    }
}
