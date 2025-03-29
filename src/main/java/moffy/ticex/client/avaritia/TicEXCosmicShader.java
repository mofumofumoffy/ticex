package moffy.ticex.client.avaritia;

import java.util.Objects;

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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public final class TicEXCosmicShader{

    public static TicEXCosmicShader instance = null;

    private final RenderStateShard.ShaderStateShard shader;
    private RenderType cosmicRenderTypeTool;

    public final float[] COSMIC_UVS = new float[40];
    public boolean inventoryRender = false;

    public int renderTime;
    public float renderFrame;

    public CCShaderInstance cosmicShader;

    public CCUniform cosmicTime;
    public CCUniform cosmicYaw;
    public CCUniform cosmicPitch;
    public CCUniform cosmicExternalScale;
    public CCUniform cosmicOpacity;
    public CCUniform cosmicUVs;

    public TicEXCosmicShader(){
        this.shader = new RenderStateShard.ShaderStateShard(() -> cosmicShader);
        cosmicRenderTypeTool = RenderType.create("ticex:cosmic",
                                    DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 2097152, true, false,
                                    RenderType.CompositeState.builder().setShaderState(shader)
                                            .setLightmapState(RenderStateShard.LIGHTMAP)
                                            .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                                            .setTextureState(RenderStateShard.BLOCK_SHEET_MIPPED)
                                            .createCompositeState(true)
                                );

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ()->()->{
            IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
            bus.addListener(this::onRegisterShaders);

            MinecraftForge.EVENT_BUS.addListener(this::clientTick);
            MinecraftForge.EVENT_BUS.addListener(this::renderTick);
            MinecraftForge.EVENT_BUS.addListener(this::drawScreenPre);
            MinecraftForge.EVENT_BUS.addListener(this::drawScreenPost);
        });
    }

    public CCShaderInstance getCosmicShader() {
        return cosmicShader;
    }

    public RenderType getCosmicRenderTypeTool() {
        return cosmicRenderTypeTool;
    }

    public RenderType getCosmicRenderTypeArmor(ResourceLocation texture) {
        return RenderType.create("", 
            DefaultVertexFormat.NEW_ENTITY, 
            VertexFormat.Mode.QUADS, 
            256, 
            true, 
            false, 
            RenderType.CompositeState.builder().setShaderState(shader)
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setTransparencyState(RenderType.NO_TRANSPARENCY)
                .setCullState(RenderType.NO_CULL)
                .setLightmapState(RenderType.LIGHTMAP)
                .setOverlayState(RenderType.OVERLAY)
                .setLayeringState(RenderType.VIEW_OFFSET_Z_LAYERING)
                .createCompositeState(true)
            );
    }

    public static void setup(){
        instance = new TicEXCosmicShader();
    }

    public void onRegisterShaders(RegisterShadersEvent event) {
        event.registerShader(CCShaderInstance.create(event.getResourceProvider(), new ResourceLocation(TicEX.MODID, "avaritia/materials/infinity"), DefaultVertexFormat.BLOCK), e -> {
            cosmicShader = (CCShaderInstance) e;
            cosmicTime = Objects.requireNonNull(cosmicShader.getUniform("time"));
            cosmicYaw = Objects.requireNonNull(cosmicShader.getUniform("yaw"));
            cosmicPitch = Objects.requireNonNull(cosmicShader.getUniform("pitch"));
            cosmicExternalScale = Objects.requireNonNull(cosmicShader.getUniform("externalScale"));
            cosmicOpacity = Objects.requireNonNull(cosmicShader.getUniform("opacity"));
            cosmicUVs = Objects.requireNonNull(cosmicShader.getUniform("cosmicuvs"));
            cosmicTime.set((float) renderTime + renderFrame);
            cosmicShader.onApply(() -> {
                cosmicTime.set((float) renderTime + renderFrame);
            });
        });
    }

    public void setupCosmic(){
        final Minecraft mc = Minecraft.getInstance();
        float yaw = 0.0f;
        float pitch = 0.0f;
        float scale = 1f;
        yaw = (float) (mc.player.getYRot() * 2.0f * Math.PI / 360.0);
        pitch = -(float) (mc.player.getXRot() * 2.0f * Math.PI / 360.0);
        
        TicEXCosmicShader.instance.cosmicTime
                .set((System.currentTimeMillis() - TicEXCosmicShader.instance.renderTime) / 2000.0F);
        TicEXCosmicShader.instance.cosmicYaw.set(yaw);
        TicEXCosmicShader.instance.cosmicPitch.set(pitch);
        TicEXCosmicShader.instance.cosmicExternalScale.set(scale);

        for (int i = 0; i < 10; ++i) {
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(Static.rl("misc/cosmic_" + i));
            TicEXCosmicShader.instance.COSMIC_UVS[i * 4] = sprite.getU0();
            TicEXCosmicShader.instance.COSMIC_UVS[i * 4 + 1] = sprite.getV0();
            TicEXCosmicShader.instance.COSMIC_UVS[i * 4 + 2] = sprite.getU1();
            TicEXCosmicShader.instance.COSMIC_UVS[i * 4 + 3] = sprite.getV1();
        }
        if (TicEXCosmicShader.instance.cosmicUVs != null) {
            TicEXCosmicShader.instance.cosmicUVs.set(TicEXCosmicShader.instance.COSMIC_UVS);
        }
}

    public void setupCosmic(ItemDisplayContext displayContext){
        final Minecraft mc = Minecraft.getInstance();
        float yaw = 0.0f;
        float pitch = 0.0f;
        float scale = 1f;
        if (TicEXCosmicShader.instance.inventoryRender || displayContext == ItemDisplayContext.GUI) {
            scale = 100.0F;
        } else {
            yaw = (float) (mc.player.getYRot() * 2.0f * Math.PI / 360.0);
            pitch = -(float) (mc.player.getXRot() * 2.0f * Math.PI / 360.0);
        }

        TicEXCosmicShader.instance.cosmicTime
                .set((System.currentTimeMillis() - TicEXCosmicShader.instance.renderTime) / 2000.0F);
        TicEXCosmicShader.instance.cosmicYaw.set(yaw);
        TicEXCosmicShader.instance.cosmicPitch.set(pitch);
        TicEXCosmicShader.instance.cosmicExternalScale.set(scale);

        for (int i = 0; i < 10; ++i) {
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(Static.rl("misc/cosmic_" + i));
            TicEXCosmicShader.instance.COSMIC_UVS[i * 4] = sprite.getU0();
            TicEXCosmicShader.instance.COSMIC_UVS[i * 4 + 1] = sprite.getV0();
            TicEXCosmicShader.instance.COSMIC_UVS[i * 4 + 2] = sprite.getU1();
            TicEXCosmicShader.instance.COSMIC_UVS[i * 4 + 3] = sprite.getV1();
        }
        if (TicEXCosmicShader.instance.cosmicUVs != null) {
            TicEXCosmicShader.instance.cosmicUVs.set(TicEXCosmicShader.instance.COSMIC_UVS);
        }
    }

    public void clientTick(TickEvent.ClientTickEvent event) {
        if (!Minecraft.getInstance().isPaused() && event.phase == TickEvent.Phase.END) {
            ++renderTime;
        }
    }

    public void renderTick(TickEvent.RenderTickEvent event) {
        if (!Minecraft.getInstance().isPaused() && event.phase == TickEvent.Phase.START) {
            renderFrame = event.renderTickTime;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void drawScreenPre(final ScreenEvent.Render.Pre e) {
        inventoryRender = true;
    }

    @OnlyIn(Dist.CLIENT)
    public void drawScreenPost(final ScreenEvent.Render.Post e) {
        inventoryRender = false;
    }
    
}
