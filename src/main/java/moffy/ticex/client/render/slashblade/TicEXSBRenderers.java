package moffy.ticex.client.render.slashblade;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import moffy.overloaded_tinkering_lib.Config;
import moffy.overloaded_tinkering_lib.client.CustomTinkerRenders;
import moffy.overloaded_tinkering_lib.client.provider.ShaderProvider;
import moffy.overloaded_tinkering_lib.client.provider.context.ItemRenderContext;
import moffy.overloaded_tinkering_lib.client.provider.context.RenderContext;
import moffy.overloaded_tinkering_lib.client.provider.context.armor.RenderGenericContext;
import moffy.overloaded_tinkering_lib.client.provider.renderer.IGenericRenderer;
import moffy.ticex.TicEX;
import moffy.ticex.client.render.custom.DecoratedRenderType;
import moffy.ticex.lib.context.ContextFrame;
import moffy.ticex.lib.context.ContextFrameScope;
import moffy.ticex.lib.context.TicEXContexts;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class TicEXSBRenderers {

    private static final Map<RenderType, DecoratedRenderType> renderTypeCache = new HashMap<>();

    @OnlyIn(Dist.CLIENT)
    public static void renderWrapped(IBladeRenderer renderer, ItemStack stack, WavefrontObject model, String target, ResourceLocation texture, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, Function<ResourceLocation, RenderType> renderTypeGetter, boolean enableEffect) {
        ItemRenderContext itemRenderContext = TicEXContexts.SB_RENDERING_CONTEXT.get();

        if(!(stack.getItem() instanceof IModifiable)) {
            renderer.render(stack, model, target, texture, matrixStackIn, bufferIn, packedLightIn, renderTypeGetter, enableEffect);
            return;
        }

        ToolStack tool = ToolStack.from(stack);

        if(tool.getModifierLevel(TicEXRegistry.KOSHIRAE_MODIFIER.get()) > 0) {
            renderer.render(stack, model, target, texture, matrixStackIn, bufferIn, packedLightIn, renderTypeGetter, enableEffect);
            return;
        }

        MaterialNBT materials = tool.getMaterials();
        for (int i = 0; i < materials.size(); i++) {
            MaterialVariant material = materials.get(i);
            SBToolRenderType.PartType partType = SBToolRenderType.PartType.byIndex(i);
            if (partType == null) continue;

            ShaderProvider.Generic shaderProvider = CustomTinkerRenders.GENERIC_SHADERS.getShaderProvider(material.getVariant());
            boolean useShader = shaderProvider != null && Config.USE_SHADER.get();

            Mutable<Color> color = new MutableObject<>(null);

            ResourceLocation bladeTexture = partType.tryTexture(material.getVariant(), () -> {
                if(!useShader) {
                    Optional<MaterialRenderInfo> optional = MaterialRenderInfoLoader.INSTANCE.getRenderInfo(material.getVariant());
                    optional.ifPresent(materialRenderInfo -> {
                        color.setValue(new Color(materialRenderInfo.vertexColor()));
                    });
                }
            });

            RenderType renderType = renderTypeGetter.apply(bladeTexture);
            Function<ResourceLocation, RenderType> paintedRenderTypeGetter = loc -> renderType;

            try(ContextFrame<Color> frame = TicEXContexts.SB_COLOR_OVERRIDE.open(color.getValue())) {
                if (useShader) {
                    renderWithShader(renderer, material.getVariant(), partType, shaderProvider, itemRenderContext, model, target, texture, paintedRenderTypeGetter, enableEffect);
                } else {
                    renderer.render(stack, model, target, texture, matrixStackIn, bufferIn, packedLightIn, paintedRenderTypeGetter, enableEffect);
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderWithShader(IBladeRenderer renderer, MaterialVariantId material, SBToolRenderType.PartType partType, ShaderProvider.Generic shaderProvider, ItemRenderContext itemRenderContext,
                                        WavefrontObject model, String target, ResourceLocation texture, Function<ResourceLocation, RenderType> renderTypeGetter, boolean enableEffect) {

        ItemStack stack = itemRenderContext.itemStack();
        MultiBufferSource bufferSource = itemRenderContext.bufferSource();

        Material atlasMaterial = new Material(
                InventoryMenu.BLOCK_ATLAS,
                TicEX.getResource("obj_tool/slashblade_tool/" + partType.getName())
        );

        shaderProvider.prepareRenderMaterial(material);
        RenderContext renderContext = new RenderContext(
                bufferSource,
                1.0f, 1.0f, 1.0f, 1.0f,
                itemRenderContext.poseStack(),
                itemRenderContext.combinedLight(),
                itemRenderContext.combinedOverlay()
        );

        RenderType renderType = renderTypeGetter.apply(texture);
        RenderGenericContext genericContext = new RenderGenericContext(
                renderContext, atlasMaterial.atlasLocation(), renderType,
                rt -> bufferSource.getBuffer(getDecoratedRenderType(rt)),
                itemRenderContext.displayContext() == ItemDisplayContext.GUI
        );

        shaderProvider.renderOverlay(genericContext, new IGenericRenderer() {
            @Override
            public void render(VertexConsumer vertexConsumer, float red, float green, float blue, float alpha, PoseStack poseStack, int combinedLight, int combinedOverlay) {
                try(ContextFrameScope scope = new ContextFrameScope()) {
                    scope.addFrame(TicEXContexts.SB_SWAP_VC.open(vertexConsumer));
                    scope.addFrame(TicEXContexts.SB_FACE_SPRITE.open(atlasMaterial.sprite()));

                    renderer.render(stack, model, target, texture, poseStack, bufferSource, combinedLight, renderTypeGetter, enableEffect);
                }
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    public static RenderType getDecoratedRenderType(RenderType renderType) {
        if (renderTypeCache.containsKey(renderType)) {
            return renderTypeCache.get(renderType);
        }

        DecoratedRenderType decorated = DecoratedRenderType.decorate(renderType, null, VertexFormat.Mode.TRIANGLES);

        renderTypeCache.put(renderType, decorated);
        return decorated;
    }
}
