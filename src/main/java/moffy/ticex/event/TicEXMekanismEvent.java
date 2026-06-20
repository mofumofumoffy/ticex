package moffy.ticex.event;

import com.google.common.collect.ImmutableMultimap;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.functions.FloatSupplier;
import mekanism.api.gear.IModule;
import mekanism.api.gear.IModuleHelper;
import mekanism.api.math.FloatingLong;
import mekanism.api.math.FloatingLongSupplier;
import mekanism.api.text.EnumColor;
import mekanism.client.ClientTickHandler;
import mekanism.client.MekanismClient;
import mekanism.client.key.MekKeyHandler;
import mekanism.client.key.MekanismKeyHandler;
import mekanism.common.CommonPlayerTickHandler;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.base.KeySync;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.IBlastingItem;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.content.gear.mekasuit.ModuleHydraulicPropulsionUnit;
import mekanism.common.content.gear.mekasuit.ModuleLocomotiveBoostingUnit;
import mekanism.common.content.gear.mekatool.ModuleAttackAmplificationUnit;
import mekanism.common.integration.curios.CuriosIntegration;
import mekanism.common.item.interfaces.IJetpackItem;
import mekanism.common.lib.radiation.RadiationManager;
import mekanism.common.registries.MekanismGameEvents;
import mekanism.common.registries.MekanismModules;
import mekanism.common.util.StorageUtils;
import moffy.ticex.TicEX;
import moffy.ticex.caps.mekanism.MekaArmorGearCapability;
import moffy.ticex.client.modules.mekanism.MekaPlateModelCache;
import moffy.ticex.lib.modules.mekanism.MekaGearCapability;
import moffy.ticex.lib.modules.mekanism.interfaces.IAbsorbableItem;
import moffy.ticex.lib.modules.mekanism.interfaces.IMekaGear;
import moffy.ticex.lib.utils.TicEXMekanismWeaponsUtils;
import moffy.ticex.lib.utils.TicEXUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.ModelEvent.BakingCompleted;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class TicEXMekanismEvent {
    public static final UUID ATTACK_DAMAGE_ADDITION = UUID.fromString("435fee9c-f619-40fd-96a3-f9a7d75ec04a");
    public static final UUID ATTACK_DAMAGE_AMPLIFIER = UUID.fromString("f6847795-2993-4c62-9532-f34c8c0a8acf");

    public static void onModifyAttribute(ItemAttributeModifierEvent event){
        ItemStack stack = event.getItemStack();
        EquipmentSlot slot = event.getSlotType();
        if(stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent()){
            IMekaGear mekaGear = stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).orElseThrow(IllegalStateException::new);
            if (slot == EquipmentSlot.MAINHAND) {
                int unitDamage = 0;
                IModule<ModuleAttackAmplificationUnit> attackAmplificationUnit = mekaGear.getModule(stack, MekanismModules.ATTACK_AMPLIFICATION_UNIT);
                if (attackAmplificationUnit != null && attackAmplificationUnit.isEnabled()) {

                    unitDamage = attackAmplificationUnit.getCustomInstance().getDamage();
                    if (unitDamage > 0) {
                        FloatingLong energyCost = MekanismConfig.gear.mekaToolEnergyUsageWeapon.get().multiply(unitDamage / 4D);
                        IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
                        FloatingLong energy = energyContainer == null ? FloatingLong.ZERO : energyContainer.getEnergy();
                        if (energyCost.smallerThan(energy)) {
                            double bonusDamage = unitDamage * energy.divideToLevel(energyCost);
                            if (bonusDamage > 0) {
                                ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
                                builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_ADDITION, "Weapon modifier",
                                        bonusDamage, AttributeModifier.Operation.ADDITION));
                                builder.build().forEach(event::addModifier);
                                return;
                            }
                        }
                    }
                }
                if(ModList.get().isLoaded("mekaweapons")){
                    event.addModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_AMPLIFIER, "Weapon amplifier", TicEXMekanismWeaponsUtils.getAmplifier(stack), AttributeModifier.Operation.MULTIPLY_BASE));
                }
            }
        }
    }

    public static void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.side.isServer()) {
            tickEnd(event.player);
        }
    }

    static void handleArmorModifierHook(Player player, DamageSource source, float damageAmount){
        EquipmentContext context = new EquipmentContext(player);
        for(EquipmentSlot slot : TicEXUtils.ARMOR_TYPES){
            ItemStack stack = player.getItemBySlot(slot);
            if(stack.getItem() instanceof IModifiable){
                ToolStack tool = ToolStack.from(stack);
                for(ModifierEntry entry : tool.getModifierList()){
                    entry.getHook(ModifierHooks.ON_ATTACKED).onAttacked(tool, entry, context, slot, source, damageAmount, isDirectDamage(source));
                }
            }
        }
    }

    public static void onEntityAttack(LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();
        if (event.getAmount() <= 0 || !entity.isAlive()) {
            return;
        }
        if (event.getSource().is(DamageTypeTags.IS_FALL)) {
            FallEnergyInfo info = getFallAbsorptionEnergyInfo(entity);
            if (info != null && tryAbsorbAll(event, info.container, info.damageRatio, info.energyCost)) {
                return;
            }
        }
        if (entity instanceof Player player) {
            if (IAbsorbableItem.tryAbsorbAll(player, event.getSource(), event.getAmount())) {
                handleArmorModifierHook(player, event.getSource(), 0);
                event.setCanceled(true);
            }
        }
    }

    static boolean isDirectDamage(DamageSource source) {
        return source.getEntity() != null && !source.isIndirect() && !source.is(DamageTypeTags.AVOIDS_GUARDIAN_THORNS);
    }

    public static void onLivingHurt(LivingHurtEvent event) {
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
            float ratioAbsorbed = IAbsorbableItem.getDamageAbsorbed(
                    player,
                    event.getSource(),
                    event.getAmount()
            );
            if (ratioAbsorbed > 0) {
                float damageRemaining = event.getAmount() * Math.max(0, 1 - ratioAbsorbed);
                if (damageRemaining <= 0) {
                    handleArmorModifierHook(player, event.getSource(), 0);
                    event.setCanceled(true);
                } else {
                    event.setAmount(damageRemaining);
                }
            }
        }
    }

    public static void getBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        float speed = event.getNewSpeed();

        Optional<BlockPos> position = event.getPosition();
        if (position.isPresent()) {
            BlockPos pos = position.get();

            ItemStack mainHand = player.getMainHandItem();
            if (!mainHand.isEmpty()) {
                LazyOptional<IMekaGear> mekaGearLazyOptional = mainHand.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY);
                if(mekaGearLazyOptional.isPresent()){
                    IMekaGear mekaGear = mekaGearLazyOptional.orElseThrow(IllegalStateException::new);
                    if(mekaGear instanceof IBlastingItem blastingGear){
                        Map<BlockPos, BlockState> blocks = blastingGear.getBlastedBlocks(
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

    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.getAbilities().flying) return;

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

    private static void tickEnd(Player player) {
        Mekanism.playerState.updateStepAssist(player);
        Mekanism.playerState.updateSwimBoost(player);
        if (player instanceof ServerPlayer serverPlayer) {
            RadiationManager.get().tickServer(serverPlayer);
        }

        ItemStack currentItem = player.getInventory().getSelected();
        LazyOptional<IMekaGear> mekaGearLazyOptional = currentItem.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY);
        if(mekaGearLazyOptional.isPresent()){
            IMekaGear mekaGear = mekaGearLazyOptional.orElseThrow(IllegalStateException::new);

            ItemStack jetpack = getActiveJetpack(player);
            if (!jetpack.isEmpty() && mekaGear instanceof IJetpackItem jetpackGear) {
                ItemStack primaryJetpack = getPrimaryJetpack(player);
                if (!primaryJetpack.isEmpty()) {
                    IJetpackItem.JetpackMode primaryMode = jetpackGear.getJetpackMode(primaryJetpack);
                    IJetpackItem.JetpackMode mode = IJetpackItem.getPlayerJetpackMode(player, primaryMode, () -> Mekanism.keyMap.has(player.getUUID(), KeySync.ASCEND));
                    if (mode != IJetpackItem.JetpackMode.DISABLED) {
                        if (IJetpackItem.handleJetpackMotion(player, mode, () -> Mekanism.keyMap.has(player.getUUID(), KeySync.ASCEND))) {
                            player.resetFallDistance();
                            if (player instanceof ServerPlayer serverPlayer) {
                                serverPlayer.connection.aboveGroundTickCount = 0;
                            }
                        }
                        jetpackGear.useJetpackFuel(jetpack);
                        if (player.level().getGameTime() % 10 == 0) {
                            player.gameEvent(MekanismGameEvents.JETPACK_BURN.get());
                        }
                    }
                }
            }
        }

        Mekanism.playerState.updateFlightInfo(player);
    }

    private static ItemStack getActiveJetpack(LivingEntity entity) {
        return getJetpack(entity, stack -> {
            LazyOptional<IMekaGear> mekaGearLazyOptional = stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY);
            if(mekaGearLazyOptional.isPresent()){
                IMekaGear mekaGear = mekaGearLazyOptional.orElseThrow(IllegalStateException::new);
                if(mekaGear instanceof IJetpackItem iJetpackGear){
                    return iJetpackGear.canUseJetpack(stack);
                }
            }
            return false;
        });
    }

    private static ItemStack getJetpack(LivingEntity entity, Predicate<ItemStack> matcher) {
        ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (matcher.test(chest)) {
            return chest;
        } else {
            return Mekanism.hooks.CuriosLoaded ? CuriosIntegration.findFirstCurio(entity, matcher) : ItemStack.EMPTY;
        }
    }

    private static @NotNull ItemStack getPrimaryJetpack(LivingEntity entity) {
        return getJetpack(entity, (stack) -> {
            LazyOptional<IMekaGear> mekaGearLazyOptional = stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY);
            if(mekaGearLazyOptional.isPresent()){
                IMekaGear mekaGear = mekaGearLazyOptional.orElseThrow(IllegalStateException::new);
                return mekaGear instanceof IJetpackItem;
            }
            return false;
        });
    }

    private static boolean handleDamage(
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

    private static boolean tryAbsorbAll(LivingAttackEvent event, @Nullable IEnergyContainer energyContainer, FloatSupplier absorptionRatio, FloatingLongSupplier energyCost) {
        if (energyContainer != null && absorptionRatio.getAsFloat() == 1) {
            FloatingLong energyRequirement = energyCost.get().multiply(event.getAmount());
            if (energyRequirement.isZero()) {
                event.setCanceled(true);
                return true;
            }
            FloatingLong simulatedExtract = energyContainer.extract(energyRequirement, Action.SIMULATE, AutomationType.MANUAL);
            if (simulatedExtract.equals(energyRequirement)) {
                energyContainer.extract(energyRequirement, Action.EXECUTE, AutomationType.MANUAL);
                event.setCanceled(true);
                return true;
            }
        }
        return false;
    }

    @Nullable
    private static FallEnergyInfo getFallAbsorptionEnergyInfo(LivingEntity base) {
        ItemStack feetStack = base.getItemBySlot(EquipmentSlot.FEET);
        if (!feetStack.isEmpty()) {
            LazyOptional<IMekaGear> mekaGearLazyOptional = feetStack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY);
            if(mekaGearLazyOptional.isPresent()){
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

    public static void onRegisterCaps(RegisterCapabilitiesEvent event) {
        event.register(IMekaGear.class);
    }

    @OnlyIn(Dist.CLIENT)
    public static void handleItemToolTip(ItemTooltipEvent event){
        ItemStack stack = event.getItemStack();
        List<Component> components = event.getToolTip();
        if(stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent()){
            IMekaGear mekaGear = stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).orElseThrow(IllegalStateException::new);
            if (MekKeyHandler.isKeyPressed(MekanismKeyHandler.detailsKey)) {
                mekaGear.addModuleDetails(stack, components);
            } else {
                StorageUtils.addStoredEnergy(stack, components, true);
                if(mekaGear instanceof MekaArmorGearCapability mekaArmorGearCapability){
                    if (!mekaArmorGearCapability.getGasTankSpecs().isEmpty()) {
                        StorageUtils.addStoredGas(stack, components, true, false);
                    }
                    if (!mekaArmorGearCapability.getFluidTankSpecs().isEmpty()) {
                        StorageUtils.addStoredFluid(stack, components, true);
                    }
                }
                components.add(MekanismLang.HOLD_FOR_MODULES.translateColored(EnumColor.GRAY, EnumColor.INDIGO, MekanismKeyHandler.detailsKey.getTranslatedKeyMessage()));
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void onLoadAdditionalModel(ModelEvent.RegisterAdditional event) {
        MekaPlateModelCache.INSTANCE.setup(event);
    }

    @OnlyIn(Dist.CLIENT)
    public static void onModelBake(BakingCompleted event) {
        MekaPlateModelCache.INSTANCE.onBake(event);
    }

    @OnlyIn(Dist.CLIENT)
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            tickStart();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void tickStart() {
        MekanismClient.ticksPassed++;

        Minecraft minecraft = Minecraft.getInstance();


        if (minecraft.level != null && minecraft.player != null && !minecraft.isPaused()) {

            RadiationManager.get().tickClient(minecraft.player);

            UUID playerUUID = minecraft.player.getUUID();
            ItemStack jetpack = getActiveJetpack(minecraft.player);

            boolean jetpackInUse = isJetpackInUse(minecraft.player, jetpack);
            Mekanism.playerState.setJetpackState(playerUUID, jetpackInUse, true);

            if (!jetpack.isEmpty()) {
                ItemStack primaryJetpack = getPrimaryJetpack(minecraft.player);
                if (!primaryJetpack.isEmpty()) {
                    LazyOptional<IMekaGear> mekaGearLazyOptional = primaryJetpack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY);
                    if (mekaGearLazyOptional.isPresent()) {
                        IMekaGear mekaGear = mekaGearLazyOptional.orElseThrow(IllegalStateException::new);
                        IJetpackItem.JetpackMode primaryMode = ((IJetpackItem) mekaGear).getJetpackMode(primaryJetpack);
                        IJetpackItem.JetpackMode mode = IJetpackItem.getPlayerJetpackMode(minecraft.player, primaryMode, () -> minecraft.player.input.jumping);
                        MekanismClient.updateKey(minecraft.player.input.jumping, KeySync.ASCEND);
                        if (jetpackInUse && IJetpackItem.handleJetpackMotion(minecraft.player, mode, () -> minecraft.player.input.jumping)) {
                            minecraft.player.resetFallDistance();
                        }
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean isJetpackInUse(Player player, ItemStack jetpack) {
        Minecraft minecraft = Minecraft.getInstance();
        LazyOptional<IMekaGear> mekaGearLazyOptional = jetpack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY);
        if (!player.isSpectator() && !jetpack.isEmpty() && mekaGearLazyOptional.isPresent()) {
            IMekaGear mekaGear = mekaGearLazyOptional.orElseThrow(IllegalStateException::new);
            IJetpackItem.JetpackMode mode = ((IJetpackItem)mekaGear).getJetpackMode(jetpack);
            boolean guiOpen = minecraft.screen != null;
            boolean ascending = minecraft.player.input.jumping;
            boolean rising = ascending && !guiOpen;
            if (mode == IJetpackItem.JetpackMode.NORMAL) {
                return rising;
            }

            if (mode == IJetpackItem.JetpackMode.HOVER) {
                boolean descending = minecraft.player.input.shiftKeyDown;
                if (rising && !descending) {
                    return true;
                }

                return !CommonPlayerTickHandler.isOnGroundOrSleeping(player);
            }
        }

        return false;
    }
}
