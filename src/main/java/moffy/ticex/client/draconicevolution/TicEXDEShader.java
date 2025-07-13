package moffy.ticex.client.draconicevolution;

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
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.Objects;

public final class TicEXDEShader extends BCShader<TicEXDEShader> {
    private final RenderStateShard.ShaderStateShard shaderState;

    private CCUniform scaleUniform;
    private CCUniform uv1OverrideUniform;
    private CCUniform uv2OverrideUniform;

    private CCUniform baseColorUniform;

    private final RenderType modifierRenderType;
    private final RenderType materialsRenderType;
    public boolean guiRender = false;

    public static TicEXDEShader instance = null;

    public TicEXDEShader(IEventBus bus) {
        super(new ResourceLocation(TicEX.MODID, "draconicevolution/trace"), DefaultVertexFormat.NEW_ENTITY);
        this.shaderState = new RenderStateShard.ShaderStateShard(this::getShaderInstance);
        Supplier<CompositeState.CompositeStateBuilder> shaderStateBaseFactory = () ->
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
        materialsRenderType = RenderType.create(
            TicEX.MODID + ":tool_evolved_materials",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            2097152,
            true,
            false,
            shaderStateBaseFactory.get()
                .setTransparencyState(RenderType.NO_TRANSPARENCY)
                .createCompositeState(true)
        );

        this.register(bus);
    }

    public static void init(IEventBus bus) {
        instance = new TicEXDEShader(bus).onShaderApplied(e ->
            e.getTimeUniform().glUniform1f((float) (ClientUtils.getRenderTime() / 20))
        );
    }

    public void setup(TechLevel techLevel) {
        glUniformBaseColor(this, techLevel, 1.0F);

        if(hasScaleUniform()) {
            getScaleUniform().glUniform1f(guiRender ? 0.1f : 1.0f);
        }
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

    public RenderType getMaterialsRenderType() {
        return materialsRenderType;
    }

    private static final float[][] baseColours = {
        { 0.0F, 0.5F, 0.8F, 1F },
        { 0.55F, 0.0F, 0.65F, 1F },
        { 0.8F, 0.5F, 0.1F, 1F },
        { 0.75F, 0.05F, 0.05F, 0.2F },
    };

    public static void glUniformBaseColor(BCShader<?> shader, TechLevel techLevel, float pulse) {
        if (shader instanceof TicEXDEShader toolShader) {
            if (toolShader.hasBaseColorUniform()) {
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


                toolShader.getBaseColorUniform().glUniform4f(r, g, b, a);
            }
        }
    }
}
