package moffy.ticex.event;

import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import vazkii.botania.common.BotaniaDamageTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class TicEXBotaniaEvent {
    private static final Map<UUID, Boolean> criticalFlag = new HashMap<>();

    public static void onCritical(CriticalHitEvent event) {
        Player player = event.getEntity();
        Entity entity = event.getTarget();
        criticalFlag.put(player.getUUID(), true);
        if (!player.level().isClientSide()) {
            for (ItemStack armorStack : player.getArmorSlots()) {
                ToolStack armor = ToolStack.from(armorStack);
                if (armorStack.getItem() instanceof IModifiable) {
                    if (TicEXRegistry.DHAROK != null && armor.getModifierLevel(TicEXRegistry.DHAROK.get()) > 0) {
                        event.setDamageModifier(event.getDamageModifier() * (1F + (1F - player.getHealth() / player.getMaxHealth()) * 0.5F));
                    }
                    if (entity instanceof LivingEntity livingEntity) {
                        if (TicEXRegistry.AHRIM != null && armor.getModifierLevel(TicEXRegistry.AHRIM.get()) > 0) {
                            livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 1));

                        }
                        if (TicEXRegistry.TORAG != null && armor.getModifierLevel(TicEXRegistry.TORAG.get()) > 0) {
                            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));

                        }
                        if (TicEXRegistry.KARIL != null && armor.getModifierLevel(TicEXRegistry.KARIL.get()) > 0) {
                            livingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 1));

                        }
                    }
                }
            }
        }
    }

    public static void onLivingAttack(LivingAttackEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            Level level = event.getEntity().level();
            if (event.getSource().getEntity() instanceof Player player) {
                if (criticalFlag.getOrDefault(player.getUUID(), false)) {
                    criticalFlag.remove(player.getUUID());
                    for (ItemStack armorStack : player.getArmorSlots()) {
                        ToolStack armor = ToolStack.from(armorStack);
                        if (armorStack.getItem() instanceof IModifiable) {
                            if (TicEXRegistry.GUTHAN != null && armor.getModifierLevel(TicEXRegistry.GUTHAN.get()) > 0) {
                                player.heal(event.getAmount()*0.25f);
                            }
                            if (TicEXRegistry.VERAC != null && armor.getModifierLevel(TicEXRegistry.VERAC.get()) > 0) {
                                event.setCanceled(true);
                                DamageSource damageSource = new DamageSource(level.registryAccess()
                                        .registryOrThrow(Registries.DAMAGE_TYPE)
                                        .getHolderOrThrow(BotaniaDamageTypes.PLAYER_ATTACK_ARMOR_PIERCING), event.getSource().getDirectEntity(), event.getSource().getEntity());
                                event.getEntity().hurt(damageSource, event.getAmount());
                            }
                        }
                    }

                }
            }
        }
    }
}
