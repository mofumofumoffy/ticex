package moffy.ticex.mixin.slashblade;

import java.util.List;
import mods.flammpfeil.slashblade.SlashBladeConfig;
import mods.flammpfeil.slashblade.capability.concentrationrank.ConcentrationRankCapabilityProvider;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.AttackHelper;
import mods.flammpfeil.slashblade.util.AttackManager;
import moffy.ticex.modules.general.TicEXRegistry;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ModifierLootingHandler;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@Mixin(value = AttackHelper.class, remap = false)
public abstract class PlayerAttackHelperMixin {

    @Shadow
    public static float getSweepingBonus(LivingEntity attacker) {
        return 0;
    }

    @Shadow
    public static float getRankBonus(LivingEntity attacker) {
        return 0;
    }

    @Shadow
    public static float getEnchantmentBonus(LivingEntity attacker, Entity target) {
        return 0;
    }

    @Shadow
    public static float calculateKnockback(LivingEntity attacker) {
        return 0;
    }

    @Shadow
    public static boolean isCriticalHit(LivingEntity attacker, Entity target) {
        return false;
    }

    @Shadow
    public static AttackHelper.FireAspectResult handleFireAspect(LivingEntity attacker, Entity target) {
        return null;
    }

    @Shadow
    public static void applyKnockback(LivingEntity attacker, Entity target, float knockback) {
    }

    @Shadow
    public static void restoreTargetMotionIfNeeded(Entity target, Vec3 originalMotion) {
    }

    @Shadow
    public static void playAttackEffects(LivingEntity attacker, Entity target, boolean isCritical) {
    }

    @Shadow
    public static void handleEnchantmentsAndDurability(LivingEntity attacker, Entity target) {
    }

    @Shadow
    public static void handlePostAttackEffects(LivingEntity attacker, Entity target, AttackHelper.FireAspectResult fireAspectResult) {
    }

    @Shadow
    public static void handleFailedAttack(LivingEntity attacker, Entity target, AttackHelper.FireAspectResult fireAspectResult) {
    }

    @Inject(at = @At("HEAD"), method = "attack", cancellable = true)
    private static void attackExtension(LivingEntity attacker, Entity target, float comboRatio, CallbackInfo ci) {
        ItemStack stack = attacker.getMainHandItem();
        if(attacker instanceof Player player && stack.getItem() instanceof IModifiable){
            if (ForgeHooks.onPlayerAttackTarget(player, target)) {
                float baseDamage = (float)attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);
                baseDamage += getSweepingBonus(player);
                baseDamage += getRankBonus(player);
                baseDamage += getEnchantmentBonus(player, target);
                baseDamage = (float)((double)baseDamage * (double)(comboRatio * AttackManager.getSlashBladeDamageScale(attacker)) * (Double)SlashBladeConfig.SLASHBLADE_DAMAGE_MULTIPLIER.get());
                if (!(baseDamage <= 0.0F)) {
                    float knockback = calculateKnockback(player);
                    boolean isCritical = isCriticalHit(player, target);
                    CriticalHitEvent hitResult = ForgeHooks.getCriticalHit(player, target, isCritical, isCritical ? 1.5F : 1.0F);
                    isCritical = hitResult != null;
                    if (isCritical) {
                        baseDamage *= hitResult.getDamageModifier();
                    }

                    AttackHelper.FireAspectResult fireAspectResult = handleFireAspect(player, target);
                    Vec3 originalMotion = target.getDeltaMovement();

                    ToolStack bladeTool = ToolStack.from(stack);
                    float baseDamageTmp = baseDamage;

                    ToolAttackContext context = new ToolAttackContext(attacker, player, InteractionHand.MAIN_HAND, target, target instanceof  LivingEntity ? (LivingEntity) target : null, isCritical, 1, false);

                    for(ModifierEntry entry : bladeTool.getModifiers()){
                        baseDamage = entry.getHook(ModifierHooks.MELEE_DAMAGE).getMeleeDamage(bladeTool, entry, context, baseDamageTmp, baseDamage);
                    }

                    if(baseDamage <= 0.0F){
                        ci.cancel();
                    }

                    float knockbackTmp = knockback;
                    for(ModifierEntry entry : bladeTool.getModifiers()){
                        knockback = entry.getHook(ModifierHooks.MELEE_HIT).beforeMeleeHit(bladeTool, entry, context, baseDamage, knockbackTmp, knockback);
                    }

                    boolean damageSuccess = target.hurt(attacker.damageSources().playerAttack(player), baseDamage);
                    if (damageSuccess) {
                        applyKnockback(attacker, target, knockback);
                        restoreTargetMotionIfNeeded(target, originalMotion);
                        playAttackEffects(attacker, target, isCritical);
                        handleEnchantmentsAndDurability(attacker, target);
                        handlePostAttackEffects(attacker, target, fireAspectResult);

                        for(ModifierEntry entry : bladeTool.getModifiers()){
                            entry.getHook(ModifierHooks.MELEE_HIT).afterMeleeHit(bladeTool, entry, context, baseDamage);
                        }
                    } else {
                        handleFailedAttack(attacker, target, fireAspectResult);
                        for(ModifierEntry entry : bladeTool.getModifiers()){
                            entry.getHook(ModifierHooks.MELEE_HIT).failedMeleeHit(bladeTool, entry, context, baseDamage);
                        }
                    }
                }
            }
            ci.cancel();
        }
    }
}
