package moffy.ticex.client.render.provider.context.armor;

import com.mojang.blaze3d.vertex.VertexConsumer;
import moffy.ticex.client.render.provider.context.RenderContext;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.Material;

public record RenderArmorPartContext(
        RenderContext renderContext,
        Model model,
        Material material,
        boolean hasGlint
) {
    public static VertexConsumer getNakedBuffer(MultiBufferSource bufferSource, Material material, boolean hasGlint) {
        return ItemRenderer.getArmorFoilBuffer(
                bufferSource,
                RenderType.armorCutoutNoCull(material.texture()),
                false,
                hasGlint
        );
    }

    public VertexConsumer getConsumer() {
        return getNakedBuffer(renderContext.bufferSource(), material, hasGlint);
    }
}
