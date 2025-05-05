package moffy.ticex.modifier;

import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

public class ModifierDeflection extends Modifier implements MeleeDamageModifierHook, ProjectileHitModifierHook{

    @Override
    public int getPriority() {
        return 1000;
    }

    @Override
    protected void registerHooks(Builder hookBuilder) {
        super.registerHooks(hookBuilder);
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE, ModifierHooks.PROJECTILE_HIT);
    }

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifierEntry, ToolAttackContext context, float baseDamage,
            float damage) {
        if(!context.isExtraAttack() && tool.getModifierLevel(TicEXRegistry.DEFINE_MODIFIER.get()) <= 0){
            for(ModifierEntry toolEntry:tool.getModifierList()){
                toolEntry.getHook(ModifierHooks.MELEE_HIT).beforeMeleeHit(tool, modifierEntry, context, damage * (2f + modifierEntry.getLevel() * 0.25f), baseDamage, damage);
            }
            for(ModifierEntry toolEntry:tool.getModifierList()){
                toolEntry.getHook(ModifierHooks.MELEE_HIT).afterMeleeHit(tool, modifierEntry, context, damage * (2f + modifierEntry.getLevel() * 0.25f));
            }

            return 0;
        }
        return damage;
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier,
            Projectile projectile, EntityHitResult hit, LivingEntity attacker, LivingEntity target) {
        for(ModifierEntry toolEntry:modifiers.getModifiers()){
            if(!toolEntry.matches(this)){
                toolEntry.getHook(ModifierHooks.PROJECTILE_HIT).onProjectileHitEntity(modifiers, persistentData, modifier, projectile, hit, attacker, target);
            };
            
        }
        return false;
    }
}
