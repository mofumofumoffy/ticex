package moffy.ticex.client.modules.avaritia;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import committee.nova.mods.avaritia.Static;
import committee.nova.mods.avaritia.api.client.shader.CCShaderInstance;
import committee.nova.mods.avaritia.api.client.shader.CCUniform;
import moffy.ticex.TicEX;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.event.RegisterShadersEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class TicEXCosmicShader {
    private final RenderStateShard.ShaderStateShard stateShard;
    private final RenderType cosmicToolRenderType;
    private final Map<ResourceLocation, RenderType> cosmicArmorRenderTypeCache = new HashMap<>();

    public int internalRenderTime;
    public float internalRenderFrame;

    public CCShaderInstance shaderInstance;
    public CCUniform cosmicTime;
    public CCUniform cosmicYaw;
    public CCUniform cosmicPitch;
    public CCUniform cosmicExternalScale;
    public CCUniform cosmicOpacity;
    public CCUniform cosmicUVs;

    public TicEXCosmicShader() {
        stateShard = new RenderStateShard.ShaderStateShard(() -> shaderInstance);
        cosmicToolRenderType = RenderType.create(
                "ticex:cosmic",
                DefaultVertexFormat.BLOCK,
                VertexFormat.Mode.QUADS,
                2097152,
                true,
                false,
                RenderType.CompositeState.builder()
                        .setShaderState(stateShard)
                        .setLightmapState(RenderStateShard.LIGHTMAP)
                        .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                        .setTextureState(RenderStateShard.BLOCK_SHEET_MIPPED)
                        .createCompositeState(true)
        );
    }

    public void initialize() {
        float[] COSMIC_UVS = new float[40];
        for (int i = 0; i < 10; ++i) {
            TextureAtlasSprite sprite = Minecraft.getInstance()
                    .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(Static.rl("misc/cosmic_" + i));
            COSMIC_UVS[i * 4] = sprite.getU0();
            COSMIC_UVS[i * 4 + 1] = sprite.getV0();
            COSMIC_UVS[i * 4 + 2] = sprite.getU1();
            COSMIC_UVS[i * 4 + 3] = sprite.getV1();
        }

        if (cosmicUVs != null) {
            cosmicUVs.set(COSMIC_UVS);
        }
    }

    public CCShaderInstance getShaderInstance() {
        return shaderInstance;
    }

    public RenderType getCosmicToolRenderType() {
        return cosmicToolRenderType;
    }

    public RenderType getCosmicRenderTypeArmor(ResourceLocation texture) {
        if (cosmicArmorRenderTypeCache.containsKey(texture)) {
            return cosmicArmorRenderTypeCache.get(texture);
        }

        var renderType = RenderType.create(
                "ticex:cosmic_armor",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                256,
                true,
                false,
                RenderType.CompositeState.builder()
                        .setShaderState(stateShard)
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                        .setTransparencyState(RenderType.NO_TRANSPARENCY)
                        .setCullState(RenderType.NO_CULL)
                        .setLightmapState(RenderType.LIGHTMAP)
                        .setOverlayState(RenderType.OVERLAY)
                        .setLayeringState(RenderType.VIEW_OFFSET_Z_LAYERING)
                        .createCompositeState(true)
        );

        cosmicArmorRenderTypeCache.put(texture, renderType);
        return renderType;
    }

    public void registerShader(RegisterShadersEvent event) {
        event.registerShader(
                CCShaderInstance.create(
                        event.getResourceProvider(),
                        new ResourceLocation(TicEX.MODID, "avaritia/materials/infinity"),
                        DefaultVertexFormat.BLOCK
                ),
                e -> {
                    shaderInstance = (CCShaderInstance) e;
                    cosmicTime = Objects.requireNonNull(shaderInstance.getUniform("time"));
                    cosmicYaw = Objects.requireNonNull(shaderInstance.getUniform("yaw"));
                    cosmicPitch = Objects.requireNonNull(shaderInstance.getUniform("pitch"));
                    cosmicExternalScale = Objects.requireNonNull(shaderInstance.getUniform("externalScale"));
                    cosmicOpacity = Objects.requireNonNull(shaderInstance.getUniform("opacity"));
                    cosmicUVs = Objects.requireNonNull(shaderInstance.getUniform("cosmicuvs"));
                    cosmicTime.set((float) internalRenderTime + internalRenderFrame);
                    shaderInstance.onApply(() -> cosmicTime.set((float) internalRenderTime + internalRenderFrame));
                    initialize();
                }
        );
    }

    public void setupUniform() {
        cosmicTime.set(
                (System.currentTimeMillis() - internalRenderTime) / 2000.0F
        );
    }
}
