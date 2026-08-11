package moffy.ticex.client.providers;

import moffy.ticex.client.providers.context.ItemRenderContext;
import moffy.ticex.client.providers.context.armor.RenderArmorPartContext;
import moffy.ticex.client.providers.context.tool.RenderGenericContext;
import moffy.ticex.client.providers.context.tool.RenderQuadContext;
import moffy.ticex.client.providers.renderer.IArmorPartContextRenderer;
import moffy.ticex.client.providers.renderer.IGenericRenderer;
import moffy.ticex.client.providers.renderer.IQuadContextRenderer;
import moffy.ticex.client.render.ToolRenders;
import net.minecraft.client.renderer.ShaderInstance;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public abstract class ShaderProvider<RENDER_CONTEXT, RENDERER> {
    public abstract void renderOverlay(RENDER_CONTEXT context, RENDERER renderer);

    public abstract void renderUnderlay(RENDER_CONTEXT context, RENDERER renderer);

    public void prepareRenderMaterial(MaterialVariantId materialId) {
    }

    public abstract ShaderInstance getShaderInstance();

    public static abstract class Tool extends ShaderProvider<RenderQuadContext, IQuadContextRenderer> {

        public abstract void prepareRenderItem(ItemRenderContext context);

        public void preRenderMaterial(ItemRenderContext context, MaterialVariantId materialId) {
        }

        public abstract void startRenderBatch(ItemRenderContext context, ToolRenders.RenderPhase phase);

        public abstract void endRenderBatch(ItemRenderContext context, ToolRenders.RenderPhase phase);

        public void prepareRenderModifier(ToolStack toolStack, ModifierId materialId) {
        }

        public void preRenderModifier(ToolStack toolStack, ModifierId materialId) {
        }
    }

    public static abstract class Armor extends ShaderProvider<RenderArmorPartContext, IArmorPartContextRenderer> {
    }

    public static abstract class Generic extends ShaderProvider<RenderGenericContext, IGenericRenderer> {
    }
}
