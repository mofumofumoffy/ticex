package moffy.ticex.item.modifiable;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanMap;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.event.MekanismTeleportEvent;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.api.math.FloatingLong;
import mekanism.api.math.FloatingLongSupplier;
import mekanism.api.radial.RadialData;
import mekanism.api.radial.mode.IRadialMode;
import mekanism.api.radial.mode.NestedRadialMode;
import mekanism.api.text.EnumColor;
import mekanism.client.key.MekKeyHandler;
import mekanism.client.key.MekanismKeyHandler;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.ItemCapabilityWrapper;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.energy.item.RateLimitEnergyHandler;
import mekanism.common.config.MekanismConfig;
import mekanism.common.config.value.CachedFloatingLongValue;
import mekanism.common.content.gear.IBlastingItem;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.content.gear.Module;
import mekanism.common.content.gear.mekatool.*;
import mekanism.common.content.gear.shared.ModuleEnergyUnit;
import mekanism.common.item.gear.ItemAtomicDisassembler;
import mekanism.common.item.interfaces.IModeItem;
import mekanism.common.lib.attribute.AttributeCache;
import mekanism.common.lib.radial.IGenericRadialModeItem;
import mekanism.common.lib.radial.data.NestingRadialData;
import mekanism.common.network.to_client.PacketPortalFX;
import mekanism.common.registration.impl.CreativeTabDeferredRegister;
import mekanism.common.registries.MekanismModules;
import mekanism.common.tags.MekanismTags;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.StorageUtils;
import moffy.ticex.TicEX;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.mining.IsEffectiveToolHook;
import slimeknights.tconstruct.library.tools.definition.module.mining.MiningSpeedToolHook;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ModifiableMekaTool extends ModifiableItem implements CreativeTabDeferredRegister.ICustomCreativeTabContents, IModuleContainerItem, IBlastingItem, IGenericRadialModeItem, IModifiableMekItem {
    private static final FloatingLongSupplier chargeRateSupplier = MekanismConfig.gear.mekaToolBaseChargeRate;
    private static final FloatingLongSupplier maxEnergySupplier = MekanismConfig.gear.mekaToolBaseEnergyCapacity;
    private static final ResourceLocation RADIAL_ID = Mekanism.rl("meka_tool");
    private final Int2ObjectMap<AttributeCache> attributeCaches = new Int2ObjectArrayMap<>(ModuleAttackAmplificationUnit.AttackDamage.values().length);

    public ModifiableMekaTool(Properties properties) {
        super(properties, TicEXRegistry.MEKA_TOOL_DEFINITION);
    }

    public boolean areCapabilityConfigsLoaded() {
        return MekanismConfig.gear.isLoaded();
    }

    @Override
    public void gatherCapabilities(List<ItemCapabilityWrapper.ItemCapability> capabilities, ItemStack stack) {
        capabilities.add(RateLimitEnergyHandler.create(() -> this.getChargeRate(stack), () -> this.getMaxEnergy(stack), BasicEnergyContainer.manualOnly, BasicEnergyContainer.alwaysTrue));
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ToolCapabilityProvider(stack);
    }

    public boolean isBarVisible(@NotNull ItemStack stack) {
        return true;
    }

    public int getBarWidth(@NotNull ItemStack stack) {
        return StorageUtils.getEnergyBarWidth(stack);
    }

    public int getBarColor(@NotNull ItemStack stack) {
        return MekanismConfig.client.energyColor.get();
    }

    public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        TooltipUtil.addInformation(this, stack, world, tooltip, SafeClientAccess.getTooltipKey(), flag);
        if (MekKeyHandler.isKeyPressed(MekanismKeyHandler.detailsKey)) {
            this.addModuleDetails(stack, tooltip);
        } else {
            StorageUtils.addStoredEnergy(stack, tooltip, true);
            tooltip.add(MekanismLang.HOLD_FOR_MODULES.translateColored(EnumColor.GRAY, EnumColor.INDIGO, MekanismKeyHandler.detailsKey.getTranslatedKeyMessage()));
        }

    }

    public void addItems(CreativeModeTab.Output tabOutput) {
        if (chargeRateSupplier instanceof CachedFloatingLongValue configValue) {
            tabOutput.accept(StorageUtils.getFilledEnergyVariant(new ItemStack(this), configValue));
        } else {
            tabOutput.accept(StorageUtils.getFilledEnergyVariant(new ItemStack(this), maxEnergySupplier.get()));
        }

    }

    protected FloatingLong getChargeRate(ItemStack stack) {
        IModule<ModuleEnergyUnit> module = this.getModule(stack, MekanismModules.ENERGY_UNIT);
        return module == null ? chargeRateSupplier.get() : module.getCustomInstance().getChargeRate(module);
    }

    protected FloatingLong getMaxEnergy(ItemStack stack) {
        IModule<ModuleEnergyUnit> module = this.getModule(stack, MekanismModules.ENERGY_UNIT);
        return module == null ? maxEnergySupplier.get() : module.getCustomInstance().getEnergyCapacity(module);
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }

    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }

    public boolean isCorrectToolForDrops(@NotNull BlockState state) {
        return true;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return true;
    }

    public boolean canPerformAction(ItemStack stack, ToolAction action) {
        return super.canPerformAction(stack, action) || ItemAtomicDisassembler.ALWAYS_SUPPORTED_ACTIONS.contains(action) ? this.hasEnergyForDigAction(stack) : this.getModules(stack).stream().anyMatch((module) -> module.isEnabled() && this.canPerformAction((IModule<?>)module, action));
    }

    private <MODULE extends ICustomModule<MODULE>> boolean canPerformAction(IModule<MODULE> module, ToolAction action) {
        return module.getCustomInstance().canPerformAction(module, action);
    }

    public boolean hasEnergyForDigAction(ItemStack stack) {
        IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
        if (energyContainer == null) {
            return false;
        } else {
            FloatingLong energyRequired = this.getDestroyEnergy(stack, 0.0F, this.isModuleEnabled(stack, MekanismModules.SILK_TOUCH_UNIT));
            FloatingLong energyAvailable = energyContainer.getEnergy();
            return energyRequired.smallerOrEqual(energyAvailable) || !energyAvailable.divide(energyRequired).isZero();
        }
    }

    public boolean isNotReplaceableByPickAction(ItemStack stack, Player player, int inventorySlot) {
        return super.isNotReplaceableByPickAction(stack, player, inventorySlot) || ItemDataUtils.hasData(stack, "modules", 10);
    }

    public int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
        if (stack.isEmpty()) {
            return 0;
        } else {
            ListTag enchantments = ItemDataUtils.getList(stack, "Enchantments");
            return Math.max(MekanismUtils.getEnchantmentLevel(enchantments, enchantment), super.getEnchantmentLevel(stack, enchantment));
        }
    }

    public Map<Enchantment, Integer> getAllEnchantments(ItemStack stack) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.deserializeEnchantments(ItemDataUtils.getList(stack, "Enchantments"));
        super.getAllEnchantments(stack).forEach((enchantment, level) -> enchantments.merge(enchantment, level, Math::max));
        return enchantments;
    }

    public @NotNull InteractionResult useOn(UseOnContext context) {
        for(mekanism.common.content.gear.Module<?> module : this.getModules(context.getItemInHand())) {
            if (module.isEnabled()) {
                InteractionResult result = this.onModuleUse(module, context);
                if (result != InteractionResult.PASS) {
                    return result;
                }
            }
        }

        return super.useOn(context);
    }

    private <MODULE extends ICustomModule<MODULE>> InteractionResult onModuleUse(IModule<MODULE> module, UseOnContext context) {
        return module.getCustomInstance().onItemUse(module, context);
    }

    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player, @NotNull LivingEntity entity, @NotNull InteractionHand hand) {
        for(mekanism.common.content.gear.Module<?> module : this.getModules(stack)) {
            if (module.isEnabled()) {
                InteractionResult result = this.onModuleInteract(module, player, entity, hand);
                if (result != InteractionResult.PASS) {
                    return result;
                }
            }
        }

        return super.interactLivingEntity(stack, player, entity, hand);
    }

    private <MODULE extends ICustomModule<MODULE>> InteractionResult onModuleInteract(IModule<MODULE> module, @NotNull Player player, @NotNull LivingEntity entity, @NotNull InteractionHand hand) {
        return module.getCustomInstance().onInteract(module, player, entity, hand);
    }

    public float getMekDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
        if (energyContainer == null) {
            return 0.0F;
        } else {
            FloatingLong energyRequired = this.getDestroyEnergy(stack, state.getDestroySpeed(null, null), this.isModuleEnabled(stack, MekanismModules.SILK_TOUCH_UNIT));
            FloatingLong energyAvailable = energyContainer.extract(energyRequired, Action.SIMULATE, AutomationType.MANUAL);
            if (energyAvailable.smallerThan(energyRequired)) {
                return MekanismConfig.gear.mekaToolBaseEfficiency.get() * energyAvailable.divide(energyRequired).floatValue();
            } else {
                IModule<ModuleExcavationEscalationUnit> module = this.getModule(stack, MekanismModules.EXCAVATION_ESCALATION_UNIT);
                return module != null && module.isEnabled() ? module.getCustomInstance().getEfficiency() : MekanismConfig.gear.mekaToolBaseEfficiency.get();
            }
        }
    }

    public float getTiCDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
         if (!stack.hasTag()) {
             return 1.0F;
         }
         ToolStack tool = ToolStack.from(stack);
         if (tool.isBroken()) {
            return 0.3F;
        } else {
            return Math.max(1.0F, tool.getHook(ToolHooks.MINING_SPEED).modifyDestroySpeed(tool, state, tool.getStats().get(ToolStats.MINING_SPEED)));
        }
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        return getTiCDestroySpeed(stack, state) + getMekDestroySpeed(stack, state);
    }

    public boolean mineBlock(@NotNull ItemStack stack, @NotNull Level world, @NotNull BlockState state, @NotNull BlockPos pos, @NotNull LivingEntity entityliving) {
        IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
        if (energyContainer != null) {
            FloatingLong energyRequired = this.getDestroyEnergy(stack, state.getDestroySpeed(world, pos), this.isModuleEnabled(stack, MekanismModules.SILK_TOUCH_UNIT));
            energyContainer.extract(energyRequired, Action.EXECUTE, AutomationType.MANUAL);
        }

        return true;
    }

    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        IModule<ModuleAttackAmplificationUnit> attackAmplificationUnit = this.getModule(stack, MekanismModules.ATTACK_AMPLIFICATION_UNIT);
        if (attackAmplificationUnit != null && attackAmplificationUnit.isEnabled()) {
            int unitDamage = attackAmplificationUnit.getCustomInstance().getDamage();
            if (unitDamage > 0) {
                IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
                if (energyContainer != null && !energyContainer.isEmpty()) {
                    energyContainer.extract(MekanismConfig.gear.mekaToolEnergyUsageWeapon.get().multiply((double)unitDamage / (double)4.0F), Action.EXECUTE, AutomationType.MANUAL);
                }
            }
        }

        return true;
    }

    public Map<BlockPos, BlockState> getBlastedBlocks(Level world, Player player, ItemStack stack, BlockPos pos, BlockState state) {
        if (!player.isShiftKeyDown()) {
            IModule<ModuleBlastingUnit> blastingUnit = this.getModule(stack, MekanismModules.BLASTING_UNIT);
            if (blastingUnit != null && blastingUnit.isEnabled()) {
                int radius = blastingUnit.getCustomInstance().getBlastRadius();
                if (radius > 0 && IBlastingItem.canBlastBlock(world, pos, state)) {
                    return IBlastingItem.findPositions(world, pos, player, radius);
                }
            }
        }

        return Collections.emptyMap();
    }

    private Object2IntMap<BlockPos> getVeinedBlocks(Level world, ItemStack stack, Map<BlockPos, BlockState> blocks, Reference2BooleanMap<Block> oreTracker) {
        IModule<ModuleVeinMiningUnit> veinMiningUnit = this.getModule(stack, MekanismModules.VEIN_MINING_UNIT);
        if (veinMiningUnit != null && veinMiningUnit.isEnabled()) {
            ModuleVeinMiningUnit customInstance = veinMiningUnit.getCustomInstance();
            return ModuleVeinMiningUnit.findPositions(world, blocks, customInstance.isExtended() ? customInstance.getExcavationRange() : 0, oreTracker);
        } else {
            return blocks.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (be) -> 0, (l, r) -> l, Object2IntArrayMap::new));
        }
    }

    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
        if (!player.level().isClientSide && !player.isCreative()) {
            IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
            if (energyContainer != null) {
                Level world = player.level();
                BlockState state = world.getBlockState(pos);
                boolean silk = this.isModuleEnabled(stack, MekanismModules.SILK_TOUCH_UNIT);
                FloatingLong modDestroyEnergy = this.getDestroyEnergy(stack, silk);
                FloatingLong energyRequired = this.getDestroyEnergy(modDestroyEnergy, state.getDestroySpeed(world, pos));
                if (energyContainer.extract(energyRequired, Action.SIMULATE, AutomationType.MANUAL).greaterOrEqual(energyRequired)) {
                    Map<BlockPos, BlockState> blocks = this.getBlastedBlocks(world, player, stack, pos, state);
                    blocks = blocks.isEmpty() && ModuleVeinMiningUnit.canVeinBlock(state) ? Map.of(pos, state) : blocks;
                    Reference2BooleanMap<Block> oreTracker = blocks.values().stream().collect(Collectors.toMap(BlockBehaviour.BlockStateBase::getBlock, (bs) -> bs.is(MekanismTags.Blocks.ATOMIC_DISASSEMBLER_ORE), (l, r) -> l, Reference2BooleanArrayMap::new));
                    Object2IntMap<BlockPos> veinedBlocks = this.getVeinedBlocks(world, stack, blocks, oreTracker);
                    if (!veinedBlocks.isEmpty()) {
                        FloatingLong baseDestroyEnergy = this.getDestroyEnergy(silk);
                        MekanismUtils.veinMineArea(energyContainer, energyRequired, world, pos, (ServerPlayer)player, stack, this, veinedBlocks, (hardness) -> this.getDestroyEnergy(modDestroyEnergy, hardness), (hardness, distance, bs) -> this.getDestroyEnergy(baseDestroyEnergy, hardness).multiply((double)0.5F * Math.pow(distance, oreTracker.getBoolean(bs.getBlock()) ? (double)1.5F : (double)2.0F)));
                    }
                }
            }

            return super.onBlockStartBreak(stack, pos, player);
        } else {
            return super.onBlockStartBreak(stack, pos, player);
        }
    }

    private FloatingLong getDestroyEnergy(boolean silk) {
        return silk ? MekanismConfig.gear.mekaToolEnergyUsageSilk.get() : MekanismConfig.gear.mekaToolEnergyUsage.get();
    }

    public FloatingLong getDestroyEnergy(ItemStack itemStack, float hardness, boolean silk) {
        return this.getDestroyEnergy(this.getDestroyEnergy(itemStack, silk), hardness);
    }

    private FloatingLong getDestroyEnergy(FloatingLong baseDestroyEnergy, float hardness) {
        return hardness == 0.0F ? baseDestroyEnergy.divide(2L) : baseDestroyEnergy;
    }

    private FloatingLong getDestroyEnergy(ItemStack itemStack, boolean silk) {
        FloatingLong destroyEnergy = this.getDestroyEnergy(silk);
        IModule<ModuleExcavationEscalationUnit> module = this.getModule(itemStack, MekanismModules.EXCAVATION_ESCALATION_UNIT);
        float efficiency = module != null && module.isEnabled() ? module.getCustomInstance().getEfficiency() : MekanismConfig.gear.mekaToolBaseEfficiency.get();
        return destroyEnergy.multiply(efficiency);
    }

    public @NotNull Multimap<Attribute, AttributeModifier> getMekanismAttributeModifiers(@NotNull EquipmentSlot slot, @NotNull ItemStack stack) {
        if (slot == EquipmentSlot.MAINHAND) {
            int unitDamage = 0;
            IModule<ModuleAttackAmplificationUnit> attackAmplificationUnit = this.getModule(stack, MekanismModules.ATTACK_AMPLIFICATION_UNIT);
            if (attackAmplificationUnit != null && attackAmplificationUnit.isEnabled()) {
                unitDamage = attackAmplificationUnit.getCustomInstance().getDamage();
                if (unitDamage > 0) {
                    FloatingLong energyCost = MekanismConfig.gear.mekaToolEnergyUsageWeapon.get().multiply((double)unitDamage / (double)4.0F);
                    IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
                    FloatingLong energy = energyContainer == null ? FloatingLong.ZERO : energyContainer.getEnergy();
                    if (energy.smallerThan(energyCost)) {
                        double bonusDamage = (double)unitDamage * energy.divideToLevel(energyCost);
                        if (bonusDamage > (double)0.0F) {
                            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
                            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)MekanismConfig.gear.mekaToolBaseDamage.get() + bonusDamage, AttributeModifier.Operation.ADDITION));
                            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", MekanismConfig.gear.mekaToolAttackSpeed.get(), AttributeModifier.Operation.ADDITION));
                            return builder.build();
                        }

                        unitDamage = 0;
                    }
                }
            }

            return this.attributeCaches.computeIfAbsent(unitDamage, (damage) -> new AttributeCache((builder) -> {
                builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", MekanismConfig.gear.mekaToolBaseDamage.get() + damage, AttributeModifier.Operation.ADDITION));
                builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", MekanismConfig.gear.mekaToolAttackSpeed.get(), AttributeModifier.Operation.ADDITION));
            }, MekanismConfig.gear.mekaToolBaseDamage, MekanismConfig.gear.mekaToolAttackSpeed)).get();
        } else {
            return super.getAttributeModifiers(slot, stack);
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> mekanismMap = getMekanismAttributeModifiers(slot, stack);
        Multimap<Attribute, AttributeModifier> tconMap = super.getAttributeModifiers(slot, stack);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();

        double totalAttackDamage = 0;
        for (AttributeModifier mod : mekanismMap.get(Attributes.ATTACK_DAMAGE)) {
            if (mod.getOperation() == AttributeModifier.Operation.ADDITION && mod.getId().equals(BASE_ATTACK_DAMAGE_UUID)) {
                totalAttackDamage += mod.getAmount();
            }
        }
        for (AttributeModifier mod : tconMap.get(Attributes.ATTACK_DAMAGE)) {
            if (mod.getOperation() == AttributeModifier.Operation.ADDITION && mod.getId().equals(BASE_ATTACK_DAMAGE_UUID)) {
                totalAttackDamage += mod.getAmount();
            }
        }
        if (totalAttackDamage != 0) {
            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                    BASE_ATTACK_DAMAGE_UUID,
                    "Merged attack damage",
                    totalAttackDamage,
                    AttributeModifier.Operation.ADDITION));
        }

        double tconSpeed = 0;
        double mekSpeed = 0;
        for (AttributeModifier mod : tconMap.get(Attributes.ATTACK_SPEED)) {
            if (mod.getOperation() == AttributeModifier.Operation.ADDITION && mod.getId().equals(BASE_ATTACK_SPEED_UUID)) {
                tconSpeed = mod.getAmount();
                break;
            }
        }
        for (AttributeModifier mod : mekanismMap.get(Attributes.ATTACK_SPEED)) {
            if (mod.getOperation() == AttributeModifier.Operation.ADDITION && mod.getId().equals(BASE_ATTACK_SPEED_UUID)) {
                mekSpeed = mod.getAmount();
                break;
            }
        }
        double maxSpeed = Math.max(tconSpeed, mekSpeed);
        if (maxSpeed != 0) {
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(
                    BASE_ATTACK_SPEED_UUID,
                    "Merged attack speed",
                    maxSpeed,
                    AttributeModifier.Operation.ADDITION));
        }

        return builder.build();
    }

    public @NotNull InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide()) {
            IModule<ModuleTeleportationUnit> module = this.getModule(stack, MekanismModules.TELEPORTATION_UNIT);
            if (module != null && module.isEnabled()) {
                BlockHitResult result = MekanismUtils.rayTrace(player, MekanismConfig.gear.mekaToolMaxTeleportReach.get());
                if (!module.getCustomInstance().requiresBlockTarget() || result.getType() != HitResult.Type.MISS) {
                    BlockPos pos = result.getBlockPos();
                    if (this.isValidDestinationBlock(world, pos.above()) && this.isValidDestinationBlock(world, pos.above(2))) {
                        double distance = player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ());
                        if (distance < (double)5.0F) {
                            return InteractionResultHolder.pass(stack);
                        }

                        IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
                        FloatingLong energyNeeded = MekanismConfig.gear.mekaToolEnergyUsageTeleport.get().multiply(distance / (double)10.0F);
                        if (energyContainer != null && !energyContainer.getEnergy().smallerThan(energyNeeded)) {
                            double targetX = (double)pos.getX() + (double)0.5F;
                            double targetY = (double)pos.getY() + (double)1.5F;
                            double targetZ = (double)pos.getZ() + (double)0.5F;
                            MekanismTeleportEvent.MekaTool event = new MekanismTeleportEvent.MekaTool(player, targetX, targetY, targetZ, stack, result);
                            if (MinecraftForge.EVENT_BUS.post(event)) {
                                return InteractionResultHolder.fail(stack);
                            }

                            energyContainer.extract(energyNeeded, Action.EXECUTE, AutomationType.MANUAL);
                            if (player.isPassenger()) {
                                player.dismountTo(targetX, targetY, targetZ);
                            } else {
                                player.teleportTo(targetX, targetY, targetZ);
                            }

                            player.resetFallDistance();
                            Mekanism.packetHandler().sendToAllTracking(new PacketPortalFX(pos.above()), world, pos);
                            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
                            return InteractionResultHolder.success(stack);
                        }

                        return InteractionResultHolder.fail(stack);
                    }
                }
            }
        }

        return InteractionResultHolder.pass(stack);
    }

    private boolean isValidDestinationBlock(Level world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.isAir() || MekanismUtils.isLiquidBlock(blockState.getBlock());
    }

    public boolean isEnchantable(@NotNull ItemStack stack) {
        return false;
    }

    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return false;
    }

    public boolean supportsSlotType(ItemStack stack, @NotNull EquipmentSlot slotType) {
        return IGenericRadialModeItem.super.supportsSlotType(stack, slotType) && this.getModules(stack).stream().anyMatch(mekanism.common.content.gear.Module::handlesAnyModeChange);
    }

    public @Nullable Component getScrollTextComponent(@NotNull ItemStack stack) {
        return this.getModules(stack).stream().filter(Module::handlesModeChange).findFirst().map((module) -> module.getModeScrollComponent(stack)).orElse(null);
    }

    public void changeMode(@NotNull Player player, @NotNull ItemStack stack, int shift, IModeItem.DisplayChange displayChange) {
        for(mekanism.common.content.gear.Module<?> module : this.getModules(stack)) {
            if (module.handlesModeChange()) {
                module.changeMode(player, stack, shift, displayChange);
                return;
            }
        }

    }

    public @Nullable RadialData<?> getRadialData(ItemStack stack) {
        List<NestedRadialMode> nestedModes = new ArrayList<>();
        Objects.requireNonNull(nestedModes);
        Consumer<NestedRadialMode> adder = nestedModes::add;

        for(mekanism.common.content.gear.Module<?> module : this.getModules(stack)) {
            if (module.handlesRadialModeChange()) {
                module.addRadialModes(stack, adder);
            }
        }

        if (nestedModes.isEmpty()) {
            return null;
        } else if (nestedModes.size() == 1) {
            return nestedModes.get(0).nestedData();
        } else {
            return new NestingRadialData(RADIAL_ID, nestedModes);
        }
    }

    public <M extends IRadialMode> @Nullable M getMode(ItemStack stack, RadialData<M> radialData) {
        for(mekanism.common.content.gear.Module<?> module : this.getModules(stack)) {
            if (module.handlesRadialModeChange()) {
                M mode = module.getMode(stack, radialData);
                if (mode != null) {
                    return mode;
                }
            }
        }

        return null;
    }

    public <M extends IRadialMode> void setMode(ItemStack stack, Player player, RadialData<M> radialData, M mode) {
        for(Module<?> module : this.getModules(stack)) {
            if (module.handlesRadialModeChange() && module.setMode(player, stack, radialData, mode)) {
                return;
            }
        }

    }

    @Override
    public Component getName(ItemStack stack) {
        return MutableComponent.create(super.getName(stack).getContents()).withStyle(ChatFormatting.LIGHT_PURPLE);
    }
}
