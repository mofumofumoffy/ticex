package moffy.ticex.modifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanMap;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.NBTConstants;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.event.MekanismTeleportEvent;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.api.math.FloatingLong;
import mekanism.common.Mekanism;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.IBlastingItem;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.content.gear.Module;
import mekanism.common.content.gear.mekasuit.ModuleElytraUnit;
import mekanism.common.content.gear.mekasuit.ModuleJetpackUnit;
import mekanism.common.content.gear.mekatool.ModuleAttackAmplificationUnit;
import mekanism.common.content.gear.mekatool.ModuleExcavationEscalationUnit;
import mekanism.common.content.gear.mekatool.ModuleTeleportationUnit;
import mekanism.common.content.gear.mekatool.ModuleVeinMiningUnit;
import mekanism.common.item.gear.ItemAtomicDisassembler;
import mekanism.common.item.interfaces.IJetpackItem;
import mekanism.common.network.to_client.PacketPortalFX;
import mekanism.common.registries.MekanismGases;
import mekanism.common.registries.MekanismModules;
import mekanism.common.tags.MekanismTags;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.StorageUtils;
import moffy.ticex.TicEX;
import moffy.ticex.lib.TicEXTags;
import moffy.ticex.lib.hook.EnergyModifierHook;
import moffy.ticex.lib.modules.mekanism.MekaGearCapability;
import moffy.ticex.lib.hook.EmbossmentModifierHook;
import moffy.ticex.lib.hook.ProvidePropertyModifierHook;
import moffy.ticex.lib.modules.mekanism.interfaces.IGasTankItem;
import moffy.ticex.lib.modules.mekanism.interfaces.IMekaGear;
import moffy.ticex.lib.utils.TicEXMekanismWeaponsUtils;
import moffy.ticex.lib.utils.TicEXUtils;
import moffy.ticex.modifier.propeties.MekanicProperty;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ElytraFlightModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.EnchantmentModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolActionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ValidateModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.RequirementsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.*;
import slimeknights.tconstruct.library.modifiers.hook.mining.BlockBreakModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BlockHarvestModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.BowAmmoModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.item.armor.ModifiableArmorItem;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.data.ModifierIds;

public class ModifierMekanic extends NoLevelsModifier
        implements ProvidePropertyModifierHook,
        ToolActionModifierHook,
        UsingToolModifierHook,
        ToolDamageModifierHook,
        EntityInteractionModifierHook,
        BreakSpeedModifierHook,
        BlockHarvestModifierHook,
        MeleeDamageModifierHook,
        EnchantmentModifierHook,
        ElytraFlightModifierHook,
        InventoryTickModifierHook,
        BowAmmoModifierHook,
        ValidateModifierHook,
        RequirementsModifierHook,
        BlockInteractionModifierHook,


        EnergyModifierHook,
        EmbossmentModifierHook {

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(
                this,
                TicEXRegistry.PROPERTY_PROVIDER_HOOK,
                ModifierHooks.TOOL_USING,
                ModifierHooks.TOOL_DAMAGE,
                ModifierHooks.TOOL_ACTION,
                ModifierHooks.ENTITY_INTERACT,
                ModifierHooks.BREAK_SPEED,
                ModifierHooks.BLOCK_HARVEST,
                ModifierHooks.MELEE_DAMAGE,
                ModifierHooks.ENCHANTMENTS,
                ModifierHooks.ELYTRA_FLIGHT,
                ModifierHooks.INVENTORY_TICK,
                ModifierHooks.BOW_AMMO,
                ModifierHooks.VALIDATE,
                ModifierHooks.REQUIREMENTS,
                ModifierHooks.BLOCK_INTERACT,
                TicEXRegistry.ENERGY_HOOK,
                TicEXRegistry.EMBOSSMENT_HOOK
        );
    }

    @Override
    public @NotNull InteractionResult afterEntityUse(IToolStackView tool, ModifierEntry modifier, Player player, LivingEntity target, InteractionHand hand, InteractionSource source) {
        ItemStack stack = player.getItemInHand(hand);
        if(stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent()){
            IMekaGear mekaGear = stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).orElseThrow(IllegalStateException::new);
            for (Module<?> module : mekaGear.getModules(stack)) {
                if (module.isEnabled()) {
                    InteractionResult result = onModuleInteract(module, player, target, hand);
                    if (result != InteractionResult.PASS) {
                        return result;
                    }
                }
            }
        }
        teleport(tool, player);
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult beforeBlockUse(IToolStackView tool, ModifierEntry modifier, UseOnContext context, InteractionSource source) {
        ItemStack toolStack = context.getItemInHand();
        if(toolStack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent()){
            IMekaGear mekaGear = toolStack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).orElseThrow(IllegalStateException::new);
            for (Module<?> module : mekaGear.getModules(toolStack)) {
                if (module.isEnabled()) {
                    InteractionResult result = onModuleUse(module, context);
                    if (result != InteractionResult.PASS) {
                        return result;
                    }
                }
            }
        }
        return BlockInteractionModifierHook.super.beforeBlockUse(tool, modifier, context, source);
    }

    private <MODULE extends ICustomModule<MODULE>> InteractionResult onModuleUse(IModule<MODULE> module, UseOnContext context) {
        return module.getCustomInstance().onItemUse(module, context);
    }

    @Override
    public InteractionResult afterBlockUse(IToolStackView tool, ModifierEntry modifier, UseOnContext context, InteractionSource source) {
        teleport(tool, context.getPlayer());
        return BlockInteractionModifierHook.super.afterBlockUse(tool, modifier, context, source);
    }

    private void teleport(IToolStackView tool, Entity entity){
        if(tool instanceof ToolStack toolStack) {
            ItemStack stack = toolStack.createStack();
            if (stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent() && entity instanceof Player player) {
                IMekaGear mekaGear = stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).orElseThrow(IllegalStateException::new);
                if (!player.level().isClientSide()) {
                    IModule<ModuleTeleportationUnit> module = mekaGear.getModule(stack, MekanismModules.TELEPORTATION_UNIT);
                    if (module != null && module.isEnabled()) {
                        BlockHitResult result = MekanismUtils.rayTrace(player, MekanismConfig.gear.mekaToolMaxTeleportReach.get());
                        if (!module.getCustomInstance().requiresBlockTarget() || result.getType() != HitResult.Type.MISS) {
                            BlockPos pos = result.getBlockPos();
                            if (isValidDestinationBlock(player.level(), pos.above()) && isValidDestinationBlock(player.level(), pos.above(2))) {
                                double distance = player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ());
                                if (distance < 5) {
                                    return;
                                }
                                IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
                                FloatingLong energyNeeded = MekanismConfig.gear.mekaToolEnergyUsageTeleport.get().multiply(distance / 10D);
                                if (energyContainer == null || energyContainer.getEnergy().smallerThan(energyNeeded)) {
                                    return;
                                }
                                double targetX = pos.getX() + 0.5;
                                double targetY = pos.getY() + 1.5;
                                double targetZ = pos.getZ() + 0.5;
                                MekanismTeleportEvent.MekaTool event = new MekanismTeleportEvent.MekaTool(player, targetX, targetY, targetZ, stack, result);
                                if (MinecraftForge.EVENT_BUS.post(event)) {
                                    return;
                                }
                                Objects.requireNonNull(energyContainer).extract(energyNeeded, Action.EXECUTE, AutomationType.MANUAL);
                                if (player.isPassenger()) {
                                    player.dismountTo(targetX, targetY, targetZ);
                                } else {
                                    player.teleportTo(targetX, targetY, targetZ);
                                }
                                player.resetFallDistance();
                                Mekanism.packetHandler().sendToAllTracking(new PacketPortalFX(pos.above()), player.level(), pos);
                                player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
                            }
                        }
                    }
                }
            }
        }
    }

    private <MODULE extends ICustomModule<MODULE>> InteractionResult onModuleInteract(IModule<MODULE> module, @NotNull Player player, @NotNull LivingEntity entity,
                                                                                      @NotNull InteractionHand hand) {
        return module.getCustomInstance().onInteract(module, player, entity, hand);
    }


    @Override
    public BiFunction<Player, ItemStack, Map<String, Object>> getPropertyProvider() {
        return MekanicProperty.getProperties();
    }

    @Override
    public boolean canPerformAction(IToolStackView iToolStackView, ModifierEntry modifierEntry, ToolAction toolAction) {
        if(iToolStackView instanceof ToolStack toolStack) {
            ItemStack stack = toolStack.createStack();
            if (stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent()) {
                IMekaGear mekaGear = stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).orElseThrow(IllegalStateException::new);
                if (!(stack.getItem() instanceof ArmorItem) && ItemAtomicDisassembler.ALWAYS_SUPPORTED_ACTIONS.contains(toolAction)) {
                    return hasEnergyForDigAction(stack, mekaGear);
                }
                return mekaGear.getModules(stack).stream().anyMatch(module -> module.isEnabled() && canPerformAction(module, toolAction));
            }
        }
        return false;
    }

    private <MODULE extends ICustomModule<MODULE>> boolean canPerformAction(IModule<MODULE> module, ToolAction action) {
        return module.getCustomInstance().canPerformAction(module, action);
    }

    public boolean hasEnergyForDigAction(ItemStack stack, IMekaGear mekaGear) {
        IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
        if (energyContainer != null) {
            FloatingLong energyRequired = getDestroyEnergy(stack, 0, mekaGear.isModuleEnabled(stack, MekanismModules.SILK_TOUCH_UNIT));
            FloatingLong energyAvailable = energyContainer.getEnergy();
            return energyRequired.smallerOrEqual(energyAvailable) || !energyAvailable.divide(energyRequired).isZero();
        }
        return false;
    }

    @Override
    public int onDamageTool(IToolStackView iToolStackView, ModifierEntry modifierEntry, int i, @Nullable LivingEntity livingEntity) {
        return 0;
    }

    @Override
    public void onBreakSpeed(IToolStackView iToolStackView, ModifierEntry modifierEntry, PlayerEvent.BreakSpeed breakSpeed, Direction direction, boolean b, float v) {
        if(iToolStackView instanceof ToolStack toolStack){
            ItemStack stack = toolStack.createStack();
            if(stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent()){
                IMekaGear mekaGear = stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).orElseThrow(IllegalStateException::new);
                IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
                if (energyContainer == null) {
                    breakSpeed.setNewSpeed(0f);
                    return;
                }
                FloatingLong energyRequired = getDestroyEnergy(stack, breakSpeed.getState().destroySpeed, mekaGear.isModuleEnabled(stack, MekanismModules.SILK_TOUCH_UNIT));
                FloatingLong energyAvailable = Objects.requireNonNull(energyContainer).extract(energyRequired, Action.SIMULATE, AutomationType.MANUAL);
                if (energyAvailable.smallerThan(energyRequired)) {
                    breakSpeed.setNewSpeed(MekanismConfig.gear.mekaToolBaseEfficiency.get() * energyAvailable.divide(energyRequired).floatValue());
                }
                IModule<ModuleExcavationEscalationUnit> module = mekaGear.getModule(stack, MekanismModules.EXCAVATION_ESCALATION_UNIT);
                breakSpeed.setNewSpeed(module == null || !module.isEnabled() ? MekanismConfig.gear.mekaToolBaseEfficiency.get() : module.getCustomInstance().getEfficiency());
            }
        }
    }

    private FloatingLong getDestroyEnergy(boolean silk) {
        return silk ? MekanismConfig.gear.mekaToolEnergyUsageSilk.get() : MekanismConfig.gear.mekaToolEnergyUsage.get();
    }

    private FloatingLong getDestroyEnergy(ItemStack itemStack, float hardness, boolean silk) {
        return getDestroyEnergy(getDestroyEnergy(itemStack, silk), hardness);
    }

    private FloatingLong getDestroyEnergy(FloatingLong baseDestroyEnergy, float hardness) {
        return hardness == 0 ? baseDestroyEnergy.divide(2) : baseDestroyEnergy;
    }

    private FloatingLong getDestroyEnergy(ItemStack itemStack, boolean silk) {
        if(itemStack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent()){
            IMekaGear mekaGear = itemStack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).orElseThrow(IllegalStateException::new);
            FloatingLong destroyEnergy = getDestroyEnergy(silk);
            IModule<ModuleExcavationEscalationUnit> module = mekaGear.getModule(itemStack, MekanismModules.EXCAVATION_ESCALATION_UNIT);
            float efficiency = module == null || !module.isEnabled() ? MekanismConfig.gear.mekaToolBaseEfficiency.get() : module.getCustomInstance().getEfficiency();
            return destroyEnergy.multiply(efficiency);

        }
        return FloatingLong.create(0);
    }

    @Override
    public void startHarvest(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext toolHarvestContext) {
        BlockHarvestModifierHook.super.startHarvest(tool, modifier, toolHarvestContext);
        if(tool instanceof ToolStack toolStack){
            ItemStack stack = toolStack.createStack();
            if(stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent()){
                IMekaGear mekaGear = stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).orElseThrow(IllegalStateException::new);
                IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
                if (energyContainer != null) {
                    FloatingLong energyRequired = getDestroyEnergy(stack, toolHarvestContext.getState().getDestroySpeed(toolHarvestContext.getWorld(), toolHarvestContext.getPos()), mekaGear.isModuleEnabled(stack, MekanismModules.SILK_TOUCH_UNIT));
                    energyContainer.extract(energyRequired, Action.EXECUTE, AutomationType.MANUAL);
                }

                ServerPlayer player = toolHarvestContext.getPlayer();
                if(player != null){
                    if (player.level().isClientSide || player.isCreative()) {
                        return;
                    }
                    if (energyContainer != null) {
                        Level world = player.level();
                        BlockPos pos = toolHarvestContext.getPos();
                        BlockState state = world.getBlockState(pos);
                        boolean silk = mekaGear.isModuleEnabled(stack, MekanismModules.SILK_TOUCH_UNIT);
                        FloatingLong modDestroyEnergy = getDestroyEnergy(stack, silk);
                        FloatingLong energyRequired = getDestroyEnergy(modDestroyEnergy, state.getDestroySpeed(world, pos));
                        if (energyContainer.extract(energyRequired, Action.SIMULATE, AutomationType.MANUAL).greaterOrEqual(energyRequired) && mekaGear instanceof IBlastingItem blastingCapability) {
                            Map<BlockPos, BlockState> blocks = blastingCapability.getBlastedBlocks(world, player, stack, pos, state);
                            blocks = blocks.isEmpty() && ModuleVeinMiningUnit.canVeinBlock(state) ? Map.of(pos, state) : blocks;

                            Reference2BooleanMap<Block> oreTracker = blocks.values().stream().collect(Collectors.toMap(BlockBehaviour.BlockStateBase::getBlock,
                                    bs -> bs.is(MekanismTags.Blocks.ATOMIC_DISASSEMBLER_ORE), (l, r) -> l, Reference2BooleanArrayMap::new));

                            Object2IntMap<BlockPos> veinedBlocks = getVeinedBlocks(mekaGear, world, stack, blocks, oreTracker);
                            if (!veinedBlocks.isEmpty()) {
                                TicEX.LOGGER.info("{}", veinedBlocks.size());
                                FloatingLong baseDestroyEnergy = getDestroyEnergy(silk);
                                veinedBlocks.forEach((pos1, integer) -> {
                                    ToolHarvestLogic.breakExtraBlock(toolStack, stack, toolHarvestContext.forPosition(pos1, world.getBlockState(pos1)));
                                    energyContainer.extract(baseDestroyEnergy, Action.EXECUTE, AutomationType.MANUAL);
                                });
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void finishHarvest(IToolStackView iToolStackView, ModifierEntry modifierEntry, ToolHarvestContext toolHarvestContext, int i) {

    }

    private Object2IntMap<BlockPos> getVeinedBlocks(IMekaGear mekaGear, Level world, ItemStack stack, Map<BlockPos, BlockState> blocks, Reference2BooleanMap<Block> oreTracker) {
        IModule<ModuleVeinMiningUnit> veinMiningUnit = mekaGear.getModule(stack, MekanismModules.VEIN_MINING_UNIT);
        if (veinMiningUnit != null && veinMiningUnit.isEnabled()) {
            ModuleVeinMiningUnit customInstance = veinMiningUnit.getCustomInstance();
            return ModuleVeinMiningUnit.findPositions(world, blocks, customInstance.isExtended() ? customInstance.getExcavationRange() : 0, oreTracker);
        }
        return blocks.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, be -> 0, (l, r) -> l, Object2IntArrayMap::new));
    }

    @Override
    public float getMeleeDamage(IToolStackView iToolStackView, ModifierEntry modifierEntry, ToolAttackContext toolAttackContext, float v, float v1) {
        if(iToolStackView instanceof ToolStack toolStack){
            ItemStack stack = toolStack.createStack();
            if(stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent()){
                IMekaGear mekaGear = stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).orElseThrow(IllegalStateException::new);
                IModule<ModuleAttackAmplificationUnit> attackAmplificationUnit = mekaGear.getModule(stack, MekanismModules.ATTACK_AMPLIFICATION_UNIT);
                if (attackAmplificationUnit != null && attackAmplificationUnit.isEnabled()) {
                    int unitDamage = attackAmplificationUnit.getCustomInstance().getDamage();
                    if (unitDamage > 0) {
                        IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
                        if (energyContainer != null && !energyContainer.isEmpty()) {
                            energyContainer.extract(MekanismConfig.gear.mekaToolEnergyUsageWeapon.get().multiply(unitDamage / 4D), Action.EXECUTE, AutomationType.MANUAL);
                        }
                        FloatingLong energyCost = MekanismConfig.gear.mekaToolEnergyUsageWeapon.get().multiply(unitDamage / 4D);
                        FloatingLong energy = energyContainer == null ? FloatingLong.ZERO : energyContainer.getEnergy();
                        if (energy.smallerThan(energyCost)) {
                            double bonusDamage = unitDamage * energy.divideToLevel(energyCost);
                            if(bonusDamage > 0){
                                return v1 + (float) bonusDamage;
                            }
                        }
                    }
                }
                if(ModList.get().isLoaded("mekaweapons")){
                    return v1 * (TicEXMekanismWeaponsUtils.getAmplifier(stack) + 1);
                }
            }
        }
        return v1;
    }

    @Override
    public void onUsingTick(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
        if(ModList.get().isLoaded("mekaweapons")){
            TicEXMekanismWeaponsUtils.handleAutoFire(entity, tool, useDuration, timeLeft);
        }

    }

    private boolean isValidDestinationBlock(Level world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.isAir() || MekanismUtils.isLiquidBlock(blockState.getBlock());
    }

    @Override
    public int updateEnchantmentLevel(IToolStackView iToolStackView, ModifierEntry modifierEntry, Enchantment enchantment, int i) {
        if(iToolStackView instanceof ToolStack toolStack) {
            ItemStack stack = toolStack.createStack();
            if (stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent()) {
                ListTag enchantments = ItemDataUtils.getList(stack, NBTConstants.ENCHANTMENTS);
                return Math.max(MekanismUtils.getEnchantmentLevel(enchantments, enchantment), i);
            }
        }
        return i;
    }

    @Override
    public void updateEnchantments(IToolStackView iToolStackView, ModifierEntry modifierEntry, Map<Enchantment, Integer> map) {
        if(iToolStackView instanceof ToolStack toolStack) {
            ItemStack stack = toolStack.createStack();
            if (stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent()) {
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.deserializeEnchantments(ItemDataUtils.getList(stack, NBTConstants.ENCHANTMENTS));
                enchantments.forEach((enchantment, integer) -> map.merge(enchantment, integer, Math::max));
            }
        }
    }

    @Override
    public boolean elytraFlightTick(IToolStackView iToolStackView, ModifierEntry modifierEntry, LivingEntity livingEntity, int flightTicks) {
        if(iToolStackView instanceof ToolStack toolStack){
            ItemStack stack = toolStack.createStack();
            if(stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent()) {
                IMekaGear mekaGear = stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).orElseThrow(IllegalStateException::new);
                if (!livingEntity.level().isClientSide) {
                    int nextFlightTicks = flightTicks + 1;
                    if (nextFlightTicks % 10 == 0) {
                        if (nextFlightTicks % 20 == 0) {
                            IModule<ModuleElytraUnit> module = mekaGear.getModule(stack, MekanismModules.ELYTRA_UNIT);
                            if (module != null && module.isEnabled()) {
                                module.useEnergy(livingEntity, MekanismConfig.gear.mekaSuitElytraEnergyUsage.get());
                            }
                        }
                        livingEntity.gameEvent(GameEvent.ELYTRA_GLIDE);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onInventoryTick(IToolStackView iToolStackView, ModifierEntry modifierEntry, Level level, LivingEntity livingEntity, int i, boolean b, boolean b1, ItemStack itemStack) {
        if(iToolStackView instanceof ToolStack toolStack) {
            ItemStack stack = toolStack.createStack();
            if (stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent() && livingEntity instanceof Player player) {
                IMekaGear mekaGear = stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).orElseThrow(IllegalStateException::new);
                for (Module<?> module : mekaGear.getModules(stack)) {
                    module.tick(player);
                }
            }
        }

    }

    @Override
    public @NotNull ItemStack findAmmo(IToolStackView iToolStackView, ModifierEntry modifierEntry, LivingEntity livingEntity, ItemStack itemStack, Predicate<ItemStack> predicate) {
        if(ModList.get().isLoaded("mekaweapons") && iToolStackView instanceof ToolStack toolStack){
            ItemStack mekaArrowStack = new ItemStack(TicEXRegistry.MEKANIC_ARROW.get());
            mekaArrowStack.getOrCreateTag().put("shooterItem", toolStack.createStack().save(new CompoundTag()));
            return mekaArrowStack;
        }
        return itemStack;
    }

    @Override
    public boolean shouldDisplay(boolean advanced) {
        return advanced;
    }

    @Override
    public @NotNull List<ModifierEntry> displayModifiers(ModifierEntry entry) {
        List<ModifierEntry> entries = new ArrayList<>();
        /*if (entry.getLevel() == 1) {
            entries.add(new ModifierEntry(ModifierIds.reinforced, 5));
            entries.add(new ModifierEntry(ModifierIds.netherite, 1));
        }*/
        return entries;
    }

    @Override
    public Component validate(@NotNull IToolStackView tool, ModifierEntry entry) {
        /*if (
                        tool.getModifierLevel(ModifierIds.reinforced) < 5 || tool.getModifierLevel(ModifierIds.netherite) < 1

        ) {
            return Component.translatable("recipe.ticex.modifier.mekanic_requirements");
        }*/
        return null;
    }

    @Override
    public int receiveEnergy(IToolStackView tool, ItemStack stack, int received, boolean simulate) {
        return stack.getCapability(Capabilities.STRICT_ENERGY).map(iStrictEnergyHandler -> (int)iStrictEnergyHandler.insertEnergy(FloatingLong.create(received), simulate ? Action.SIMULATE : Action.EXECUTE).getValue()).orElse(0);
    }

    @Override
    public int extractEnergy(IToolStackView tool, ItemStack stack, int extracted, boolean simulate) {
        return stack.getCapability(Capabilities.STRICT_ENERGY).map(iStrictEnergyHandler -> (int)iStrictEnergyHandler.extractEnergy(FloatingLong.create(extracted), simulate ? Action.SIMULATE : Action.EXECUTE).getValue()).orElse(0);
    }

    @Override
    public int getEnergyStored(IToolStackView tool, ItemStack stack) {
        return stack.getCapability(Capabilities.STRICT_ENERGY).map(iStrictEnergyHandler -> (int)iStrictEnergyHandler.getEnergy(0).getValue()).orElse(0);
    }

    @Override
    public int getMaxEnergyStored(IToolStackView tool, ItemStack stack) {
        return stack.getCapability(Capabilities.STRICT_ENERGY).map(iStrictEnergyHandler -> (int)iStrictEnergyHandler.getMaxEnergy(0).getValue()).orElse(0);
    }

    @Override
    public boolean canExtract(IToolStackView tool, ItemStack stack) {
        return true;
    }

    @Override
    public boolean canReceive(IToolStackView tool, ItemStack stack) {
        return true;
    }

    @Override
    public boolean applyItem(EmbossmentContext context, int inputIndex, boolean secondary) {
        ToolStack toolStack = ToolStack.from(context.getToolStack());
        if(toolStack.getModifierLevel(TicEXRegistry.REBIRTH_MODIFIER.get()) <= 0){
            toolStack.addModifier(TicEXRegistry.REBIRTH_MODIFIER.getId(), 1);
        }
        return true;
    }
}
