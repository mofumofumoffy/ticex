package moffy.ticex.mixin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import moffy.ticex.TicEXConfig;
import moffy.ticex.client.PartPredicate;
import moffy.ticex.client.ShaderProvider;
import moffy.ticex.client.ShaderToolQuad;
import moffy.ticex.client.ShaderToolRenderUtils;
import moffy.ticex.client.ShaderProvider.RenderQuadArgsWrapper;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@Mixin(value = ItemRenderer.class, priority = 1700)
public abstract class ItemRendererMixin {

    @Shadow
    private ItemColors itemColors;

    @Shadow
    public abstract void renderModelLists(BakedModel pModel, ItemStack pStack, int pCombinedLight, int pCombinedOverlay, PoseStack pPoseStack, VertexConsumer pBuffer);

    @SuppressWarnings("deprecation")
    @Inject(
        at = @At("head"),
        method = "render",
        cancellable = true
    )
    public void render(ItemStack pItemStack, ItemDisplayContext pDisplayContext, boolean pLeftHand, PoseStack pPoseStack, MultiBufferSource pBuffer, int pCombinedLight, int pCombinedOverlay, BakedModel pModel, CallbackInfo cb){

        if (!pItemStack.isEmpty() && TicEXConfig.USE_SHADER.get() && !TicEXRegistry.CUSTOM_MODELS.keySet().contains(pItemStack.getItem())){
            if(pItemStack.getItem() instanceof IModifiable){
                ToolStack tool = ToolStack.from(pItemStack);
                if(TicEXRegistry.TOOL_SHADERS.isToolTarget(tool)){
                    List<ShaderToolRenderUtils.RenderTask> renderQueue = new ArrayList<>();
                    Set<PartPredicate> seen = new HashSet<>();

                    pPoseStack.pushPose();

                    pModel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(pPoseStack, pModel, pDisplayContext, pLeftHand);
                    pPoseStack.translate(-0.5F, -0.5F, -0.5F);
                    for (var model : pModel.getRenderPasses(pItemStack, true)) {
                        for (var rendertype : model.getRenderTypes(pItemStack, true)) {
                            RandomSource randomsource = RandomSource.create();

                            for(Direction direction : Direction.values()) {
                                randomsource.setSeed(42L);
                                this.addShaderQuadToQueue(rendertype, pPoseStack, pBuffer, model.getQuads((BlockState)null, direction, randomsource), pItemStack, pCombinedLight, pCombinedOverlay, pDisplayContext, tool, renderQueue::add, seen);
                            }

                            randomsource.setSeed(42L);
                            this.addShaderQuadToQueue(rendertype, pPoseStack, pBuffer, model.getQuads((BlockState)null, (Direction)null, randomsource), pItemStack, pCombinedLight, pCombinedOverlay, pDisplayContext, tool, renderQueue::add, seen);
                        }
                    }

                    renderQueue.sort(Comparator.comparingInt(task -> ((ShaderToolRenderUtils.RenderTask)task).getPhase().getIndex()));
                    for(ShaderToolRenderUtils.RenderTask task : renderQueue){
                        task.renderQuad();
                    }

                    pPoseStack.popPose();
                    cb.cancel();
                }
            } 
        }
    }


    @Unique
    private void addShaderQuadToQueue(RenderType renderType, PoseStack pPoseStack, MultiBufferSource pBuffer, List<BakedQuad> pQuads, ItemStack pItemStack, int pCombinedLight, int pCombinedOverlay, ItemDisplayContext pDisplayContext, IToolStackView tool, Consumer<ShaderToolRenderUtils.RenderTask> addTaskFn, Set<PartPredicate> seenList){
        boolean flag = !pItemStack.isEmpty();

        for(BakedQuad bakedquad : pQuads) {
            int i = -1;
            if (flag && bakedquad.isTinted()) {
                i = itemColors.getColor(pItemStack, bakedquad.getTintIndex());
            }

            float f = (float)(i >> 16 & 255) / 255.0F;
            float f1 = (float)(i >> 8 & 255) / 255.0F;
            float f2 = (float)(i & 255) / 255.0F;

            RenderQuadArgsWrapper defaultWrapper = new RenderQuadArgsWrapper(renderType, pPoseStack, bakedquad, pCombinedOverlay, f, f1, f2, pCombinedLight, pCombinedOverlay, flag, pBuffer, pDisplayContext, tool);

            if(bakedquad instanceof ShaderToolQuad){
                PartPredicate predicate = ((ShaderToolQuad)bakedquad).getPredicate();
                ShaderProvider<RenderQuadArgsWrapper> provider = TicEXRegistry.TOOL_SHADERS.getProvider(predicate);

                Consumer<RenderQuadArgsWrapper> renderMethod = (wrapper->{
                    wrapper.renderQuadsWithConsumer();
                });
                
                if(provider != null){
                    //underlay
                    if(!seenList.contains(predicate)){
                        addTaskFn.accept(new ShaderToolRenderUtils.RenderTask(ShaderToolRenderUtils.RenderPhase.UNDERLAY, provider::renderUnderLayer, defaultWrapper));
                        seenList.add(predicate);
                    }

                    //overlay
                    if(predicate.isModifierId()){
                        addTaskFn.accept(new ShaderToolRenderUtils.RenderTask(ShaderToolRenderUtils.RenderPhase.OVERLAY_MODIFIER, provider::renderOverLayer, defaultWrapper));
                        addTaskFn.accept(new ShaderToolRenderUtils.RenderTask(ShaderToolRenderUtils.RenderPhase.MODIFIER_WITH_OVERLAY, renderMethod, defaultWrapper));
                    } else if(predicate.isMaterialVariantId()){
                        addTaskFn.accept(new ShaderToolRenderUtils.RenderTask(ShaderToolRenderUtils.RenderPhase.OVERLAY_MATERIAL, provider::renderOverLayer, defaultWrapper));
                        addTaskFn.accept(new ShaderToolRenderUtils.RenderTask(ShaderToolRenderUtils.RenderPhase.MATERIAL_WITH_OVERLAY, renderMethod, defaultWrapper));
                    }
                } else {
                    //normal items
                    if(predicate.isModifierId()){
                        addTaskFn.accept(new ShaderToolRenderUtils.RenderTask(ShaderToolRenderUtils.RenderPhase.NORMAL_MODIFIER, renderMethod, defaultWrapper));
                    } else if(predicate.isMaterialVariantId()){
                        addTaskFn.accept(new ShaderToolRenderUtils.RenderTask(ShaderToolRenderUtils.RenderPhase.NORMAL_MATERIAL, renderMethod, defaultWrapper));
                    }
                }
            }
        }
    }
}
