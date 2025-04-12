package moffy.ticex.mixin.slashblade;

import java.util.List;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.Lists;

import mods.flammpfeil.slashblade.entity.EntityAbstractSummonedSword;
import mods.flammpfeil.slashblade.entity.IShootable;
import mods.flammpfeil.slashblade.util.AttackManager;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@Mixin(value = AttackManager.class, remap = false)
public class AttackManagerMixin {

    @Inject(
        at = @At("HEAD"),
        method = "areaAttack(Lnet/minecraft/world/entity/Entity;Ljava/util/function/Consumer;DZZFLjava/util/List;)Ljava/util/List;",
        cancellable = true
    )
    private static <E extends Entity & IShootable> void areaAttack(E owner, Consumer<LivingEntity> beforeHit, double reach, boolean forceHit, boolean resetHit, float comboRatio, List<Entity> exclude, CallbackInfoReturnable<List<Entity>> cb){
        if(owner.getShooter() instanceof LivingEntity livingAttacker){
            ItemStack mainHandStack = livingAttacker.getMainHandItem();
            if(mainHandStack.getItem() instanceof IModifiable){
                List<Entity> founds = Lists.newArrayList();

                if (!owner.level().isClientSide()) {
                    founds = TargetSelector.getTargettableEntitiesWithinAABB(owner.level(), reach, owner);

                    float baseAmount = (float) owner.getDamage();
                    ToolStack tool = ToolStack.from(mainHandStack);
        
                    for (Entity entity : founds) {
        
                        if (entity instanceof LivingEntity living)
                            beforeHit.accept(living);

                        ToolAttackContext context = new ToolAttackContext(livingAttacker, livingAttacker instanceof Player player ? player : null, InteractionHand.MAIN_HAND, entity, entity instanceof LivingEntity livingTarget ? livingTarget : null, false, 0, false);

                        dealToolDamage(tool, context, livingAttacker, owner.damageSources().indirectMagic(owner, owner.getShooter()), baseAmount, owner, forceHit, resetHit);
                    }
                }

                cb.setReturnValue(founds);
            }
        }
    }

    @Inject(
        at = @At("head"),
        method = "doAttackWith",
        cancellable = true
    )
    private static void doAttackWith(DamageSource src, float amount, Entity target, boolean forceHit, boolean resetHit, CallbackInfo cb) {
        if (target instanceof EntityAbstractSummonedSword)
            return;
        
        Entity attacker = src.getEntity();
        if(attacker instanceof LivingEntity livingAttacker){
            ItemStack mainHandStack = livingAttacker.getMainHandItem();
            if(mainHandStack.getItem() instanceof IModifiable){
                ToolStack tool = ToolStack.from(mainHandStack);
                ToolAttackContext context = new ToolAttackContext(livingAttacker, livingAttacker instanceof Player player ? player : null, InteractionHand.MAIN_HAND, target, target instanceof LivingEntity livingTarget ? livingTarget : null, false, 0, false);

                dealToolDamage(tool, context, livingAttacker, src, amount, target, forceHit, resetHit);

                cb.cancel();
            }
        }
    }

    private static void dealToolDamage(IToolStackView tool, ToolAttackContext context, LivingEntity livingAttacker, DamageSource src, float amount, Entity target, boolean forceHit, boolean resetHit){
        float amplifier = ToolAttackUtil.getAttributeAttackDamage(tool, livingAttacker, EquipmentSlot.MAINHAND);

        float amplifierTmp = amplifier;
        
        for(ModifierEntry modifier : tool.getModifierList()){
            amplifier = modifier.getHook(ModifierHooks.MELEE_DAMAGE).getMeleeDamage(tool, modifier, context, amplifierTmp, amplifier);
        }

        if(amplifier <= 0){
            return;
        }

        amount = amount / (float)livingAttacker.getAttributeValue(Attributes.ATTACK_DAMAGE) * amplifier;

        if (forceHit)target.invulnerableTime = 0;

        for(ModifierEntry modifier : tool.getModifierList()){
            modifier.getHook(ModifierHooks.MELEE_HIT).beforeMeleeHit(tool, modifier, context, amount, 0, 0);
        }

        boolean succeed = target.hurt(src, amount);

        if (resetHit)target.invulnerableTime = 0;

        if (succeed){
            for(ModifierEntry modifier : tool.getModifierList()){
                modifier.getHook(ModifierHooks.MELEE_HIT).afterMeleeHit(tool, modifier, context, amplifierTmp);;
            }
        } else {
            for(ModifierEntry modifier : tool.getModifierList()){
                modifier.getHook(ModifierHooks.MELEE_HIT).failedMeleeHit(tool, modifier, context, amplifierTmp);
            }
        }
    }
}
