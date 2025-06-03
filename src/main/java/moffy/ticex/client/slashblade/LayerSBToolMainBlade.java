package moffy.ticex.client.slashblade;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.joml.Matrix4f;
import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import jp.nyatla.nymmd.MmdException;
import jp.nyatla.nymmd.MmdMotionPlayerGL2;
import jp.nyatla.nymmd.MmdPmdModelMc;
import jp.nyatla.nymmd.MmdVmdMotionMc;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.client.renderer.layers.LayerMainBlade;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.renderer.model.BladeMotionManager;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.util.BladeRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import mods.flammpfeil.slashblade.init.DefaultResources;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.util.TimeValueHelper;
import mods.flammpfeil.slashblade.util.VectorHelper;
import moffy.ticex.item.modifiable.ModifiableSlashBladeItem;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class LayerSBToolMainBlade<T extends LivingEntity, M extends EntityModel<T>> extends LayerMainBlade<T, M>{

    final LazyOptional<MmdPmdModelMc> bladeholder = LazyOptional.of(() -> {
      try {
         return new MmdPmdModelMc(new ResourceLocation("slashblade", "model/bladeholder.pmd"));
      } catch (FileNotFoundException var1) {
         var1.printStackTrace();
      } catch (MmdException var2) {
         var2.printStackTrace();
      } catch (IOException var3) {
         var3.printStackTrace();
      }

      return null;
   });
   final LazyOptional<MmdMotionPlayerGL2> motionPlayer = LazyOptional.of(() -> {
      MmdMotionPlayerGL2 mmp = new MmdMotionPlayerGL2();
      this.bladeholder.ifPresent((pmd) -> {
         try {
            mmp.setPmd(pmd);
         } catch (MmdException var3) {
            var3.printStackTrace();
         }

      });
      return mmp;
   });

    public LayerSBToolMainBlade(RenderLayerParent<T, M> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void renderStandbyBlade(PoseStack matrixStack, MultiBufferSource bufferIn, int lightIn, ItemStack blade,
            T entity) {

        LazyOptional<ISlashBladeState> state = blade.getCapability(CapabilitySlashBlade.BLADESTATE);
        state.ifPresent(s -> {
	        double modelScaleBase = 0.0078125F;
	        double motionScale = 1.5 / 12.0;

	        String part;
	        try (MSAutoCloser msacA = MSAutoCloser.pushMatrix(matrixStack)) {
                matrixStack.translate(0, 1.5f, 0);
                var carrytype = s.getCarryType();
                switch(carrytype) {
                    case PSO2:
                        matrixStack.translate(1F,-1.125f, 0.20f);
                        matrixStack.mulPose(new Quaternionf().rotateZYX(-0.122173F, 0, 0));
                        if(Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON
                        && entity == Minecraft.getInstance().player) return;
                        break;

                    case KATANA:
                        matrixStack.translate(0.25F,-0.875f, -0.55f);
                        matrixStack.mulPose(new Quaternionf().rotateZYX(3.1415927F, 1.570796f, 0.261799F));
                        break;

                    case DEFAULT:
                        matrixStack.translate(0.25F,-0.875f, -0.55f);
                        matrixStack.mulPose(new Quaternionf().rotateZYX(0F, 1.570796f, 0.261799F));
                        break;

                    case NINJA:
                        matrixStack.translate(-0.5F,-2f, 0.20f);
                        matrixStack.mulPose(new Quaternionf().rotateZYX(-2.094395F, 0f, 3.1415927F));
                        if(Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON
                                && entity == Minecraft.getInstance().player) return;
                        break;

                    case RNINJA:
                        matrixStack.translate(0.5F,-2f, 0.20f);
                        matrixStack.mulPose(new Quaternionf().rotateZYX(-1.047198F, 0, 0));
                        if(Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON
                                && entity == Minecraft.getInstance().player) return;
                        break;

                    default:
                        // as default
                        matrixStack.translate(0.25F,-0.875f, -0.55f);
                        matrixStack.mulPose(new Quaternionf().rotateZYX(0F, 1.570796f, 0.261799F));
                        break;
                }

                float modelScale = (float) (modelScaleBase * (1.0f / motionScale));
                matrixStack.scale((float) motionScale, (float) motionScale, (float) motionScale);
                matrixStack.scale(modelScale, modelScale, modelScale);

                try (MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStack)) {
                    if (s.isBroken()) {
                        part = "blade_damaged";
                    } else {
                        part = "blade";
                    }

                    renderToolSlashBlade(blade, s, part, matrixStack, bufferIn, lightIn);
                    renderToolSlashBlade(blade, s, "sheath", matrixStack, bufferIn, lightIn);
                }
            }
        });
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource bufferIn, int lightIn, T entity, float limbSwing,
            float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        this.renderOffhandItem(matrixStack, bufferIn, lightIn, entity);

        float motionYOffset = 1.5f;
        double motionScale = 1.5 / 12.0;
        double modelScaleBase = 0.0078125F;

        ItemStack stack = entity.getItemInHand(InteractionHand.MAIN_HAND);

        if (stack.isEmpty())
            return;

        LazyOptional<ISlashBladeState> state = stack.getCapability(CapabilitySlashBlade.BLADESTATE);
        state.ifPresent(s -> {
            WavefrontObject model = BladeModelManager.getInstance().getModel(s.getModel().orElse(DefaultResources.resourceDefaultModel));

            motionPlayer.ifPresent(mmp -> {
                ComboState combo = ComboStateRegistry.REGISTRY.get().getValue(s.getComboSeq()) != null
                        ? ComboStateRegistry.REGISTRY.get().getValue(s.getComboSeq())
                        : ComboStateRegistry.NONE.get();

                double time = TimeValueHelper.getMSecFromTicks(
                        Math.max(0, entity.level().getGameTime() - s.getLastActionTime()) + partialTicks);

                while (combo != ComboStateRegistry.NONE.get() && combo.getTimeoutMS() < time) {
                    time -= combo.getTimeoutMS();

                    combo = ComboStateRegistry.REGISTRY.get().getValue(combo.getNextOfTimeout(entity)) != null
                            ? ComboStateRegistry.REGISTRY.get().getValue(combo.getNextOfTimeout(entity))
                            : ComboStateRegistry.NONE.get();
                }
                if (combo == ComboStateRegistry.NONE.get()) {
                    combo = ComboStateRegistry.REGISTRY.get().getValue(s.getComboRoot()) != null
                            ? ComboStateRegistry.REGISTRY.get().getValue(s.getComboRoot())
                            : ComboStateRegistry.STANDBY.get();
                }

                MmdVmdMotionMc motion = BladeMotionManager.getInstance().getMotion(combo.getMotionLoc());

                double maxSeconds = 0;
                try {
                    mmp.setVmd(motion);
                    maxSeconds = TimeValueHelper.getMSecFromFrames(motion.getMaxFrame());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                double start = TimeValueHelper.getMSecFromFrames(combo.getStartFrame());
                double end = TimeValueHelper.getMSecFromFrames(combo.getEndFrame());
                double span = Math.abs(end - start);

                span = Math.min(maxSeconds, span);

                if (combo.getLoop()) {
                    time = time % span;
                }
                time = Math.min(span, time);

                time = start + time;

                try {
                    mmp.updateMotion((float) time);
                } catch (MmdException e) {
                    e.printStackTrace();
                }

                try (MSAutoCloser msacA = MSAutoCloser.pushMatrix(matrixStack)) {

                    setUserPose(matrixStack, entity, partialTicks);



                    matrixStack.translate(0, motionYOffset, 0);

                    matrixStack.scale((float) motionScale, (float) motionScale, (float) motionScale);


                    matrixStack.mulPose(Axis.ZP.rotationDegrees(180));

                    try (MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStack)) {
                        int idx = mmp.getBoneIndexByName("hardpointA");

                        if (0 <= idx) {
                            float[] buf = new float[16];
                            mmp._skinning_mat[idx].getValue(buf);

                            Matrix4f mat = VectorHelper.matrix4fFromArray(buf);

                            matrixStack.scale(-1, 1, 1);
                            PoseStack.Pose entry = matrixStack.last();
                            entry.pose().mul(mat);
                            matrixStack.scale(-1, 1, 1);
                        }

                        float modelScale = (float) (modelScaleBase * (1.0f / motionScale));
                        matrixStack.scale(modelScale, modelScale, modelScale);

                        String part;
                        if (s.isBroken()) {
                            part = "blade_damaged";
                        } else {
                            part = "blade";
                        }

                        renderToolSlashBlade(stack, s, part, matrixStack, bufferIn, lightIn);
                    }

                    try (MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStack)) {
                        int idx = mmp.getBoneIndexByName("hardpointB");

                        if (0 <= idx) {
                            float[] buf = new float[16];
                            mmp._skinning_mat[idx].getValue(buf);

                            Matrix4f mat = VectorHelper.matrix4fFromArray(buf);

                            matrixStack.scale(-1, 1, 1);
                            PoseStack.Pose entry = matrixStack.last();
                            entry.pose().mul(mat);
                            matrixStack.scale(-1, 1, 1);
                        }

                        float modelScale = (float) (modelScaleBase * (1.0f / motionScale));
                        matrixStack.scale(modelScale, modelScale, modelScale);

                        renderToolSlashBlade(stack, s, "sheath", matrixStack, bufferIn, lightIn);

                        if (s.isCharged(entity)) {
                            float f = (float) entity.tickCount + partialTicks;
                            BladeRenderState.renderChargeEffect(stack, f, model, "effect",
                                    new ResourceLocation("textures/entity/creeper/creeper_armor.png"), matrixStack,
                                    bufferIn, lightIn);
                        }

                    }

                }

            });

        });
    }

    public void renderToolSlashBlade(ItemStack stack, ISlashBladeState state, String target, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn){
        ToolStack tool = ToolStack.from(stack);

        WavefrontObject model;

        ResourceLocation textureLocation;
        if(tool.getModifierLevel(TicEXRegistry.KOSHIRAE_MODIFIER.get()) > 0){

            CompoundTag persistentTag = stack.getOrCreateTag().getCompound("bladeState");
            if(persistentTag.contains("ModelName")){
               model = BladeModelManager.getInstance().getModel(ResourceLocation.tryParse(persistentTag.getString("ModelName")));
               textureLocation = ResourceLocation.tryParse(persistentTag.getString("TextureName"));
            } else {
                model = BladeModelManager.getInstance().getModel(state.getModel().orElse(DefaultResources.resourceDefaultModel));
                textureLocation = state.getTexture().orElse(DefaultResources.resourceDefaultTexture);
            }

            BladeRenderState.renderOverrided(stack, model, target, textureLocation, matrixStackIn, bufferIn,
                    packedLightIn);
            BladeRenderState.renderOverridedLuminous(stack, model, target + "_luminous", textureLocation,
                    matrixStackIn, bufferIn, packedLightIn);
        } else if(tool.getMaterials().size() > 0){
            model = BladeModelManager.getInstance().getModel(state.getModel().orElse(DefaultResources.resourceDefaultModel));
            SBToolRenderState.renderOverrided(stack, model, target, matrixStackIn, bufferIn, packedLightIn);
            SBToolRenderState.renderOverridedLuminous(stack, model, target, matrixStackIn, bufferIn, packedLightIn);
        } else {
            model = BladeModelManager.getInstance().getModel(state.getModel().orElse(DefaultResources.resourceDefaultModel));
            textureLocation = state.getTexture().orElse(DefaultResources.resourceDefaultTexture);
            BladeRenderState.renderOverrided(stack, model, target, textureLocation, matrixStackIn, bufferIn,
                    packedLightIn);
            BladeRenderState.renderOverridedLuminous(stack, model, target + "_luminous", textureLocation,
                    matrixStackIn, bufferIn, packedLightIn);
        }
    }
}
