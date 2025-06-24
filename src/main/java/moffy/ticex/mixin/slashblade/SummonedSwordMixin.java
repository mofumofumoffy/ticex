package moffy.ticex.mixin.slashblade;

import mods.flammpfeil.slashblade.entity.EntityAbstractSummonedSword;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@Mixin(value = EntityAbstractSummonedSword.class)
public abstract class SummonedSwordMixin {

    @Shadow
    public abstract Entity getShooter();

    @Inject(at = @At("head"), method = "onHitEntity", cancellable = true)
    protected void onHitEntity(EntityHitResult entityHitResult, CallbackInfo cb) {
        Entity shooter = getShooter();
        Entity target = entityHitResult.getEntity();
        if (shooter instanceof LivingEntity livingShooter) {
            ItemStack mainHandStack = livingShooter.getMainHandItem();
            if (mainHandStack.getItem() instanceof IModifiable) {
                ToolStack tool = ToolStack.from(mainHandStack);

                ModifierNBT modifiers = tool.getModifiers();
                ModDataNBT persistentData = tool.getPersistentData();
                for (ModifierEntry modifier : tool.getModifierList()) {
                    boolean cancel = modifier
                        .getHook(ModifierHooks.PROJECTILE_HIT)
                        .onProjectileHitEntity(
                            modifiers,
                            persistentData,
                            modifier,
                            ((EntityAbstractSummonedSword) ((Object) this)),
                            entityHitResult,
                            livingShooter,
                            target instanceof LivingEntity livingTarget ? livingTarget : null
                        );
                    if (cancel) {
                        cb.cancel();
                    }
                }
            }
        }
    }
}
