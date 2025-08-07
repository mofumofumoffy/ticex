package moffy.ticex.client.modules.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import moffy.ticex.lib.utils.TicEXSBUtils;
import moffy.ticex.lib.utils.TicEXTaczUtils;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;

public class LayerResonanceTools <T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    private static final float RADIUS = 1.4f;
    protected ItemRenderer itemRenderer;

    public LayerResonanceTools(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
        itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(@NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, @NotNull T pLivingEntity,
                       float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw,
                       float pHeadPitch) {

        CuriosApi.getCuriosInventory(pLivingEntity).ifPresent(handler -> {
            handler.findFirstCurio(TicEXRegistry.RESONANCE_GAUNTLET.get()).ifPresent(slotResult -> {
                ItemStack stack = slotResult.stack();
                stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler -> {
                    int amount = itemHandler.getSlots();
                    for(int i = 0; i < amount; i++){
                        ItemStack toolStack = itemHandler.getStackInSlot(i);
                        if(!toolStack.isEmpty()){
                            boolean isNormalRender = true;
                            pPoseStack.pushPose();

                            float time = pLivingEntity.tickCount + pPartialTick;
                            double baseAngle = 2 * Math.PI / amount * i;
                            double rotationAngle = baseAngle + (time * 0.07);

                            double x = RADIUS * Math.cos(rotationAngle);
                            double z = RADIUS * Math.sin(rotationAngle);

                            pPoseStack.translate(x, 1.25f, 0.5 + z);
                            pPoseStack.mulPose(Axis.ZP.rotationDegrees(45));
                            pPoseStack.translate(-0.5, -0.5, -0.5);

                            pPoseStack.scale(1.5f, 1.5f, 1.5f);

                            if(ModList.get().isLoaded("slashblade")){
                                isNormalRender = !TicEXSBUtils.renderBladeTool(toolStack, pPartialTick, pPoseStack, pBuffer, pPackedLight);
                            }

                            if(ModList.get().isLoaded("tacz")){
                                isNormalRender = isNormalRender && !TicEXTaczUtils.renderGunTool(itemRenderer,toolStack, pPoseStack, pBuffer, pLivingEntity, pPackedLight);
                            }

                            if(isNormalRender){

                                itemRenderer.renderStatic(toolStack, ItemDisplayContext.GROUND, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, pLivingEntity.level(), pLivingEntity.getId());

                            }

                            pPoseStack.popPose();
                        }
                    }
                });
            });
        });
    }

}
