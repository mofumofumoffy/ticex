package moffy.ticex.client.render.shader;

import com.mojang.blaze3d.vertex.PoseStack;
import moffy.ticex.client.render.provider.ArmorContextRenderer;
import moffy.ticex.client.render.provider.context.RenderContext;
import moffy.ticex.client.render.provider.context.armor.RenderArmorPartContext;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.Material;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.client.armor.texture.TintedArmorTexture;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

public class TintedShaderArmorTexture extends TintedArmorTexture {

    private final ShaderProvider.Armor provider;
    private final Material textureMaterial;
    private final MaterialVariantId material;
    private int color;

    public TintedShaderArmorTexture(
            Material textureMaterial,
            int color,
            ShaderProvider.Armor shaderProvider,
            MaterialVariantId material
    ) {
        super(textureMaterial.texture(), color);
        this.textureMaterial = textureMaterial;
        this.color = color;
        this.provider = shaderProvider;
        this.material = material;
    }

    @Override
    public int color() {
        return this.color;
    }

    @Override
    public @NotNull TintedArmorTexture color(int color) {
        this.color = color;
        return this;
    }

    @Override
    public void renderTexture(
            @NotNull Model model,
            @NotNull PoseStack matrices,
            @NotNull MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha,
            boolean hasGlint
    ) {

        if (this.provider != null) {
            RenderContext renderContext = new RenderContext(
                    bufferSource,
                    red, green, blue, alpha,
                    matrices, packedLight, packedOverlay
            );
            RenderArmorPartContext context = new RenderArmorPartContext(
                    renderContext,
                    model,
                    textureMaterial,
                    hasGlint
            );
            this.provider.prepareRenderMaterial(material);
            this.provider.renderOverlay(
                    context,
                    ArmorContextRenderer.RENDERER
            );
        }
    }
}
