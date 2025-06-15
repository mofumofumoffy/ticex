package moffy.ticex.modifier;

import java.lang.reflect.Field;

import moffy.ticex.entity.FakeLivingEntity;
import moffy.ticex.lib.IEntityDataAccessor;
import moffy.ticex.modules.general.TicEXRegistry;
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

    @SuppressWarnings("unchecked")
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
                ToolAttackContext fakeContext = new ToolAttackContext(attacker, attacker, context.getHand(),fakeLivingEntity, fakeLivingEntity, context.isCritical(), damage, context.isExtraAttack());

                for(ModifierEntry toolEntry:tool.getModifierList()){
                    var hook = toolEntry.getHook(ModifierHooks.MELEE_HIT);
                    hook.beforeMeleeHit(tool, modifierEntry, context, damage, 0, 0);
                    hook.beforeMeleeHit(tool, modifierEntry, fakeContext, damage, 0, 0);
                }

                fakeLivingEntity.hurt(null, damage);
                fakeLivingEntity.invulnerableTime = 0;

                for(ModifierEntry toolEntry:tool.getModifierList()){
                    var hook = toolEntry.getHook(ModifierHooks.MELEE_HIT);
                    hook.afterMeleeHit(tool, modifierEntry, context, damage);
                    hook.afterMeleeHit(tool, modifierEntry, fakeContext, damage);
                }

                float absoluteHealth = fakeLivingEntity.getFakeHealth();
                IEntityDataAccessor accessor = (IEntityDataAccessor)target;

                String fieldName = "f_20961_";
                //fieldName = "DATA_HEALTH_ID";

                Field key = accessor.getField(fieldName);
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
