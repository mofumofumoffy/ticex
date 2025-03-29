package moffy.ticex.client;

import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import slimeknights.tconstruct.library.client.armor.AbstractArmorModel;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public abstract class ShaderProvider<T> {
    private Consumer<T> underlay;
    private Consumer<T> overlay;

    public ShaderProvider(Consumer<T> overlay){
        this((t)->{}, overlay);
    }

    public ShaderProvider(Consumer<T> underlay, Consumer<T> overlay){
        this.underlay = underlay;
        this.overlay = overlay;
    }

    public void renderUnderLayer(T wrapper){
        this.underlay.accept(wrapper);
    }

    public void renderOverLayer(T wrapper){
        this.overlay.accept(wrapper);
    }

    public static class Tool extends ShaderProvider<RenderQuadArgsWrapper>{
        public Tool(Consumer<RenderQuadArgsWrapper> overlay) {
            super(overlay);
        }

        public Tool(Consumer<RenderQuadArgsWrapper>underlay, Consumer<RenderQuadArgsWrapper> overlay) {
            super(underlay, overlay);
        }
    }

    public static class Armor extends ShaderProvider<ArmorRenderArgsWrapper>{
    
        public Armor(Consumer<ArmorRenderArgsWrapper> overlay){
            super(overlay);
        }

        public Armor(Consumer<ArmorRenderArgsWrapper> underlay, Consumer<ArmorRenderArgsWrapper> overlay) {
            super(underlay, overlay);
        }
        
    }

    public static class RenderQuadArgsWrapper{
        private final RenderType renderType;
        private final PoseStack poseStack;
        private final BakedQuad quad;
        private final float red;
        private final float green;
        private final float blue;
        private final float alpha;
        private final int light;
        private final int overlay;
        private final boolean readExistingColor;
        private final MultiBufferSource bufferSource;
        private final ItemDisplayContext displayContext;
        private final IToolStackView tool;

        
        public RenderQuadArgsWrapper(RenderType renderType, PoseStack poseStack, BakedQuad quad, float red, float green,
                float blue, float alpha, int light, int overlay, boolean readExistingColor, MultiBufferSource bufferSource, ItemDisplayContext displayContext, IToolStackView tool) {
            this.renderType = renderType;
            this.poseStack = poseStack;
            this.quad = quad;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
            this.light = light;
            this.overlay = overlay;
            this.readExistingColor = readExistingColor;
            this.bufferSource = bufferSource;
            this.displayContext = displayContext;
            this.tool = tool;
        }

        

        public RenderType getRenderType() {
            return renderType;
        }



        public PoseStack getPoseStack() {
            return poseStack;
        }



        public BakedQuad getQuad() {
            return quad;
        }



        public float getRed() {
            return red;
        }



        public float getGreen() {
            return green;
        }



        public float getBlue() {
            return blue;
        }



        public float getAlpha() {
            return alpha;
        }



        public int getLight() {
            return light;
        }



        public int getOverlay() {
            return overlay;
        }



        public boolean isReadExistingColor() {
            return readExistingColor;
        }



        public MultiBufferSource getBufferSource() {
            return bufferSource;
        }



        public ItemDisplayContext getDisplayContext() {
            return displayContext;
        }



        public IToolStackView getTool() {
            return tool;
        }



        public void renderQuadsWithConsumer(){
            renderQuadsWithConsumer(renderType);
        }

        public void renderQuadsWithConsumer(RenderType renderType){
            renderQuadsWithConsumer(renderType, quad, red, green, blue);
        }

        public void renderQuadsWithConsumer(RenderType renderType, BakedQuad quad, float red, float green, float blue){
            renderQuadsWithConsumer(poseStack, renderType, quad, red, green, blue, light, overlay);
        }

        public void renderQuadsWithConsumer(PoseStack poseStack, RenderType renderType, BakedQuad quad, float red, float green, float blue, int light, int overlay) {
            VertexConsumer consumer = bufferSource.getBuffer(renderType);
            consumer.putBulkData(poseStack.last(), quad, red, green, blue, light, overlay);
        }
    }

    public static class ArmorRenderArgsWrapper{
        private final Model model;
        private final PoseStack matrices;
        private final MultiBufferSource bufferSource;
        private final int packedLight;
        private final int packedOverlay;
        private final float red;
        private final float green;
        private final float blue; 
        private final float alpha;
        private final boolean hasGlint;
        private final ResourceLocation texture;
        private final int color;
        
        public ArmorRenderArgsWrapper(Model model, PoseStack matrices, MultiBufferSource bufferSource, int packedLight,
                int packedOverlay, float red, float green, float blue, float alpha, boolean hasGlint, ResourceLocation texture, int color) {
            this.model = model;
            this.matrices = matrices;
            this.bufferSource = bufferSource;
            this.packedLight = packedLight;
            this.packedOverlay = packedOverlay;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
            this.hasGlint = hasGlint;
            this.texture = texture;
            this.color = color;
        }

        public Model getModel() {
            return model;
        }

        public PoseStack getMatrices() {
            return matrices;
        }

        public MultiBufferSource getBufferSource() {
            return bufferSource;
        }

        public int getPackedLight() {
            return packedLight;
        }

        public int getPackedOverlay() {
            return packedOverlay;
        }

        public float getRed() {
            return red;
        }

        public float getGreen() {
            return green;
        }

        public float getBlue() {
            return blue;
        }

        public float getAlpha() {
            return alpha;
        }

        public boolean isHasGlint() {
            return hasGlint;
        }

        public ResourceLocation getTexture() {
            return texture;
        }

        public int getColor() {
            return color;
        }

        public void renderArmorWithConsumer(){
            renderArmorWithConsumer(ItemRenderer.getArmorFoilBuffer(bufferSource, RenderType.armorCutoutNoCull(texture), false, hasGlint));
        }

        public void renderArmorWithConsumer(VertexConsumer buffer){
            renderArmorWithConsumer(buffer, red, green, blue, texture);
        }

        public void renderArmorWithConsumer(VertexConsumer buffer, float red, float green, float blue, ResourceLocation texture){
            renderArmorWithConsumer(buffer, model, matrices, bufferSource, packedLight, packedOverlay, red, green, blue, blue, hasGlint, texture);
        }

        public void renderArmorWithConsumer(VertexConsumer buffer, Model model, PoseStack matrices, MultiBufferSource bufferSource, int packedLight,
            int packedOverlay, float red, float green, float blue, float alpha, boolean hasGlint, ResourceLocation texture){
                AbstractArmorModel.renderColored(model, matrices, buffer, packedLight, packedOverlay, color, red, green, blue, alpha);
        }
    }
}
