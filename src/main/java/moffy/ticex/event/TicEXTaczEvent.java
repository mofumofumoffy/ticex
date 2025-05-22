package moffy.ticex.event;
/*
 * This file is part of the TicEXTaczModule.
 *
 * Licensed under the GNU General Public License v3.0.
 * See the LICENSES/GPL-3.0.md file for details.
 * 2025 Moffy
*/

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.GunMeleeEvent;

import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class TicEXTaczEvent {
    public static void onBeforeHit(EntityHurtByGunEvent.Pre event){
        LivingEntity attacker = event.getAttacker();
        Entity target = event.getHurtEntity();
        ItemStack mainHandStack = attacker.getMainHandItem();
        if(mainHandStack != null && mainHandStack.getItem() instanceof IModifiable){
            ToolStack tool = ToolStack.from(mainHandStack);
            float damage = event.getBaseAmount();
            float damageTmp = damage;

            ToolAttackContext context = new ToolAttackContext(attacker, attacker instanceof Player ? (Player)attacker : null, InteractionHand.MAIN_HAND, target, target instanceof LivingEntity ? (LivingEntity)target : null, event.isHeadShot(), 0, false);

            /* int lostStability = 10;
            for(ModifierEntry modifier : tool.getModifierList()){
                lostStability = modifier.getHook(ModifierHooks.TOOL_DAMAGE).onDamageTool(tool, modifier, lostStability, attacker);
            }

            tool.setDamage(tool.getDamage() + lostStability); */

            if(!mainHandStack.is(TicEXRegistry.KEY_MODIFIER_UNSTABLE) || !tool.isBroken()){
                for(ModifierEntry modifier : tool.getModifierList()){
                    damage = modifier.getHook(ModifierHooks.MELEE_DAMAGE).getMeleeDamage(tool, modifier, context, damageTmp, damage);
                }
    
                event.setBaseAmount(damage);
                
                for(ModifierEntry modifier : tool.getModifierList()){
                    modifier.getHook(ModifierHooks.MELEE_HIT).beforeMeleeHit(tool, modifier, context, event.getBaseAmount(), 0, 0);
                }
            }
        }
    }

    public static void onAfterHit(EntityHurtByGunEvent.Post event){
        LivingEntity attacker = event.getAttacker();
        Entity target = event.getHurtEntity();
        ItemStack mainHandStack = attacker.getMainHandItem();
        if(mainHandStack != null && target != null && mainHandStack.getItem() instanceof IModifiable){
            ToolStack tool = ToolStack.from(mainHandStack);
            ToolAttackContext context = new ToolAttackContext(attacker, attacker instanceof Player ? (Player)attacker : null, InteractionHand.MAIN_HAND, target, target instanceof LivingEntity ? (LivingEntity)target : null, event.isHeadShot(), 0, false);
            if(!tool.isBroken()){
                for(ModifierEntry modifier : tool.getModifierList()){
                    modifier.getHook(ModifierHooks.MELEE_HIT).afterMeleeHit(tool, modifier, context, event.getAmount());
                }
            }
        }
    }

    public static void onMelee(GunMeleeEvent event){
        LivingEntity attacker = event.getShooter();
        Entity target = attacker.level().getNearestEntity(LivingEntity.class, TargetingConditions.DEFAULT, attacker, 0, 0, 0, getAABB(attacker));
        ItemStack gunStack = event.getGunItemStack();
        if(gunStack != null && target != null && gunStack.getItem() instanceof IModifiable){
            ToolStack tool = ToolStack.from(gunStack);
            ToolAttackContext context = new ToolAttackContext(attacker, attacker instanceof Player ? (Player)attacker : null, InteractionHand.MAIN_HAND, target, target instanceof LivingEntity ? (LivingEntity)target : null, false, 0, false);
            if(!tool.isBroken()){
                for(ModifierEntry modifier : tool.getModifierList()){
                    modifier.getHook(ModifierHooks.MELEE_HIT).beforeMeleeHit(tool, modifier, context, 3, 0, 0);
                }

                for(ModifierEntry modifier : tool.getModifierList()){
                    modifier.getHook(ModifierHooks.MELEE_HIT).afterMeleeHit(tool, modifier, context, 3);
                }
            }
        }
    }

    public static  AABB getAABB(LivingEntity attacker){
        return attacker.getBoundingBox().inflate(5);
    }
}
