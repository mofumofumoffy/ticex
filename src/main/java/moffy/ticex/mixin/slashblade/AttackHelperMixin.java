package moffy.ticex.mixin.slashblade;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import mods.flammpfeil.slashblade.util.AttackHelper;
import moffy.ticex.lib.AttackContextHolder;
import moffy.ticex.lib.hook.CriticalModifierHook;
import moffy.ticex.lib.hook.DamageSourceModifierHook;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@Mixin(value = AttackHelper.class, remap = false)
public abstract class AttackHelperMixin {

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lmods/flammpfeil/slashblade/util/AttackHelper;calculateTotalDamage(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;FZ)D"))
    private static void setContext(LivingEntity attacker, Entity target, float comboRatio, CallbackInfo ci,
                                   @Share(value = "contextHolder") LocalRef<AttackContextHolder> contextHolderRef) {
        ToolAttackContext context = ToolAttackContext.attacker(attacker)
                .hand(InteractionHand.MAIN_HAND)
                .target(target)
                .cooldown(1)
                .build();
        ItemStack blade = ticex_1_20_1$resolveItemInHand(attacker, context.getHand());
        contextHolderRef.set(new AttackContextHolder(blade, context));
    }

    @ModifyExpressionValue(
            method = "calculateTotalDamage",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/entity/player/CriticalHitEvent;getDamageModifier()F")
    )
    private static float modifyToolCritModifier(
            float original,
            @Local(name = "isCritical") boolean isCritical,
            @Local(argsOnly = true) LivingEntity attacker,
            @Local(argsOnly = true) Entity target
    ){
        if(attacker instanceof Player playerAttacker) {
            CriticalModifierHook.CriticalContext criticalContext = CriticalModifierHook.modifyCritical(playerAttacker, target, isCritical, original);
            return criticalContext.criticalModifier();
        }
        return original;
    }

    @ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lmods/flammpfeil/slashblade/util/AttackHelper;calculateTotalDamage(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;FZ)D"))
    private static double applyAttackDamage(double damageAmount,
                                            @Local(argsOnly = true) LivingEntity attacker,
                                            @Share(value = "contextHolder") LocalRef<AttackContextHolder> contextHolderRef) {
        AttackContextHolder contextHolder = contextHolderRef.get();
        ToolAttackContext context = contextHolder.context();
        ToolStack tool = contextHolder.getTool();
        if(tool != null) {
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
                                        @Share(value = "contextHolder") LocalRef<AttackContextHolder> contextHolderRef) {
        AttackContextHolder contextHolder = contextHolderRef.get();
        ToolAttackContext context = contextHolder.context();
        ToolStack tool = contextHolder.getTool();
        if(tool != null) {
            float originalKnockback = knockback;
            for(ModifierEntry entry : tool.getModifiers()){
                knockback = entry.getHook(ModifierHooks.MELEE_HIT).beforeMeleeHit(tool, entry, context, (float) baseDamage, originalKnockback, knockback);
            }
        }

        return knockback;
    }

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", remap = true))
    private static boolean hurtWithModifiedDamageSource(Entity instance,
                                                        DamageSource pSource,
                                                        float pAmount,
                                                        Operation<Boolean> original,
                                                        @Local(argsOnly = true) LivingEntity attacker,
                                                        @Share(value = "contextHolder") LocalRef<AttackContextHolder> contextHolderRef){
        AttackContextHolder contextHolder = contextHolderRef.get();
        ToolAttackContext context = contextHolder.context();
        ToolStack tool = contextHolder.getTool();
        if(tool != null) {
            return original.call(instance, DamageSourceModifierHook.modifyDamageSource(attacker, context, pSource), pAmount);
        }
        return original.call(instance, pSource, pAmount);
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lmods/flammpfeil/slashblade/util/AttackHelper;handlePostAttackEffects(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;Lmods/flammpfeil/slashblade/util/AttackHelper$FireAspectResult;)V", shift = At.Shift.AFTER))
    private static void applyAttackSuccess(LivingEntity attacker, Entity target, float comboRatio, CallbackInfo ci,
                                          @Local double baseDamage,
                                           @Share(value = "contextHolder") LocalRef<AttackContextHolder> contextHolderRef
                                          ) {
        AttackContextHolder contextHolder = contextHolderRef.get();
        ToolAttackContext context = contextHolder.context();
        ToolStack tool = contextHolder.getTool();
        if(tool != null){
            for(ModifierEntry entry : tool.getModifiers()){
                entry.getHook(ModifierHooks.MELEE_HIT).afterMeleeHit(tool, entry, context, (float) baseDamage);
            }
        }
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lmods/flammpfeil/slashblade/util/AttackHelper;handleFailedAttack(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;Lmods/flammpfeil/slashblade/util/AttackHelper$FireAspectResult;)V", shift = At.Shift.AFTER))
    private static void applyAttackFailed(LivingEntity attacker, Entity target, float comboRatio, CallbackInfo ci,
                                          @Local double baseDamage,
                                          @Share(value = "contextHolder") LocalRef<AttackContextHolder> contextHolderRef) {
        AttackContextHolder contextHolder = contextHolderRef.get();
        ToolAttackContext context = contextHolder.context();
        ToolStack tool = contextHolder.getTool();

        if(tool != null){
            for(ModifierEntry entry : tool.getModifiers()){
                entry.getHook(ModifierHooks.MELEE_HIT).failedMeleeHit(tool, entry, context, (float) baseDamage);
            }
        }
    }

    //This roundabout method is provided because some dual wielding mods apply a mixin to LivingEntity.getMainHandItem().
    @Unique
    private static ItemStack ticex_1_20_1$resolveItemInHand(LivingEntity attacker, InteractionHand hand){
        return hand == InteractionHand.MAIN_HAND ? attacker.getMainHandItem() : attacker.getOffhandItem();
    }
}
