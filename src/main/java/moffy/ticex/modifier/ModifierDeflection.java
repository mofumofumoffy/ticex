package moffy.ticex.modifier;

import java.lang.reflect.Field;

import moffy.ticex.TicEX;
import moffy.ticex.entity.FakeLivingEntity;
import moffy.ticex.lib.IEntityDataAccessor;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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

    private FakeLivingEntity fakeLivingEntity = null;

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
        if(!context.isExtraAttack()){
            if(fakeLivingEntity == null){
                fakeLivingEntity = new FakeLivingEntity((EntityType<? extends LivingEntity>)TicEXRegistry.FAKE_LIVING_ENTITY.get(), context.getLevel());
            }
    
            LivingEntity target = context.getLivingTarget();
            Player attacker = context.getPlayerAttacker();
            
            if(target != null && attacker != null){
                fakeLivingEntity.setHealth(target.getHealth());
    
                ToolAttackContext newContext = new ToolAttackContext(attacker, attacker, context.getHand(), fakeLivingEntity, fakeLivingEntity, true, context.getCooldown(), false);
                
                for(ModifierEntry toolEntry:tool.getModifierList()){
                    toolEntry.getHook(ModifierHooks.MELEE_HIT).beforeMeleeHit(tool, modifierEntry, newContext, damage, baseDamage, damage);
                }

                fakeLivingEntity.hurt(null, damage);
                fakeLivingEntity.invulnerableTime = 0;
                
                for(ModifierEntry toolEntry:tool.getModifierList()){
                    toolEntry.getHook(ModifierHooks.MELEE_HIT).afterMeleeHit(tool, modifierEntry, newContext, damage);
                }

                float absoluteHealth = fakeLivingEntity.getFakeHealth();
                IEntityDataAccessor accessor = (IEntityDataAccessor)target;
                Field key = accessor.getField("f_20961_");
                if(key != null){
                    accessor.setValue(key, absoluteHealth);
                }
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
