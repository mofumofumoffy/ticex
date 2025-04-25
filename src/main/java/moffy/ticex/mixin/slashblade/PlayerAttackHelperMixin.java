package moffy.ticex.mixin.slashblade;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mods.flammpfeil.slashblade.SlashBladeConfig;
import mods.flammpfeil.slashblade.capability.concentrationrank.ConcentrationRankCapabilityProvider;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.AttackManager;
import mods.flammpfeil.slashblade.util.PlayerAttackHelper;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ModifierLootingHandler;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@Mixin(value = PlayerAttackHelper.class, remap = false)
public class PlayerAttackHelperMixin {
    @Inject(
        at = @At("invoke"),
        method = "attack",
        cancellable = true
    )
    private static void attackExtension(Player attacker, Entity target, float comboRatio, CallbackInfo cb){
        if (!ForgeHooks.onPlayerAttackTarget(attacker, target)){
            cb.cancel();
        }
        ItemStack mainHandStack = attacker.getMainHandItem();
        if(mainHandStack != null && mainHandStack.getItem() instanceof IModifiable){
            boolean isCritical = attacker.fallDistance > 0.0F && !attacker.onGround() &&
                            !attacker.onClimbable() && !attacker.isInWater() &&
                            !attacker.hasEffect(MobEffects.BLINDNESS) &&
                            !attacker.isPassenger() && target instanceof LivingEntity && !attacker.isSprinting();

            ToolStack tool = ToolStack.from(mainHandStack);
            ToolAttackContext context = new ToolAttackContext(attacker, attacker, InteractionHand.MAIN_HAND, target, target instanceof LivingEntity ? (LivingEntity)target : null, isCritical, 0, false);
            if ((target.isAttackable() && !target.skipAttackInteraction(attacker)) || tool.getModifierLevel(TicEXRegistry.DEFLECTION_MODIFIER.get()) > 0) {
                ModifierLootingHandler.setLootingSlot(context.getAttacker(), EquipmentSlot.MAINHAND);

                float baseDamage = ToolAttackUtil.getAttributeAttackDamage(tool, attacker, EquipmentSlot.MAINHAND);
                float baseDamageTmp = baseDamage;

                List<ModifierEntry> modifiers = tool.getModifierList();
                for(ModifierEntry modifier : modifiers){
                    baseDamage = modifier.getHook(ModifierHooks.MELEE_DAMAGE).getMeleeDamage(tool, modifier, context, baseDamageTmp, baseDamage);
                }

                baseDamage += 10 * (EnchantmentHelper.getSweepingDamageRatio(attacker) * 0.5f);

                IConcentrationRank.ConcentrationRanks rankBonus = attacker
                    .getCapability(ConcentrationRankCapabilityProvider.RANK_POINT)
                    .map(rp -> rp.getRank(attacker.getCommandSenderWorld().getGameTime()))
                    .orElse(IConcentrationRank.ConcentrationRanks.NONE);
                float rankDamageBonus = rankBonus.level / 2.0f;
                if (IConcentrationRank.ConcentrationRanks.S.level <= rankBonus.level) {
                    int refine = attacker.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).map(rp -> rp.getRefine()).orElse(0);
                    int level = attacker.experienceLevel;
                    rankDamageBonus = (float) Math.max(rankDamageBonus, Math.min(level, refine) * SlashBladeConfig.REFINE_DAMAGE_MULTIPLIER.get());
                }
                baseDamage += rankDamageBonus;

                float enchantmentDamageBonus;
                if (target instanceof LivingEntity) {
                    enchantmentDamageBonus  = EnchantmentHelper.getDamageBonus(attacker.getMainHandItem(), ((LivingEntity)target).getMobType());
                } else {
                    enchantmentDamageBonus  = EnchantmentHelper.getDamageBonus(attacker.getMainHandItem(), MobType.UNDEFINED);
                }
                baseDamage += enchantmentDamageBonus;

                baseDamage *= comboRatio * AttackManager.getSlashBladeDamageScale(attacker) * SlashBladeConfig.SLASHBLADE_DAMAGE_MULTIPLIER.get();

                if (baseDamage > 0.0F) {
                    float knockback = (float) attacker.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
                    knockback += EnchantmentHelper.getKnockbackBonus(attacker);
                    if (attacker.isSprinting()) {
                        attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, attacker.getSoundSource(), 1.0F, 1.0F);
                        ++knockback;
                    }
                    float knockbackTmp = knockback;

                    CriticalHitEvent hitResult = ForgeHooks.getCriticalHit(attacker, target, isCritical, isCritical ? 1.5F : 1.0F);
                    isCritical = hitResult != null;
                    if (isCritical) {
                        baseDamage *= hitResult.getDamageModifier();
                    }

                    float preAttackHealth = 0.0F;
                    boolean shouldSetFire = false;
                    int fireAspectLevel = EnchantmentHelper.getFireAspect(attacker);
                    if (target instanceof LivingEntity) {
                        preAttackHealth = ((LivingEntity) target).getHealth();
                        if (fireAspectLevel > 0 && !target.isOnFire()) {
                            shouldSetFire = true;
                            target.setSecondsOnFire(1);
                        }
                    }

                    Vec3 vec3 = target.getDeltaMovement();

                    for(ModifierEntry modifier : modifiers){
                        knockback = modifier.getHook(ModifierHooks.MELEE_HIT).beforeMeleeHit(tool, modifier, context, baseDamage, knockbackTmp, knockback);
                    }
                    boolean damageSuccess = ToolAttackUtil.dealDefaultDamage(attacker, target, baseDamage);
                    if (damageSuccess) {

                        if (knockback > 0) {
                            if (target instanceof LivingEntity living) {
                                living.knockback(knockback * 0.5D, Mth.sin(attacker.getYRot() * ((float) Math.PI / 180F)), -Mth.cos(attacker.getYRot() * ((float) Math.PI / 180F)));
                            } else {
                                target.push(-Mth.sin(attacker.getYRot() * ((float) Math.PI / 180F)) * knockback * 0.5D, 0.1D, Mth.cos(attacker.getYRot() * ((float) Math.PI / 180F)) * knockback * 0.5D);
                            }

                            attacker.setDeltaMovement(attacker.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                            attacker.setSprinting(false);
                        }

                        if (target instanceof ServerPlayer && target.hurtMarked) {
                            ((ServerPlayer) target).connection.send(new ClientboundSetEntityMotionPacket(target));
                            target.hurtMarked = false;
                            target.setDeltaMovement(vec3);
                        }

    
                        attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, attacker.getSoundSource(), 1.0F, 1.0F);
                        if (isCritical) {
                            attacker.crit(target);
                        }


                        attacker.setLastHurtMob(target);
                        if (target instanceof LivingEntity) {
                            EnchantmentHelper.doPostHurtEffects((LivingEntity) target, attacker);
                        }

                        EnchantmentHelper.doPostDamageEffects(attacker, target);
                        ItemStack itemstack1 = attacker.getMainHandItem();
                        Entity entity = target;
                        if (target instanceof net.minecraftforge.entity.PartEntity) {
                            entity = ((net.minecraftforge.entity.PartEntity<?>) target).getParent();
                        }

                        if (itemstack1 != null && !attacker.level().isClientSide() && !itemstack1.isEmpty() && entity instanceof LivingEntity) {
                            itemstack1.hurtEnemy((LivingEntity) entity, attacker);
                        }

                        if (target instanceof LivingEntity) {
                            float damageDealt = preAttackHealth - ((LivingEntity) target).getHealth();
                            attacker.awardStat(Stats.DAMAGE_DEALT, Math.round(damageDealt * 10.0F));
                            if (fireAspectLevel > 0) {
                                target.setSecondsOnFire(fireAspectLevel * 4);
                            }


                            if (attacker.level() instanceof ServerLevel && damageDealt > 2.0F) {
                                int k = (int) (damageDealt * 0.5D);
                                ((ServerLevel) attacker.level()).sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY(0.5D), target.getZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
                            }

                            for(ModifierEntry modifier : modifiers){
                                modifier.getHook(ModifierHooks.MELEE_HIT).afterMeleeHit(tool, modifier, context, damageDealt);
                            }
                        }

                        attacker.causeFoodExhaustion(0.1F);

                        
                    } else {
                        attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, attacker.getSoundSource(), 1.0F, 1.0F);
                        if (shouldSetFire) {
                            target.clearFire();
                        }

                        for(ModifierEntry modifier : modifiers){
                            modifier.getHook(ModifierHooks.MELEE_HIT).failedMeleeHit(tool, modifier, context, baseDamage);
                        }
                    }
                }
            }
            cb.cancel();
        }
    }
}
