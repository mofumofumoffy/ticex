package moffy.ticex.modifier;

import java.util.Map;
import java.util.function.BiFunction;

import committee.nova.mods.avaritia.init.registry.ModDamageTypes;
import committee.nova.mods.avaritia.util.ToolUtils;
import moffy.ticex.lib.hook.ProvidePropertyModifierHook;
import moffy.ticex.modifier.propeties.OmnipotenceProperty;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

public class ModifierOmnipotence extends NoLevelsModifier implements ProjectileHitModifierHook, MeleeHitModifierHook, BreakSpeedModifierHook, ProvidePropertyModifierHook{
    @Override
    public int getPriority() {
        return 999;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MELEE_HIT, ModifierHooks.BREAK_SPEED, TicEXRegistry.PROPERTY_PROVIDER_HOOK);
    }

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage,
            float baseKnockback, float knockback) {
        LivingEntity victim = context.getLivingTarget();
        if(victim != null){
            if (victim instanceof EnderDragon) {
                victim.setInvulnerable(false);
            } else if (victim instanceof Player pvp) {
                if (ToolUtils.isInfinite(pvp)) {
                    pvp.level().explode(context.getPlayerAttacker(), pvp.getBlockX(), pvp.getBlockY(), pvp.getBlockZ(), 25.0F, Level.ExplosionInteraction.MOB);
                    return 0;
                } else {
                    victim.setInvulnerable(false);
                }
            } else {
                victim.setInvulnerable(false);
            }
        }
        return MeleeHitModifierHook.super.beforeMeleeHit(tool, modifier, context, damage, baseKnockback, knockback);
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context,
            float damageDealt){
        LivingEntity attackerEntity = context.getAttacker();
        Entity victim = context.getTarget();
        if(context.getLevel() instanceof ServerLevel){
            dealInfinityDamage(context.getLevel(), attackerEntity, victim);
        }
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier,
            Projectile projectile, EntityHitResult hit, LivingEntity attacker, LivingEntity target) {
        boolean result = ProjectileHitModifierHook.super.onProjectileHitEntity(modifiers, persistentData, modifier, projectile, hit,
        attacker, target);

        if(attacker.level() instanceof ServerLevel){
            dealInfinityDamage(attacker.level(), attacker, target);
        }

        return result;
    }

    @Override
    public void onBreakSpeed(IToolStackView tool, ModifierEntry entry, BreakSpeed event, Direction direction, boolean isEffective,
            float miningSpeedModifier) {
        event.setNewSpeed(Float.MAX_VALUE);
    }

    private void dealInfinityDamage(Level level, LivingEntity attackerEntity, Entity targetEntity){
        if(targetEntity != null && targetEntity.isAlive()){
            LivingEntity victim = null;

            if(targetEntity instanceof LivingEntity){
                victim = (LivingEntity)targetEntity;
            } else if(targetEntity instanceof PartEntity){
                Entity parentEntity = ((PartEntity<?>)targetEntity).getParent();
                if(parentEntity instanceof LivingEntity){
                    victim = (LivingEntity)parentEntity;
                }
            }


            if(level instanceof ServerLevel){
                if(victim != null){
                    ServerLevel serverLevel = (ServerLevel)level;

                    try{
                        ToolUtils.class.getDeclaredMethod("sweepAttack", Level.class, LivingEntity.class, Entity.class);
                        ToolUtils.sweepAttack(level, attackerEntity, targetEntity);
                    }catch(Exception e){}

                    victim.setHealth(0);
                    victim.die(new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ModDamageTypes.INFINITY), victim, attackerEntity));

                    int reward = victim.getExperienceReward();
                    if(reward > 0){
                        victim.level().addFreshEntity(new ExperienceOrb(victim.level(), victim.getX(), victim.getY(), victim.getZ(), reward));
                    }

                    serverLevel.broadcastEntityEvent(victim, (byte)3);
                } else {

                    ServerLevel serverLevel = (ServerLevel)level;
                    targetEntity.killedEntity(serverLevel, attackerEntity);
                    targetEntity.kill();
                    serverLevel.broadcastEntityEvent(targetEntity, (byte)3);
                }
            }
            if(victim != null)victim.setPose(Pose.DYING);
        }
    }

    @Override
    public BiFunction<Player, ItemStack, Map<String, Object>> getPropertyProvider() {
        return OmnipotenceProperty.getProperties();
    }
}
