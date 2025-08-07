package moffy.ticex.event;

import mods.flammpfeil.slashblade.SlashBladeConfig;
import mods.flammpfeil.slashblade.event.BladeMotionEvent;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.event.handler.InputCommandEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.AttackManager;
import mods.flammpfeil.slashblade.util.PlayerAttackHelper;
import moffy.ticex.TicEX;
import moffy.ticex.client.modules.slashblade.SBToolBladeItemRenderer;
import moffy.ticex.entity.slashblade.SBToolItemEntity;
import moffy.ticex.item.modifiable.ModifiableSlashBladeItem;
import moffy.ticex.modules.general.TicEXRegistry;
import moffy.ticex.network.slashblade.StateSyncPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.network.PacketDistributor;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Optional;

public class TicEXSBEvent {

    public static void onBladeMotion(BladeMotionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncState(player);
        }
    }

    public static void onLivingFall(LivingFallEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncState(player);
        }
    }

    public static void onPlayerFlyableFall(PlayerFlyableFallEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncState(player);
        }
    }

    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            syncState(player);
        }
    }

    public static void onLivingExperienceDrop(LivingExperienceDropEvent event) {
        if (event.getAttackingPlayer() instanceof ServerPlayer player) {
            syncState(player);
        }
    }

    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
                syncState(player);
        }
    }

    public static void onInputCommand(InputCommandEvent event) {
        syncState(event.getEntity());
    }

    public static void onHit(SlashBladeEvent.HitEvent event){
        if(event.getUser() instanceof ServerPlayer player){
            ItemStack mainHandStack = player.getMainHandItem();
            if(mainHandStack.getItem() instanceof  ModifiableSlashBladeItem) {
                ToolStack tool = ToolStack.from(mainHandStack);
                LivingEntity target = event.getTarget();
                ToolAttackContext context = new ToolAttackContext(player, player, InteractionHand.MAIN_HAND, target, target, false, 1, false);

                float damage = (float) calculateTotalDamage(player, target, 1.0f, player.isFallFlying());
                float damageTmp = damage;

                for (ModifierEntry modifierEntry : tool.getModifierList()) {
                    damage = modifierEntry.getHook(ModifierHooks.MELEE_DAMAGE).getMeleeDamage(tool, modifierEntry, context, damageTmp, damage);
                }

                if (damage <= 0.0F) {
                    event.setCanceled(true);
                } else {
                    for (ModifierEntry modifierEntry : tool.getModifierList()) {
                        modifierEntry.getHook(ModifierHooks.MELEE_HIT).beforeMeleeHit(tool, modifierEntry, context, damage, 0, 0);
                    }
                    for (ModifierEntry modifierEntry : tool.getModifierList()) {
                        modifierEntry.getHook(ModifierHooks.MELEE_HIT).afterMeleeHit(tool, modifierEntry, context, damage);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(
            (EntityType<SBToolItemEntity>) TicEXRegistry.SLASHBLADE_TOOL_ITEM_ENTITY.get(),
            SBToolBladeItemRenderer::new
        );
    }

    public static double calculateTotalDamage(Player attacker, Entity target, float comboRatio, boolean isCritical) {
        double baseDamage = attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);

        baseDamage += PlayerAttackHelper.getSweepingBonus(attacker);
        baseDamage += PlayerAttackHelper.getRankBonus(attacker);
        baseDamage += PlayerAttackHelper.getEnchantmentBonus(attacker, target);
        baseDamage *= comboRatio * AttackManager.getSlashBladeDamageScale(attacker) * SlashBladeConfig.SLASHBLADE_DAMAGE_MULTIPLIER.get();

        CriticalHitEvent hitResult = ForgeHooks.getCriticalHit(attacker, target, isCritical, isCritical ? 1.5F : 1.0F);
        isCritical = hitResult != null;
        if (isCritical) {
            baseDamage *= hitResult.getDamageModifier();
        }
        return baseDamage;
    }

    public static void syncState(ServerPlayer player) {
        ItemStack mainHandStack = player.getMainHandItem();
        if (mainHandStack.getItem() instanceof ModifiableSlashBladeItem) {
            mainHandStack
                .getCapability(ItemSlashBlade.BLADESTATE)
                .ifPresent(state -> {
                    StateSyncPacket packet = new StateSyncPacket(state.serializeNBT());
                    TicEX.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
                });
        }
    }
}
