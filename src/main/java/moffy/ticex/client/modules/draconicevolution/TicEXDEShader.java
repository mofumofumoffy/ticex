package moffy.ticex.client.modules.draconicevolution;

import codechicken.lib.render.shader.CCUniform;
import codechicken.lib.util.ClientUtils;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.client.shader.BCShader;
import com.google.common.base.Supplier;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import moffy.ticex.TicEX;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class TicEXDEShader extends BCShader<TicEXDEShader> {
    private static final float[][] baseColours = {
            {0.0F, 0.5F, 0.8F, 1F},
            {0.55F, 0.0F, 0.65F, 1F},
            {0.8F, 0.5F, 0.1F, 1F},
            {0.75F, 0.05F, 0.05F, 0.2F},
    };
    private final RenderStateShard.ShaderStateShard shaderState;
    private final Supplier<CompositeState.CompositeStateBuilder> shaderStateBaseFactory;
    private final Map<ResourceLocation, RenderType> armorRenderTypeCache = new HashMap<>();
    private final RenderType modifierRenderType;
    private CCUniform scaleUniform;
    private CCUniform uv1OverrideUniform;
    private CCUniform uv2OverrideUniform;
    private CCUniform baseColorUniform;

    public TicEXDEShader() {
        super(TicEX.getResource("draconicevolution/trace"), DefaultVertexFormat.NEW_ENTITY);
        shaderState = new RenderStateShard.ShaderStateShard(this::getShaderInstance);

        shaderStateBaseFactory = () ->
                CompositeState.builder()
                        .setShaderState(shaderState)
                        .setTextureState(RenderStateShard.BLOCK_SHEET_MIPPED)
                        .setLightmapState(RenderType.LIGHTMAP)
                        .setOverlayState(RenderStateShard.OVERLAY)
                        .setOutputState(RenderType.TRANSLUCENT_TARGET);

        modifierRenderType = RenderType.create(
                TicEX.MODID + ":tool_evolved_modifiers",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                2097152,
                true,
                false,
                shaderStateBaseFactory.get()
                        .setTransparencyState(RenderType.LIGHTNING_TRANSPARENCY)
                        .createCompositeState(true)
        );

        onShaderApplied(e ->
                e.getTimeUniform().glUniform1f((float) (ClientUtils.getRenderTime() / 20))
        );
    }

    public static void init() {
    }

    public static void glUniformBaseColor(TicEXDEShader shader, TechLevel techLevel, float pulse) {
        if (shader.hasBaseColorUniform()) {
            float[] baseColour = baseColours[techLevel.index];
            float r = baseColour[0];
            float g = baseColour[1];
            float b = baseColour[2];
            float a = baseColour[3];
            switch (techLevel) {
                case DRACONIUM:
                case WYVERN:
                case DRACONIC:
                    a *= 1.0F + pulse;
                    break;
                case CHAOTIC:
                    r += pulse * 0.2F;
                    g += pulse * 0.2F;
                    b += pulse * 0.2F;

            }
            shader.getBaseColorUniform().glUniform4f(r, g, b, a);
        }
    }

    public void setupUniforms(TechLevel techLevel, float scale) {
        glUniformBaseColor(this, techLevel, 1.0F);
        scaleUniform.set(scale);
    }

    public CCUniform getUv1OverrideUniform() {
        return Objects.requireNonNull(uv1OverrideUniform, missingUniformMessage("UV1Override"));
    }

    public boolean hasScaleUniform() {
        return scaleUniform != null;
    }

    public CCUniform getScaleUniform() {
        return Objects.requireNonNull(scaleUniform, missingUniformMessage("Scale"));
    }

    public boolean hasUv1OverrideUniform() {
        return uv1OverrideUniform != null;
    }

    public CCUniform getUv2OverrideUniform() {
        return Objects.requireNonNull(uv2OverrideUniform, missingUniformMessage("UV2Override"));
    }

    public boolean hasUv2OverrideUniform() {
        return uv2OverrideUniform != null;
    }

    public CCUniform getBaseColorUniform() {
        return Objects.requireNonNull(baseColorUniform, missingUniformMessage("BaseColor"));
    }

    public boolean hasBaseColorUniform() {
        return baseColorUniform != null;
    }

    @Override
    protected void onShaderLoaded() {
        super.onShaderLoaded();
        scaleUniform = shaderInstance.getUniform("Scale");
        uv1OverrideUniform = shaderInstance.getUniform("UV1Override");
        uv2OverrideUniform = shaderInstance.getUniform("UV2Override");

        baseColorUniform = shaderInstance.getUniform("BaseColor");
    }

    public RenderType getModifierRenderType() {
        return modifierRenderType;
    }

    public RenderType createMaterialsRenderType(TechLevel techLevel) {
        return RenderType.create(
                TicEX.MODID + ":tool_evolved_" + techLevel.name().toLowerCase(),
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                2097152,
                true,
                false,
                shaderStateBaseFactory.get()
                        .setTransparencyState(RenderType.NO_TRANSPARENCY)
                        .createCompositeState(true)
        );
    }

    public RenderType createArmorRenderType(ResourceLocation atlasTexture) {
        if (armorRenderTypeCache.containsKey(atlasTexture)) {
            return armorRenderTypeCache.get(atlasTexture);
        }

        var renderType = RenderType.create(
                TicEX.MODID + ":tool_evolved",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                2097152,
                true,
                false,
                shaderStateBaseFactory.get()
                        .setTextureState(new RenderStateShard.TextureStateShard(atlasTexture, false, false))
                        .setLayeringState(RenderType.VIEW_OFFSET_Z_LAYERING)
                        .createCompositeState(true)
        );
        armorRenderTypeCache.put(atlasTexture, renderType);
        return renderType;
    }
}
