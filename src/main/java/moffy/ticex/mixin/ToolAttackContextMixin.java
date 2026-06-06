package moffy.ticex.mixin;

import moffy.ticex.lib.hook.DamageSourceModifierHook;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@Mixin(value = ToolAttackContext.class, remap = false)
public class ToolAttackContextMixin {
    @Shadow
    @Final
    private LivingEntity attacker;

    @Shadow
    @Final
    private EquipmentSlot slotType;

    @Inject(
            method = "makeDamageSource",
            at = @At("RETURN"),
            cancellable = true
    )
    public void modifyDamageSource(CallbackInfoReturnable<DamageSource> cir){
        ItemStack stack = attacker.getItemBySlot(slotType);
        if(stack.getItem() instanceof IModifiable){
            cir.setReturnValue(DamageSourceModifierHook.modifyDamageSource(ToolStack.from(stack), cir.getReturnValue()));
        }
    }

}
