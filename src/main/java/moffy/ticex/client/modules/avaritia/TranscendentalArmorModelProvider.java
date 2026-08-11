package moffy.ticex.client.modules.avaritia;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import committee.nova.mods.avaritia.Res;
import committee.nova.mods.avaritia.client.model.entity.InfinityArmorModel;
import committee.nova.mods.avaritia.client.shader.AvaritiaRenderTypes;
import moffy.ticex.TicEX;
import moffy.ticex.TicEXConfig;
import moffy.ticex.client.providers.ExtraArmorModelProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.client.armor.ArmorModelManager;

public class TranscendentalArmorModelProvider extends ExtraArmorModelProvider {
    protected final ModelPart root = InfinityArmorModel.createLayer().bakeRoot();
    protected final ModelPart leftWing = root.getChild("left_wing");
    protected final ModelPart rightWing = root.getChild("right_wing");

    private HumanoidModel<?> base;
    private EquipmentSlot slot;

    private Minecraft mc;
    private MultiBufferSource multiBufferSource;
    private VertexConsumer wingCutoutNoCull = null;

    @Nullable
    private Player player = null;

    @Override
    public void providerSetup(@NotNull LivingEntity living, @NotNull ItemStack stack, @NotNull EquipmentSlot slot, @NotNull HumanoidModel<?> base, ArmorModelManager.@NotNull ArmorModel model) {
        this.base = base;
        this.slot = slot;

        this.mc = Minecraft.getInstance();
        this.multiBufferSource = mc.renderBuffers().bufferSource();

        if(living instanceof Player p){
            this.player = p;
        }
    }

    @Override
    public void renderExtraModel(@NotNull PoseStack matrices, @NotNull VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if(TicEXConfig.USE_ARMOR_MODEL == null || !TicEXConfig.USE_ARMOR_MODEL.get()){
            return;
        }
        if(this.player != null && this.slot == EquipmentSlot.CHEST && (this.player.getAbilities().flying || this.player.isFallFlying())){
            long time = 0;
            if (mc.level != null) {
                time = mc.level.getGameTime();
            }

            double pulse = Math.sin(time / 10.0D) * 0.5D + 0.5D;
            double pulse_mag_sqr = pulse * pulse * pulse * pulse * pulse * pulse;

            setupAnim();

            matrices.pushPose();
            this.leftWing.render(matrices,  multiBufferSource.getBuffer(RenderType.armorCutoutNoCull(Res.WING_TEX)), packedLightIn, OverlayTexture.NO_OVERLAY);
            this.rightWing.render(matrices,  multiBufferSource.getBuffer(RenderType.armorCutoutNoCull(Res.WING_TEX)), packedLightIn, OverlayTexture.NO_OVERLAY);

            leftWing.render(matrices, Res.ARMOR_WING_MASK.wrap(multiBufferSource.getBuffer(AvaritiaRenderTypes.COSMIC_ARMOR)), packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
            rightWing.render(matrices, Res.ARMOR_WING_MASK.wrap(multiBufferSource.getBuffer(AvaritiaRenderTypes.COSMIC_ARMOR)), packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);

            leftWing.render(matrices, multiBufferSource.getBuffer(AvaritiaRenderTypes.WingGlow(Res.WING_GLOW_TEX)), packedLightIn, OverlayTexture.NO_OVERLAY, 0.84F, 1.0F, 0.95F, (float) (pulse_mag_sqr * 0.5D));
            rightWing.render(matrices, multiBufferSource.getBuffer(AvaritiaRenderTypes.WingGlow(Res.WING_GLOW_TEX)), packedLightIn, OverlayTexture.NO_OVERLAY, 0.84F, 1.0F, 0.95F, (float) (pulse_mag_sqr * 0.5D));
            matrices.popPose();
        }
    }

    private void setupAnim(){
        leftWing.xRot = this.base.body.xRot;
        leftWing.yRot = this.base.body.yRot + (float) (Math.PI * 0.4);
        leftWing.zRot = this.base.body.zRot;

        rightWing.xRot = this.base.body.xRot;
        rightWing.yRot = this.base.body.yRot + (float) (-Math.PI * 0.4);
        rightWing.zRot = this.base.body.zRot;
    }
}
