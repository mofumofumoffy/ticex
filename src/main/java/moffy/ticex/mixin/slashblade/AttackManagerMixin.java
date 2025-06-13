package moffy.ticex.mixin.slashblade;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mods.flammpfeil.slashblade.entity.EntityAbstractSummonedSword;
import mods.flammpfeil.slashblade.util.AttackManager;
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
            if(mainHandStack != null && mainHandStack.getItem() instanceof IModifiable){
                ToolStack tool = ToolStack.from(mainHandStack);
                ToolAttackContext context = new ToolAttackContext(livingAttacker, livingAttacker instanceof Player player ? player : null, InteractionHand.MAIN_HAND, target, target instanceof LivingEntity livingTarget ? livingTarget : null, false, 0, false);

                dealToolDamage(tool, context, livingAttacker, src, amount, target, forceHit, resetHit);

                cb.cancel();
            }
        }
    }

    @Unique
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
