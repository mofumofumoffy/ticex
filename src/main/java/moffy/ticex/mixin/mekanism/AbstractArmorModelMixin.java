package moffy.ticex.mixin.mekanism;

import moffy.ticex.lib.modules.mekanism.MekaGearCapability;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.client.armor.AbstractArmorModel;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.data.ModifierIds;

@Mixin(value = AbstractArmorModel.class, remap = false)
public class AbstractArmorModelMixin {
    @Shadow
    protected boolean hasWings;

    @Inject(
            at = @At("TAIL"),
            method = "setup"
    )
    protected void setup(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> base, CallbackInfo ci) {
        if(stack.getItem() instanceof IModifiable){
            ToolStack toolStack = ToolStack.from(stack);
            if(toolStack.getModifierLevel(ModifierIds.wings) < 1 && stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent()){
                hasWings = false;
            }
        }
    }
}
