package moffy.ticex.client.modules.avaritia;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import committee.nova.mods.avaritia.Const;
import committee.nova.mods.avaritia.client.AvaritiaForgeClient;
import committee.nova.mods.avaritia.client.shader.AvaritiaShaders;
import moffy.ticex.TicEX;
import moffy.ticex.modifier.ModifierOmnipotence;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterShadersEvent;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class TicEXCosmicShader {
    private final RenderStateShard.ShaderStateShard cosmicStateShard;
    private final RenderType cosmicRenderType;
    private ShaderInstance cosmicShaderInstance;
    private final RenderStateShard.ShaderStateShard hellStateShard;
    private final RenderType hellRenderType;
    private ShaderInstance hellShaderInstance;

    private final Map<ResourceLocation, RenderType> cosmicArmorRenderTypeCache = new HashMap<>();
    private final Map<ResourceLocation, RenderType> hellArmorRenderTypeCache = new HashMap<>();

    public CosmicShaderContext cosmicContext;
    public CosmicShaderContext hellContext;

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
        cosmicStateShard = new RenderStateShard.ShaderStateShard(() -> cosmicShaderInstance);
        cosmicRenderType = RenderType.create(
                "ticex:cosmic",
                DefaultVertexFormat.BLOCK,
                VertexFormat.Mode.QUADS,
                2097152,
                true,
                false,
                RenderType.CompositeState.builder()
                        .setShaderState(cosmicStateShard)
                        .setLightmapState(RenderStateShard.LIGHTMAP)
                        .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                        .setTextureState(RenderStateShard.BLOCK_SHEET_MIPPED)
                        .createCompositeState(true)
        );

        hellStateShard = new RenderStateShard.ShaderStateShard(() -> hellShaderInstance);
        hellRenderType = RenderType.create(
                "ticex:cosmic_hell",
                DefaultVertexFormat.BLOCK,
                VertexFormat.Mode.QUADS,
                2097152,
                true,
                false,
                RenderType.CompositeState.builder()
                        .setShaderState(hellStateShard)
                        .setLightmapState(RenderStateShard.LIGHTMAP)
                        .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                        .setTextureState(RenderStateShard.BLOCK_SHEET_MIPPED)
                        .createCompositeState(true)
        );
    }
    public void initialize() {

    }

    public RenderType getCosmicRenderType(ModDataNBT persistentData) {
        if(persistentData.getBoolean(ModifierOmnipotence.SLAUGHTER_LOC)){
            return hellRenderType;
        }
        return cosmicRenderType;
    }

    public RenderType getCosmicRenderTypeArmor(ResourceLocation texture, ModDataNBT persistentData) {
        if(persistentData.getBoolean(ModifierOmnipotence.SLAUGHTER_LOC)){
            return getCosmicRenderTypeArmor(texture, hellArmorRenderTypeCache);
        }
        return getCosmicRenderTypeArmor(texture, cosmicArmorRenderTypeCache);
    }

    public RenderType getCosmicRenderTypeArmor(ResourceLocation texture, Map<ResourceLocation, RenderType> renderTypeCache){
        if (renderTypeCache.containsKey(texture)) {
            return renderTypeCache.get(texture);
        }

        var renderType = RenderType.create(
                "ticex:cosmic_armor",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                256,
                true,
                false,
                RenderType.CompositeState.builder()
                        .setShaderState(cosmicStateShard)
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                        .setTransparencyState(RenderType.NO_TRANSPARENCY)
                        .setLightmapState(RenderType.LIGHTMAP)
                        .setCullState(RenderType.NO_CULL)
                        .setOverlayState(RenderType.OVERLAY)
                        .setLayeringState(RenderType.VIEW_OFFSET_Z_LAYERING)
                        .createCompositeState(true)
        );

        renderTypeCache.put(texture, renderType);
        return renderType;
    }

    public void registerShader(RegisterShadersEvent event) {
        try{
            event.registerShader(
                    new ShaderInstance(
                            event.getResourceProvider(),
                            TicEX.getResource("avaritia/infinity"),
                            DefaultVertexFormat.BLOCK
                    ),
            e -> {
                this.cosmicShaderInstance = e;
                this.cosmicContext = new CosmicShaderContext(
                        Objects.requireNonNull(this.cosmicShaderInstance.getUniform("time")),
                        Objects.requireNonNull(this.cosmicShaderInstance.getUniform("yaw")),
                        Objects.requireNonNull(this.cosmicShaderInstance.getUniform("pitch")),
                        Objects.requireNonNull(this.cosmicShaderInstance.getUniform("externalScale")),
                        Objects.requireNonNull(this.cosmicShaderInstance.getUniform("opacity")),
                        Objects.requireNonNull(this.cosmicShaderInstance.getUniform("cosmicuvs"))
                );
                this.cosmicShaderInstance.apply();
            });

            event.registerShader(
                    new ShaderInstance(
                            event.getResourceProvider(),
                            TicEX.getResource("avaritia/infinity_hell"),
                            DefaultVertexFormat.BLOCK
                    ),
                    e -> {
                        this.hellShaderInstance = e;
                        this.hellContext = new CosmicShaderContext(
                                Objects.requireNonNull(this.hellShaderInstance.getUniform("time")),
                                Objects.requireNonNull(this.hellShaderInstance.getUniform("yaw")),
                                Objects.requireNonNull(this.hellShaderInstance.getUniform("pitch")),
                                Objects.requireNonNull(this.hellShaderInstance.getUniform("externalScale")),
                                Objects.requireNonNull(this.hellShaderInstance.getUniform("opacity")),
                                Objects.requireNonNull(this.hellShaderInstance.getUniform("cosmicuvs"))
                        );
                        this.hellShaderInstance.apply();
                    });
        }catch (IOException e){
            TicEX.LOGGER.error("Shader Loading Err:",e);
        }
    }

    public void setupUniform(ResourceLocation atlas, boolean onGui) {
        this.setupUniform(atlas, onGui, null);
    }

    public void setupUniform(ResourceLocation atlas, boolean onGui, @Nullable ModDataNBT persistentData) {
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

        CosmicShaderContext context =cosmicContext;

        if(persistentData != null && persistentData.getBoolean(ModifierOmnipotence.SLAUGHTER_LOC)){
            context = hellContext;
        }

        context.cosmicTime.set(mc.level.getGameTime() % Integer.MAX_VALUE);
        context.cosmicYaw.set(yaw);
        context.cosmicPitch.set(pitch);
        context.cosmicExternalScale.set(scale);

        context.cosmicOpacity.set(1.0F);

        if (context.cosmicUVs != null) {
            context.cosmicUVs.set(this.cosmicUVGetter.apply(atlas));
        }
    }

    public record CosmicShaderContext(
            Uniform cosmicTime,
            Uniform cosmicYaw,
            Uniform cosmicPitch,
            Uniform cosmicExternalScale,
            Uniform cosmicOpacity,
            Uniform cosmicUVs
    ){

    }
}
