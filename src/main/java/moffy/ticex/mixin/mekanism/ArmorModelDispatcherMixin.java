package moffy.ticex.mixin.mekanism;

import moffy.ticex.client.modules.mekanism.MekaPlateMultilayerModel;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.client.armor.ArmorModelManager;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@Mixin(value = ArmorModelManager.ArmorModelDispatcher.class, remap = false)
public abstract class ArmorModelDispatcherMixin {
    @Shadow
    protected abstract ArmorModelManager.ArmorModel getModel(ItemStack stack);

    @Inject(
            at = @At("HEAD"),
            method = "getGenericArmorModel",
            cancellable = true
    )
    public void getGenericArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original, CallbackInfoReturnable<Model> cir) {
        if(stack.getItem() instanceof IModifiable){
            ToolStack toolStack = ToolStack.from(stack);
            if(toolStack.getModifierLevel(TicEXRegistry.MEKANIC_MODIFIER.get()) > 0){
                cir.setReturnValue(switch (slot) {
                    case HEAD -> MekaPlateMultilayerModel.HEAD.setup(living, stack, slot, original, this.getModel(stack));
                    case CHEST ->
                            MekaPlateMultilayerModel.CHESTPLATE.setup(living, stack, slot, original, this.getModel(stack));
                    case LEGS -> MekaPlateMultilayerModel.LEGGINGS.setup(living, stack, slot, original, this.getModel(stack));
                    case FEET -> MekaPlateMultilayerModel.BOOTS.setup(living, stack, slot, original, this.getModel(stack));
                    default -> cir.getReturnValue();
                });
            }
        }
    }
}
