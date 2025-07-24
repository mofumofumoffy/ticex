package moffy.ticex.client.rendering.shader;

import moffy.ticex.client.rendering.ItemRenderContext;
import moffy.ticex.client.rendering.QuadRenderContext;
import moffy.ticex.client.rendering.ticex.TicEXToolRenders;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public abstract class ShaderProvider<QUAD_CONTEXT> {
    public abstract void renderQuadOverlay(QUAD_CONTEXT quadContext);

    public abstract void renderQuadUnderlay(QUAD_CONTEXT quadContext);

    public abstract ShaderInstance getShaderInstance();

    public static abstract class Tool extends ShaderProvider<QuadRenderContext.ToolQuadRenderContext> {
        public abstract void beginRender(ItemStack stack, ItemRenderContext context);

        public abstract void startRenderBatch(ItemRenderContext context, TicEXToolRenders.RenderPhase phase);

        public abstract void endRenderBatch(ItemRenderContext context, TicEXToolRenders.RenderPhase phase);

        public void beginRenderMaterial(ItemStack stack, MaterialVariantId materialId) {
        }

        public void beginRenderModifier(ToolStack toolStack, ModifierId materialId) {
        }

        public void preRenderMaterial(ItemStack stack, MaterialVariantId materialId) {
        }

        public void preRenderModifier(ToolStack toolStack, ModifierId materialId) {
        }
    }


    public static abstract class Armor extends ShaderProvider<QuadRenderContext.ArmorPartRenderContext> {
    }
}
