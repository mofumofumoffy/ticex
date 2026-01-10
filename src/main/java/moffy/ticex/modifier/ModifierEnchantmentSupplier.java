package moffy.ticex.modifier;

import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ProtectionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.EnchantmentModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.List;
import java.util.Map;

public class ModifierEnchantmentSupplier extends NoLevelsModifier implements MeleeHitModifierHook, EnchantmentModifierHook, ToolDamageModifierHook, MeleeDamageModifierHook, ProtectionModifierHook {

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT, ModifierHooks.ENCHANTMENTS, ModifierHooks.TOOL_DAMAGE, ModifierHooks.MELEE_DAMAGE, ModifierHooks.PROTECTION);
    }

    @Override
    public boolean shouldDisplay(boolean advanced) {
        return false;
    }

    @Override
    public float beforeMeleeHit(IToolStackView iToolStackView, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        return knockback + EnchantmentHelper.getKnockbackBonus(context.getAttacker()) * 0.5f;
    }

    @Override
    public void afterMeleeHit(IToolStackView iToolStackView, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        int fireAspect = EnchantmentHelper.getFireAspect(context.getAttacker());
        context.getTarget().setRemainingFireTicks(80 * fireAspect);
        EnchantmentHelper.doPostDamageEffects(context.getAttacker(), context.getTarget());
        if(context.getTarget() instanceof LivingEntity livingTarget){
            EnchantmentHelper.doPostHurtEffects(livingTarget, context.getAttacker());
        }
    }

    @Override
    public int updateEnchantmentLevel(IToolStackView iToolStackView, ModifierEntry modifierEntry, Enchantment enchantment, int i) {
        int level = i;
        if(iToolStackView instanceof ToolStack tool){
            ItemStack toolStack = tool.createStack();
            level += EnchantmentHelper.getTagEnchantmentLevel(enchantment, toolStack);
        }
        return level;
    }

    @Override
    public void updateEnchantments(IToolStackView iToolStackView, ModifierEntry modifierEntry, Map<Enchantment, Integer> map) {
        if(iToolStackView instanceof ToolStack tool){
            ItemStack toolStack = tool.createStack();
            map.putAll(EnchantmentHelper.getEnchantments(toolStack));
        }
    }

    @Override
    public int onDamageTool(IToolStackView iToolStackView, ModifierEntry modifierEntry, int damage, @Nullable LivingEntity livingEntity) {
        if(iToolStackView instanceof  ToolStack tool){
            ItemStack toolStack = tool.createStack();
            int lvl = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.UNBREAKING, toolStack);
            int actual = 0;
            if(livingEntity != null){
                RandomSource rand = livingEntity.getRandom();
                for (int i = 0; i < damage; i++) {
                    if (rand.nextInt(lvl + 1) == 0) actual++;
                }
            }
            return actual;
        }
        return damage;
    }

    @Override
    public float getProtectionModifier(IToolStackView iToolStackView, ModifierEntry modifierEntry, EquipmentContext equipmentContext, EquipmentSlot equipmentSlot, DamageSource damageSource, float v) {
        if(iToolStackView instanceof  ToolStack tool){
            ItemStack toolStack = tool.createStack();
            return v + EnchantmentHelper.getDamageProtection(List.of(toolStack), damageSource);
        }
        return v;
    }

    @Override
    public float getMeleeDamage(IToolStackView iToolStackView, ModifierEntry modifierEntry, ToolAttackContext toolAttackContext, float v, float v1) {
        if(iToolStackView instanceof  ToolStack tool && toolAttackContext.getTarget() instanceof Mob mob) {
            ItemStack toolStack = tool.createStack();
            return v1 + EnchantmentHelper.getDamageBonus(toolStack, mob.getMobType());
        }
        return v1;
    }
}
