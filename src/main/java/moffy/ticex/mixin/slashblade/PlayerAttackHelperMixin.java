package moffy.ticex.mixin.slashblade;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import mods.flammpfeil.slashblade.util.AttackHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@Mixin(value = AttackHelper.class, remap = false)
public abstract class PlayerAttackHelperMixin {

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lmods/flammpfeil/slashblade/util/AttackHelper;calculateTotalDamage(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;FZ)D"))
    private static void setContext(LivingEntity attacker, Entity target, float comboRatio, CallbackInfo ci,
                                   @Local boolean isCritical,
                                   @Share(value = "context") LocalRef<ToolAttackContext> contextRef) {
        contextRef.set(new ToolAttackContext(
                attacker, attacker instanceof Player player ? player : null,
                InteractionHand.MAIN_HAND,
                target, target instanceof LivingEntity livingEntity ? livingEntity : null,
                isCritical,
                1,
                false
        ));
    }

    @ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lmods/flammpfeil/slashblade/util/AttackHelper;calculateTotalDamage(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;FZ)D"))
    private static double applyAttackDamage(double damageAmount,
                                            @Local(argsOnly = true) LivingEntity attacker,
                                            @Share(value = "context") LocalRef<ToolAttackContext> contextRef) {
        ToolAttackContext context = contextRef.get();
        ItemStack stack = attacker.getItemInHand(context.getHand());
        if(stack.getItem() instanceof IModifiable) {
            ToolStack tool = ToolStack.from(stack);

            double originalDamage = damageAmount;

            for(ModifierEntry entry : tool.getModifiers()){
                damageAmount = entry.getHook(ModifierHooks.MELEE_DAMAGE).getMeleeDamage(tool, entry, context, (float) originalDamage, (float) damageAmount);
            }
        }

        return damageAmount;
    }

    @ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lmods/flammpfeil/slashblade/util/AttackHelper;calculateKnockback(Lnet/minecraft/world/entity/LivingEntity;)F"))
    private static float applyKnockback(float knockback,
                                        @Local double baseDamage,
                                        @Local(argsOnly = true) LivingEntity attacker,
                                        @Share(value = "context") LocalRef<ToolAttackContext> contextRef) {
        ToolAttackContext context = contextRef.get();
        ItemStack stack = attacker.getItemInHand(context.getHand());
        if(stack.getItem() instanceof IModifiable) {
            ToolStack tool = ToolStack.from(stack);

            float originalKnockback = knockback;
            for(ModifierEntry entry : tool.getModifiers()){
                knockback = entry.getHook(ModifierHooks.MELEE_HIT).beforeMeleeHit(tool, entry, context, (float) baseDamage, originalKnockback, knockback);
            }
        }

        return knockback;
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lmods/flammpfeil/slashblade/util/AttackHelper;handlePostAttackEffects(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;Lmods/flammpfeil/slashblade/util/AttackHelper$FireAspectResult;)V", shift = At.Shift.AFTER))
    private static void applyAttackSuccess(LivingEntity attacker, Entity target, float comboRatio, CallbackInfo ci,
                                          @Local double baseDamage,
                                          @Share(value = "context") LocalRef<ToolAttackContext> contextRef) {
        ToolAttackContext context = contextRef.get();
        ItemStack stack = attacker.getItemInHand(context.getHand());
        if(stack.getItem() instanceof IModifiable){
            ToolStack tool = ToolStack.from(stack);

            for(ModifierEntry entry : tool.getModifiers()){
                entry.getHook(ModifierHooks.MELEE_HIT).afterMeleeHit(tool, entry, context, (float) baseDamage);
            }
        }
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lmods/flammpfeil/slashblade/util/AttackHelper;handleFailedAttack(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;Lmods/flammpfeil/slashblade/util/AttackHelper$FireAspectResult;)V", shift = At.Shift.AFTER))
    private static void applyAttackFailed(LivingEntity attacker, Entity target, float comboRatio, CallbackInfo ci,
                                          @Local double baseDamage,
                                          @Share(value = "context") LocalRef<ToolAttackContext> contextRef) {
        ToolAttackContext context = contextRef.get();
        ItemStack stack = attacker.getItemInHand(context.getHand());

        if(stack.getItem() instanceof IModifiable){
            ToolStack tool = ToolStack.from(stack);

            for(ModifierEntry entry : tool.getModifiers()){
                entry.getHook(ModifierHooks.MELEE_HIT).failedMeleeHit(tool, entry, context, (float) baseDamage);
            }
        }
    }
}
