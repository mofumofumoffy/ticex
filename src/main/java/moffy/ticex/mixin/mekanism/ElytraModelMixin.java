package moffy.ticex.mixin.mekanism;

import mekanism.common.registries.MekanismModules;
import moffy.ticex.lib.modules.mekanism.MekaGearCapability;
import moffy.ticex.lib.modules.mekanism.interfaces.IMekaGear;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ElytraModel.class)
public class ElytraModelMixin<T extends LivingEntity> {
    @Shadow
    @Final
    private ModelPart rightWing;

    @Shadow
    @Final
    private ModelPart leftWing;
    @Inject(
            at = @At("TAIL"),
            method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V"
    )
    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch, CallbackInfo ci) {
        if(ticex_1_20_1$isElytraModuleEnabled(pEntity) && pEntity.isFallFlying()){
            float yScale = (float) 0.15;
            float zScale = (float) -0.5;
            leftWing.yRot *= yScale;
            rightWing.yRot *= yScale;
            leftWing.zRot *= zScale;
            rightWing.zRot *= zScale;
        }
    }

    @Unique
    private boolean ticex_1_20_1$isElytraModuleEnabled(T pEntity){
        ItemStack chestStack = pEntity.getItemBySlot(EquipmentSlot.CHEST);
        if(chestStack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent()){
            IMekaGear mekaGear = chestStack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).orElseThrow(IllegalStateException::new);
            return mekaGear.isModuleEnabled(chestStack, MekanismModules.ELYTRA_UNIT);
        }
        return false;
    }
}
