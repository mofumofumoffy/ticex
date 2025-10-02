package moffy.ticex.client.rendering.shader;

import com.mojang.blaze3d.vertex.PoseStack;
import moffy.ticex.client.rendering.QuadRenderContext.ArmorPartRenderContext;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.Material;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.client.armor.texture.TintedArmorTexture;

public class TintedShaderArmorTexture extends TintedArmorTexture {

    private final Material textureMaterial;
    private final ShaderProvider.Armor provider;
    private int color;

    public TintedShaderArmorTexture(
            Material textureMaterial,
            int color,
            ShaderProvider.Armor armorProvider
    ) {
        super(textureMaterial.texture(), color);
        this.textureMaterial = textureMaterial;
        this.color = color;
        this.provider = armorProvider;
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
//        super.renderTexture(model, matrices, bufferSource, packedLight, packedOverlay, red, green, blue, alpha, hasGlint);
        if (this.provider != null) {
            this.provider.renderQuadOverlay(
                    new ArmorPartRenderContext(
                            model,
                            matrices,
                            bufferSource,
                            packedLight,
                            packedOverlay,
                            red,
                            green,
                            blue,
                            alpha,
                            hasGlint,
                            this.textureMaterial,
                            this.color
                    )
            );
        }
    }
}
