package moffy.ticex.client.shaders;

import com.mojang.blaze3d.vertex.PoseStack;
import moffy.ticex.client.providers.ShaderProvider;
import moffy.ticex.client.providers.renderer.ArmorContextRenderer;
import moffy.ticex.client.providers.context.RenderContext;
import moffy.ticex.client.providers.context.armor.RenderArmorPartContext;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.Material;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.client.armor.texture.TintedArmorTexture;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

public class TintedShaderArmorTexture extends TintedArmorTexture {

    private final ShaderProvider.Armor provider;
    private final Material textureMaterial;
    private final MaterialVariantId material;
    private ModDataNBT persistentData;
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

    public void setPersistentData(ModDataNBT persistentData){
        this.persistentData = persistentData;
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
                    persistentData,
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
