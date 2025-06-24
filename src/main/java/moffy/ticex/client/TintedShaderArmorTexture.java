package moffy.ticex.client;

import com.mojang.blaze3d.vertex.PoseStack;
import moffy.ticex.client.ShaderProvider.ArmorRenderArgsWrapper;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.client.armor.texture.TintedArmorTexture;

public class TintedShaderArmorTexture extends TintedArmorTexture {

    private ResourceLocation texture;
    private int color;
    private ShaderProvider<ArmorRenderArgsWrapper> provider;

    public TintedShaderArmorTexture(
        ResourceLocation texture,
        int color,
        ShaderProvider<ArmorRenderArgsWrapper> armorProvider
    ) {
        super(texture, color);
        this.texture = texture;
        this.color = color;
        this.provider = armorProvider;
    }

    @Override
    public int color() {
        return this.color;
    }

    @Override
    public TintedArmorTexture color(int color) {
        this.color = color;
        return this;
    }

    @Override
    public void renderTexture(
        Model model,
        PoseStack matrices,
        MultiBufferSource bufferSource,
        int packedLight,
        int packedOverlay,
        float red,
        float green,
        float blue,
        float alpha,
        boolean hasGlint
    ) {
        //super.renderTexture(model, matrices, bufferSource, packedLight, packedOverlay, red, green, blue, alpha, hasGlint);
        this.provider.renderOverLayer(
                new ArmorRenderArgsWrapper(
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
                    this.texture,
                    this.color
                )
            );
    }
}
