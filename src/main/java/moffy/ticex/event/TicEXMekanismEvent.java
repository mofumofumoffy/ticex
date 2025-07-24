package moffy.ticex.event;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.functions.FloatSupplier;
import mekanism.api.gear.IModule;
import mekanism.api.gear.IModuleHelper;
import mekanism.api.math.FloatingLong;
import mekanism.api.math.FloatingLongSupplier;
import mekanism.common.Mekanism;
import mekanism.common.base.KeySync;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.IBlastingItem;
import mekanism.common.content.gear.mekasuit.ModuleHydraulicPropulsionUnit;
import mekanism.common.content.gear.mekasuit.ModuleLocomotiveBoostingUnit;
import mekanism.common.item.gear.ItemFreeRunners;
import mekanism.common.registries.MekanismModules;
import mekanism.common.util.StorageUtils;
import moffy.ticex.TicEXConfig;
import moffy.ticex.client.modules.mekanism.MekaPlateModelCache;
import moffy.ticex.item.modifiable.ModifiableMekaSuitArmor;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.ModelEvent.BakingCompleted;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

public class TicEXMekanismEvent {

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (TicEXConfig.MEKAPLATE_USE_POWER_SHIELD.get()) {
            LivingEntity entity = event.getEntity();
            if (event.getAmount() <= 0 || !entity.isAlive()) {
                return;
            }
            if (event.getSource().is(DamageTypeTags.IS_FALL)) {
                FallEnergyInfo info = getFallAbsorptionEnergyInfo(entity);
                if (info != null && handleDamage(event, info.container, info.damageRatio, info.energyCost)) {
                    return;
                }
            }
            if (entity instanceof Player player) {
                float ratioAbsorbed = ModifiableMekaSuitArmor.getDamageAbsorbed(
                    player,
                    event.getSource(),
                    event.getAmount()
                );
                if (ratioAbsorbed > 0) {
                    float damageRemaining = event.getAmount() * Math.max(0, 1 - ratioAbsorbed);
                    if (damageRemaining <= 0) {
                        entity.setDeltaMovement(0, 0, 0);
                        entity.hurtTime = 0;
                        entity.hurtDuration = 0;
                        event.setCanceled(true);
                    } else {
                        event.setAmount(damageRemaining);
                    }
                }
            }
        }
    }

    private boolean handleDamage(
        LivingHurtEvent event,
        @Nullable IEnergyContainer energyContainer,
        FloatSupplier absorptionRatio,
        FloatingLongSupplier energyCost
    ) {
        if (energyContainer != null) {
            float absorption = absorptionRatio.getAsFloat();
            float amount = event.getAmount() * absorption;
            FloatingLong energyRequirement = energyCost.get().multiply(amount);
            float ratioAbsorbed;
            if (energyRequirement.isZero()) {
                ratioAbsorbed = absorption;
            } else {
                ratioAbsorbed =
                    absorption *
                    energyContainer
                        .extract(energyRequirement, Action.EXECUTE, AutomationType.MANUAL)
                        .divide(amount)
                        .floatValue();
            }
            if (ratioAbsorbed > 0) {
                float damageRemaining = event.getAmount() * Math.max(0, 1 - ratioAbsorbed);
                if (damageRemaining <= 0) {
                    event.setCanceled(true);
                    return true;
                } else {
                    event.setAmount(damageRemaining);
                }
            }
        }
        return false;
    }

    @SubscribeEvent
    public void onLivingJump(LivingJumpEvent event) {
        if (event.getEntity() instanceof Player player) {
            IModule<ModuleHydraulicPropulsionUnit> module = IModuleHelper.INSTANCE.load(
                player.getItemBySlot(EquipmentSlot.FEET),
                MekanismModules.HYDRAULIC_PROPULSION_UNIT
            );
            if (module != null && module.isEnabled() && Mekanism.keyMap.has(player.getUUID(), KeySync.BOOST)) {
                float boost = module.getCustomInstance().getBoost();
                FloatingLong usage = MekanismConfig.gear.mekaSuitBaseJumpEnergyUsage.get().multiply(boost / 0.1F);
                IEnergyContainer energyContainer = module.getEnergyContainer();
                if (module.canUseEnergy(player, energyContainer, usage, false)) {
                    IModule<ModuleLocomotiveBoostingUnit> boostModule = IModuleHelper.INSTANCE.load(
                        player.getItemBySlot(EquipmentSlot.LEGS),
                        MekanismModules.LOCOMOTIVE_BOOSTING_UNIT
                    );
                    if (
                        boostModule != null &&
                        boostModule.isEnabled() &&
                        boostModule.getCustomInstance().canFunction(boostModule, player)
                    ) {
                        boost = (float) Math.sqrt(boost);
                    }
                    player.setDeltaMovement(player.getDeltaMovement().add(0, boost, 0));
                    module.useEnergy(player, energyContainer, usage, true);
                }
            }
        }
    }

    @Nullable
    private FallEnergyInfo getFallAbsorptionEnergyInfo(LivingEntity base) {
        ItemStack feetStack = base.getItemBySlot(EquipmentSlot.FEET);
        if (!feetStack.isEmpty()) {
            if (feetStack.getItem() instanceof ItemFreeRunners boots) {
                if (boots.getMode(feetStack).preventsFallDamage()) {
                    return new FallEnergyInfo(
                        StorageUtils.getEnergyContainer(feetStack, 0),
                        MekanismConfig.gear.freeRunnerFallDamageRatio,
                        MekanismConfig.gear.freeRunnerFallEnergyCost
                    );
                }
            } else if (feetStack.getItem() instanceof ModifiableMekaSuitArmor) {
                return new FallEnergyInfo(
                    StorageUtils.getEnergyContainer(feetStack, 0),
                    MekanismConfig.gear.mekaSuitFallDamageRatio,
                    MekanismConfig.gear.mekaSuitEnergyUsageFall
                );
            }
        }
        return null;
    }

    private record FallEnergyInfo(
        @Nullable IEnergyContainer container,
        FloatSupplier damageRatio,
        FloatingLongSupplier energyCost
    ) {}

    @SubscribeEvent
    public void getBreakSpeed(BreakSpeed event) {
        Player player = event.getEntity();
        float speed = event.getNewSpeed();

        Optional<BlockPos> position = event.getPosition();
        if (position.isPresent()) {
            BlockPos pos = position.get();

            ItemStack mainHand = player.getMainHandItem();
            if (!mainHand.isEmpty() && mainHand.getItem() instanceof IBlastingItem tool) {
                Map<BlockPos, BlockState> blocks = tool.getBlastedBlocks(
                    player.level(),
                    player,
                    mainHand,
                    pos,
                    event.getState()
                );
                if (!blocks.isEmpty()) {
                    float targetHardness = event.getState().getDestroySpeed(player.level(), pos);
                    float maxHardness = blocks
                        .entrySet()
                        .stream()
                        .map(entry -> entry.getValue().getDestroySpeed(player.level(), entry.getKey()))
                        .reduce(targetHardness, Float::max);
                    speed *= (targetHardness / maxHardness);
                }
            }
        }

        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        if (!legs.isEmpty() && IModuleHelper.INSTANCE.isEnabled(legs, MekanismModules.GYROSCOPIC_STABILIZATION_UNIT)) {
            if (player.isEyeInFluidType(ForgeMod.WATER_TYPE.get()) && !EnchantmentHelper.hasAquaAffinity(player)) {
                speed *= 5.0F;
            }

            if (!player.onGround()) {
                speed *= 5.0F;
            }
        }

        event.setNewSpeed(speed);
    }

    @OnlyIn(Dist.CLIENT)
    public static void onLoadAdditionalModel(ModelEvent.RegisterAdditional event) {
        MekaPlateModelCache.INSTANCE.setup(event);
    }

    @OnlyIn(Dist.CLIENT)
    public static void onModelBake(BakingCompleted event) {
        MekaPlateModelCache.INSTANCE.onBake(event);
    }
}
