package moffy.ticex.client.slashblade;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import mods.flammpfeil.slashblade.client.renderer.entity.BladeItemEntityRenderer;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.util.BladeRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.SwordType;
import moffy.ticex.entity.slashblade.SBToolItemEntity;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class SBToolBladeItemRenderer extends BladeItemEntityRenderer{

    public SBToolBladeItemRenderer(Context context) {
        super(context);
    }

    @Override
    public void render(ItemEntity itemIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
      this.shadowRadius = 0.0F;
      if (!itemIn.getItem().isEmpty()) {
         this.renderBlade(itemIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
      } else {
         partialTicks = (float)((double)itemIn.bobOffs * 20.0 - (double)itemIn.getAge());
         super.render(itemIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
      }

   }

    private void renderBlade(ItemEntity itemIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
      if (itemIn instanceof SBToolItemEntity bladeItem) {
         MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStackIn);

         try {
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(entityYaw));
            ItemStack current = itemIn.getItem();
            EnumSet<SwordType> types = SwordType.from(current);
            itemIn.getPersistentData();
            Optional<ResourceLocation> modelLoc = current.getCapability(ItemSlashBlade.BLADESTATE).map((state) -> {
               Optional<ResourceLocation> rl = state.getModel();
               Objects.requireNonNull(bladeItem);
               return (ResourceLocation)rl.orElseGet(bladeItem::getModel);
            });
            Objects.requireNonNull(bladeItem);
            ResourceLocation modelLocation = (ResourceLocation)modelLoc.orElseGet(bladeItem::getModel);
            Optional<ResourceLocation>textureLoc = current.getCapability(ItemSlashBlade.BLADESTATE).map((state) -> {
               Optional<ResourceLocation> rl = state.getTexture();
               Objects.requireNonNull(bladeItem);
               return (ResourceLocation)rl.orElseGet(bladeItem::getTexture);
            });
            Objects.requireNonNull(bladeItem);
            ResourceLocation textureLocation = (ResourceLocation)textureLoc.orElseGet(bladeItem::getTexture);
            WavefrontObject model = BladeModelManager.getInstance().getModel(modelLocation);
            float scale = 0.00625F;
            MSAutoCloser msac2 = MSAutoCloser.pushMatrix(matrixStackIn);

            try {
               float xOffset = 0.0F;
               float heightOffset;
               String renderTarget;
               if (types.contains(SwordType.EDGEFRAGMENT)) {
                  heightOffset = 225.0F;
                  xOffset = 200.0F;
                  renderTarget = "blade_fragment";
               } else if (types.contains(SwordType.BROKEN)) {
                  heightOffset = 100.0F;
                  xOffset = 30.0F;
                  renderTarget = "blade_damaged";
               } else {
                  heightOffset = 225.0F;
                  xOffset = 120.0F;
                  renderTarget = "blade";
               }

               if (itemIn.isInWater()) {
                  matrixStackIn.translate(0.0F, 0.025F, 0.0F);
                  matrixStackIn.mulPose(Axis.YP.rotationDegrees(itemIn.bobOffs));
                  matrixStackIn.scale(scale, scale, scale);
                  matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F));
               } else if (!itemIn.onGround()) {
                  matrixStackIn.scale(scale, scale, scale);
                  float speed = -81.0F;
                  matrixStackIn.mulPose(Axis.ZP.rotationDegrees(speed * ((float)itemIn.tickCount + partialTicks)));
                  matrixStackIn.translate(xOffset, 0.0F, 0.0F);
               } else {
                  matrixStackIn.scale(scale, scale, scale);
                  matrixStackIn.mulPose(Axis.ZP.rotationDegrees(60.0F + (float)Math.toDegrees((double)itemIn.bobOffs / 6.0)));
                  matrixStackIn.translate(heightOffset, 0.0F, 0.0F);
               }

               renderToolSlashBlade(current, model, textureLocation, renderTarget, matrixStackIn, bufferIn, packedLightIn);
            } catch (Throwable var23) {
               if (msac2 != null) {
                  try {
                     msac2.close();
                  } catch (Throwable var21) {
                     var23.addSuppressed(var21);
                  }
               }

               throw var23;
            }

            if (msac2 != null) {
               msac2.close();
            }

            if (itemIn.isInWater() || itemIn.onGround() && !types.contains(SwordType.NOSCABBARD)) {
               msac2 = MSAutoCloser.pushMatrix(matrixStackIn);

               try {
                  matrixStackIn.translate(0.0F, 0.025F, 0.0F);
                  matrixStackIn.mulPose(Axis.YP.rotationDegrees(itemIn.bobOffs));
                  if (!itemIn.isInWater()) {
                     matrixStackIn.translate(0.75, 0.0, -0.4);
                  }

                  matrixStackIn.scale(scale, scale, scale);
                  matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F));
                  String renderTarget = "sheath";
                  renderToolSlashBlade(current, model, textureLocation, renderTarget, matrixStackIn, bufferIn, packedLightIn);
               } catch (Throwable var24) {
                  if (msac2 != null) {
                     try {
                        msac2.close();
                     } catch (Throwable var22) {
                        var24.addSuppressed(var22);
                     }
                  }

                  throw var24;
               }

               if (msac2 != null) {
                  msac2.close();
               }
            }
         } catch (Throwable var25) {
            if (msac != null) {
               try {
                  msac.close();
               } catch (Throwable var20) {
                  var25.addSuppressed(var20);
               }
            }

            throw var25;
         }

         if (msac != null) {
            msac.close();
         }
      }

   }

   public void renderToolSlashBlade(ItemStack stack, WavefrontObject model, ResourceLocation textureLocation, String target, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn){
        ToolStack tool = ToolStack.from(stack);


        if(tool.getModifierLevel(TicEXRegistry.KOSHIRAE_MODIFIER.get()) > 0){
            CompoundTag persistentTag = stack.getOrCreateTag().getCompound("bladeState");
            if(persistentTag.contains("ModelName")){
               model = BladeModelManager.getInstance().getModel(ResourceLocation.tryParse(persistentTag.getString("ModelName")));
               textureLocation = ResourceLocation.tryParse(persistentTag.getString("TextureName"));
            }
            BladeRenderState.renderOverrided(stack, model, target, textureLocation, matrixStackIn, bufferIn,
                    packedLightIn);
            BladeRenderState.renderOverridedLuminous(stack, model, target + "_luminous", textureLocation,
                    matrixStackIn, bufferIn, packedLightIn);
        } else if(tool.getMaterials().size() > 0){
            SBToolRenderState.renderOverrided(stack, model, target, matrixStackIn, bufferIn, packedLightIn);
            SBToolRenderState.renderOverridedLuminous(stack, model, target, matrixStackIn, bufferIn, packedLightIn);
        } else {
            BladeRenderState.renderOverrided(stack, model, target, textureLocation, matrixStackIn, bufferIn,
                    packedLightIn);
            BladeRenderState.renderOverridedLuminous(stack, model, target + "_luminous", textureLocation,
                    matrixStackIn, bufferIn, packedLightIn);
        }
    }
}
