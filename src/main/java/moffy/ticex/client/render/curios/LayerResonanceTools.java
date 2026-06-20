package moffy.ticex.client.render.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import moffy.ticex.client.render.ticex.TicEXRenderUtils;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.HashMap;
import java.util.Map;

public class LayerResonanceTools implements ICurioRenderer {

    public static final float RADIUS = 1.4f;
    protected ItemRenderer itemRenderer;
    protected EntityRenderDispatcher entityRenderDispatcher;
    private final Map<LivingEntity, ICuriosItemHandler> curiosInventoryCache = new HashMap<>();

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(
            ItemStack stack,
            SlotContext slotContext,
            PoseStack matrixStack,
            RenderLayerParent<T, M> renderLayerParent,
            MultiBufferSource renderTypeBuffer,
            int light, float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        if(itemRenderer == null){
            itemRenderer = Minecraft.getInstance().getItemRenderer();
        }

        if(entityRenderDispatcher == null){
            entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        }

        if(slotContext.visible()){
            stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler -> {
                renderTools(matrixStack, renderTypeBuffer, light, slotContext.entity(), partialTicks, itemHandler);
            });
        }
    }

    private void renderTools(@NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, @NotNull LivingEntity pLivingEntity, float pPartialTick, IItemHandler itemHandler) {
        int tools = 0;
        for(int i = 0; i < itemHandler.getSlots(); i++) {
            if(!itemHandler.getStackInSlot(i).isEmpty()){
                tools++;
            }
        }

        for(int i = 0; i < itemHandler.getSlots(); i++){
            ItemStack toolStack = itemHandler.getStackInSlot(i);
            if (toolStack.isEmpty()) {
                continue;
            }

            pPoseStack.pushPose();

            float time = pLivingEntity.tickCount + pPartialTick;
            double baseAngle = 2 * Math.PI / tools * i;
            double rotationAngle = baseAngle + (time * 0.07);
            //double rotationAngle = 0f;

            double x = RADIUS * Math.cos(rotationAngle);
            double z = RADIUS * Math.sin(rotationAngle);

            pPoseStack.translate(x, 0.25f, z);
            pPoseStack.mulPose(Axis.ZP.rotation(-Mth.PI*0.5f));

            pPoseStack.scale(1.5f, 1.5f, 1.5f);
            pPoseStack.mulPose(Axis.YP.rotationDegrees(-180f));
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(-45f));

            TicEXRenderUtils.renderTool(entityRenderDispatcher, itemRenderer, toolStack, pPoseStack, pBuffer, pLivingEntity, pPackedLight);

            pPoseStack.popPose();
        }
    }
}
