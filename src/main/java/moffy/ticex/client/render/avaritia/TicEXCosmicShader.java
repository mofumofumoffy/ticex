package moffy.ticex.client.render.avaritia;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import committee.nova.mods.avaritia.Const;
import committee.nova.mods.avaritia.client.AvaritiaForgeClient;
import committee.nova.mods.avaritia.client.shader.AvaritiaShaders;
import moffy.ticex.TicEX;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.event.RegisterShadersEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class TicEXCosmicShader {
    private final RenderStateShard.ShaderStateShard stateShard;
    private final RenderType cosmicRenderType;
    private final Map<ResourceLocation, RenderType> cosmicArmorRenderTypeCache = new HashMap<>();

    public int internalRenderTime;
    public float internalRenderFrame;

    public ShaderInstance shaderInstance;
    public Uniform cosmicTime;
    public Uniform cosmicYaw;
    public Uniform cosmicPitch;
    public Uniform cosmicExternalScale;
    public Uniform cosmicOpacity;
    public Uniform cosmicUVs;

    public final Function<ResourceLocation, float[]> cosmicUVGetter = Util.memoize(resourceLocation -> {
        float[] cosmicUV = new float[AvaritiaShaders.COSMIC_UVS.length];
        for (int i = 0; i < AvaritiaShaders.COSMIC_SPRITES.length; ++i) {
            TextureAtlasSprite sprite = Minecraft.getInstance()
                    .getTextureAtlas(resourceLocation)
                    .apply(Const.rl("misc/cosmic/cosmic_" + i));
            cosmicUV[i * 4] = sprite.getU0();
            cosmicUV[i * 4 + 1] = sprite.getV0();
            cosmicUV[i * 4 + 2] = sprite.getU1();
            cosmicUV[i * 4 + 3] = sprite.getV1();
        }
        return cosmicUV;
    });

    public TicEXCosmicShader() {
        stateShard = new RenderStateShard.ShaderStateShard(() -> shaderInstance);
        cosmicRenderType = RenderType.create(
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

    }

    public ShaderInstance getShaderInstance() {
        return shaderInstance;
    }

    public RenderType getCosmicRenderType() {
        return cosmicRenderType;
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
                        .setLightmapState(RenderType.LIGHTMAP)
                        .setCullState(RenderType.NO_CULL)
                        .setOverlayState(RenderType.OVERLAY)
                        .setLayeringState(RenderType.VIEW_OFFSET_Z_LAYERING)
                        .createCompositeState(true)
        );

        cosmicArmorRenderTypeCache.put(texture, renderType);
        return renderType;
    }

    public void registerShader(RegisterShadersEvent event) {
        try{
            event.registerShader(
                    new ShaderInstance(
                            event.getResourceProvider(),
                            TicEX.getResource("avaritia/materials/infinity"),
                            DefaultVertexFormat.BLOCK
                    ),
            e -> {
                shaderInstance = e;
                cosmicTime = Objects.requireNonNull(shaderInstance.getUniform("time"));
                cosmicYaw = Objects.requireNonNull(shaderInstance.getUniform("yaw"));
                cosmicPitch = Objects.requireNonNull(shaderInstance.getUniform("pitch"));
                cosmicExternalScale = Objects.requireNonNull(shaderInstance.getUniform("externalScale"));
                cosmicOpacity = Objects.requireNonNull(shaderInstance.getUniform("opacity"));
                cosmicUVs = Objects.requireNonNull(shaderInstance.getUniform("cosmicuvs"));
                //cosmicTime.set((float) internalRenderTime + internalRenderFrame);
                shaderInstance.apply();
                initialize();
            }
            );
        }catch (IOException ignore){}
    }

    public void setupUniform(ResourceLocation atlas, boolean onGui) {
        /*cosmicTime.set(
                (System.currentTimeMillis() - internalRenderTime) / 2000.0F
        );

        float externalScale = onGui ? 100.0f : 1.0f;
        float yaw = 0f;
        float pitch = 0f;

        if(!onGui) {
            GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
            Camera mainCamera = gameRenderer.getMainCamera();

            yaw = (float) ((mainCamera.getYRot() * 2.0f * Math.PI) / 360.0);
            pitch = -(float) ((mainCamera.getXRot() * 2.0f * Math.PI) / 360.0);
        }

        cosmicUVs.set(this.cosmicUVGetter.apply(atlas));
        cosmicYaw.set(yaw);
        cosmicPitch.set(pitch);
        cosmicExternalScale.set(externalScale);*/
        final Minecraft mc = Minecraft.getInstance();
        float yaw = 0.0f;
        float pitch = 0.0f;
        float scale = 1f;
        if (AvaritiaForgeClient.inventoryRender || onGui) {
            scale = 100.0F;
        } else {
            yaw = (float) (mc.player.getYRot() * 2.0f * Math.PI / 360.0);
            pitch = -(float) (mc.player.getXRot() * 2.0f * Math.PI / 360.0);
        }
        cosmicTime.set(mc.level.getGameTime() % Integer.MAX_VALUE);
        cosmicYaw.set(yaw);
        cosmicPitch.set(pitch);
        cosmicExternalScale.set(scale);

        cosmicOpacity.set(1.0F);

        if (cosmicUVs != null) {
            cosmicUVs.set(this.cosmicUVGetter.apply(atlas));
        }
    }
}
