package moffy.ticex.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import moffy.ticex.lib.hook.CriticalModifierHook;
import moffy.ticex.lib.utils.TicEXUtils;
import moffy.ticex.registry.TicEXModifierHooks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;

@Mixin(value = ToolAttackUtil.class, remap = false)
public class ToolAttackUtilMixin {
    @ModifyExpressionValue(
            method = "performAttack",
            at = @At(value = "INVOKE", target = "Lslimeknights/tconstruct/library/tools/context/ToolAttackContext;getBaseKnockback()F")
    )
    private static float performArmorMeleeHitBefore(float original, @Local(argsOnly = true)ToolAttackContext context, @Local(name = "damage") float damage){
        float newKnockback = original;

        LivingEntity attacker = context.getAttacker();
        for(EquipmentSlot slot : TicEXUtils.EquipmentSlotLists.ARMORS){
            ItemStack stack = attacker.getItemBySlot(slot);

            if(stack.getItem() instanceof IModifiable){
                ToolStack tool = ToolStack.from(stack);
                for(ModifierEntry entry : tool.getModifierList()){
                    newKnockback = entry.getHook(TicEXModifierHooks.ARMOR_MELEE_HIT).beforeMeleeHit(tool, entry, context, damage, original, newKnockback);
                }
            }
        }

        return newKnockback;
    }

    @ModifyExpressionValue(
            method = "performAttack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z")
    )
    private static boolean performArmorMeleeHitAfter(boolean original, @Local(argsOnly = true)ToolAttackContext context, @Local(name = "damage") float damage){
        LivingEntity attacker = context.getAttacker();
        for(EquipmentSlot slot : TicEXUtils.EquipmentSlotLists.ARMORS){
            ItemStack stack = attacker.getItemBySlot(slot);

            if(stack.getItem() instanceof IModifiable){
                ToolStack tool = ToolStack.from(stack);
                for(ModifierEntry entry : tool.getModifierList()){
                    if(original){
                        entry.getHook(TicEXModifierHooks.ARMOR_MELEE_HIT).afterMeleeHit(tool, entry, context, damage);
                    } else {
                        entry.getHook(TicEXModifierHooks.ARMOR_MELEE_HIT).failedMeleeHit(tool, entry, context, damage);
                    }
                }
            }
        }
        return original;
    }

    @WrapOperation(
            method = "getCriticalModifier",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/ForgeHooks;getCriticalHit(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;ZF)Lnet/minecraftforge/event/entity/player/CriticalHitEvent;")
    )
    private static CriticalHitEvent modifyCriticalHit(
            Player player, Entity target, boolean vanillaCritical, float damageModifier, Operation<CriticalHitEvent> original, @Local(name = "livingTarget") @Nullable LivingEntity livingTarget, @Local(name = "isCritical") boolean isCritical, @Local(name = "criticalModifier") float criticalModifier
    ){
        CriticalModifierHook.CriticalContext context = CriticalModifierHook.modifyCritical(player, target, isCritical, criticalModifier);
        return original.call(player, target, context.isCritical(), context.isCritical() ? 1.0f : context.criticalModifier());
    }
}
