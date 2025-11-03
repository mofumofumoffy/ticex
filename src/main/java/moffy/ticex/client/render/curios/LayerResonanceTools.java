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
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class LayerResonanceTools<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    public static final float RADIUS = 1.4f;
    protected ItemRenderer itemRenderer;
    protected EntityRenderDispatcher entityRenderDispatcher;
    @Nullable
    private ICuriosItemHandler curiosInventory = null;

    public LayerResonanceTools(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
        this.entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
    }

    @Override
    public void render(@NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, @NotNull T pLivingEntity,
                       float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw,
                       float pHeadPitch) {

        if(curiosInventory == null) {
            LazyOptional<ICuriosItemHandler> cap = CuriosApi.getCuriosInventory(pLivingEntity);
            curiosInventory = cap.isPresent() ? cap.orElseThrow(IllegalStateException::new) : null;
        }

        if(curiosInventory != null) {
            curiosInventory.findFirstCurio(TicEXRegistry.RESONANCE_GAUNTLET.get()).ifPresent(slotResult -> {
                ItemStack stack = slotResult.stack();
                stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler -> {
                    renderTools(pPoseStack, pBuffer, pPackedLight, pLivingEntity, pPartialTick, itemHandler);
                });
            });
        }
    }

    private void renderTools(@NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, @NotNull T pLivingEntity, float pPartialTick, IItemHandler itemHandler) {
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

            double x = RADIUS * Math.cos(rotationAngle);
            double z = RADIUS * Math.sin(rotationAngle);

            pPoseStack.translate(x, 1.25f, 0.5 + z);
            pPoseStack.mulPose(Axis.ZP.rotation(-Mth.PI*0.5f));
            pPoseStack.translate(-0.5, -0.5, -0.5);

            pPoseStack.scale(1.5f, 1.5f, 1.5f);

            TicEXRenderUtils.renderTool(entityRenderDispatcher, itemRenderer, toolStack, pPoseStack, pBuffer, pLivingEntity, pPackedLight);

            pPoseStack.popPose();
        }
    }
}
