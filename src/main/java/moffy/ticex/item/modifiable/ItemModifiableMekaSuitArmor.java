package moffy.ticex.item.modifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableMultimap.Builder;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.NBTConstants;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.ICustomModule.ModuleDamageAbsorbInfo;
import mekanism.api.gear.IModule;
import mekanism.api.gear.ModuleData.ExclusiveFlag;
import mekanism.api.math.FloatingLong;
import mekanism.api.math.FloatingLongSupplier;
import mekanism.api.text.EnumColor;
import mekanism.client.key.MekKeyHandler;
import mekanism.client.key.MekanismKeyHandler;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.ItemCapabilityWrapper.ItemCapability;
import mekanism.common.capabilities.chemical.item.ChemicalTankSpec;
import mekanism.common.capabilities.chemical.item.RateLimitMultiTankGasHandler;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.energy.item.RateLimitEnergyHandler;
import mekanism.common.capabilities.fluid.item.RateLimitMultiTankFluidHandler;
import mekanism.common.capabilities.fluid.item.RateLimitMultiTankFluidHandler.FluidTankSpec;
import mekanism.common.capabilities.laser.item.LaserDissipationHandler;
import mekanism.common.capabilities.radiation.item.RadiationShieldingHandler;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.content.gear.mekasuit.ModuleElytraUnit;
import mekanism.common.content.gear.mekasuit.ModuleJetpackUnit;
import mekanism.common.content.gear.shared.ModuleEnergyUnit;
import mekanism.common.integration.gender.GenderCapabilityHelper;
import mekanism.common.item.gear.ItemHazmatSuitArmor;
import mekanism.common.item.interfaces.IJetpackItem;
import mekanism.common.item.interfaces.IModeItem;
import mekanism.common.lib.attribute.IAttributeRefresher;
import mekanism.common.registries.MekanismFluids;
import mekanism.common.registries.MekanismGases;
import mekanism.common.registries.MekanismModules;
import mekanism.common.tags.MekanismTags;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.StorageUtils;
import moffy.ticex.client.mekanism.MekaPlateDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.item.armor.MultilayerArmorItem;

public class ItemModifiableMekaSuitArmor extends MultilayerArmorItem implements IModifiableMekItem, IModuleContainerItem, IModeItem, IJetpackItem, IAttributeRefresher{

    private final List<ChemicalTankSpec<Gas>> gasTankSpecs = new ArrayList<>();
    private final List<ChemicalTankSpec<Gas>> gasTankSpecsView = Collections.unmodifiableList(gasTankSpecs);
    private final List<FluidTankSpec> fluidTankSpecs = new ArrayList<>();
    private final List<FluidTankSpec> fluidTankSpecsView = Collections.unmodifiableList(fluidTankSpecs);
    private final float absorption;
    private final double laserDissipation;
    private final double laserRefraction;

    private final ResourceLocation name;

    public ItemModifiableMekaSuitArmor(ModifiableArmorMaterial material, ArmorItem.Type slot, Item.Properties properties) {
        super(material, slot, properties);
        switch (slot) {
            case HELMET -> {
                fluidTankSpecs.add(FluidTankSpec.createFillOnly(MekanismConfig.gear.mekaSuitNutritionalTransferRate, MekanismConfig.gear.mekaSuitNutritionalMaxStorage,
                      fluid -> fluid.getFluid() == MekanismFluids.NUTRITIONAL_PASTE.getFluid(), stack -> hasModule(stack, MekanismModules.NUTRITIONAL_INJECTION_UNIT)));
                absorption = 0.15F;
                laserDissipation = 0.15;
                laserRefraction = 0.2;
            }
            case CHESTPLATE -> {
                gasTankSpecs.add(ChemicalTankSpec.createFillOnly(MekanismConfig.gear.mekaSuitJetpackTransferRate, MekanismConfig.gear.mekaSuitJetpackMaxStorage,
                      gas -> gas == MekanismGases.HYDROGEN.get(), stack -> hasModule(stack, MekanismModules.JETPACK_UNIT)));
                absorption = 0.4F;
                laserDissipation = 0.3;
                laserRefraction = 0.4;
            }
            case LEGGINGS -> {
                absorption = 0.3F;
                laserDissipation = 0.1875;
                laserRefraction = 0.25;
            }
            case BOOTS -> {
                absorption = 0.15F;
                laserDissipation = 0.1125;
                laserRefraction = 0.15;
            }
            default -> throw new IllegalArgumentException("Unknown Equipment Slot Type");
        }
        this.name = material.getId();
    }

    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new MekaPlateDispatcher(){
            @Override
            protected ResourceLocation getName() {
                return name;
            }
        });
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        
        return 0;
    }

    @Override
    public Component getName(ItemStack stack) {
        return MutableComponent.create(super.getName(stack).getContents()).withStyle(ChatFormatting.LIGHT_PURPLE);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        if (MekKeyHandler.isKeyPressed(MekanismKeyHandler.detailsKey)) {
            addModuleDetails(stack, tooltip);
        } else {
            StorageUtils.addStoredEnergy(stack, tooltip, true);
            if (!gasTankSpecs.isEmpty()) {
                StorageUtils.addStoredGas(stack, tooltip, true, false);
            }
            if (!fluidTankSpecs.isEmpty()) {
                StorageUtils.addStoredFluid(stack, tooltip, true);
            }
            tooltip.add(MekanismLang.HOLD_FOR_MODULES.translateColored(EnumColor.GRAY, EnumColor.INDIGO, MekanismKeyHandler.detailsKey.getTranslatedKeyMessage()));
        }
    }

    @Override
    public boolean makesPiglinsNeutral(@NotNull ItemStack stack, @NotNull LivingEntity wearer) {
        return true;
    }

    @Override
    public boolean isEnderMask(@NotNull ItemStack stack, @NotNull Player player, @NotNull EnderMan enderman) {
        return type == ArmorItem.Type.HELMET;
    }

    @Override
    public boolean canWalkOnPowderedSnow(@NotNull ItemStack stack, @NotNull LivingEntity wearer) {
        return type == ArmorItem.Type.BOOTS;
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return StorageUtils.getEnergyBarWidth(stack);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return MekanismConfig.client.energyColor.get();
    }

    @Override
    public boolean isNotReplaceableByPickAction(ItemStack stack, Player player, int inventorySlot) {
        
        return super.isNotReplaceableByPickAction(stack, player, inventorySlot) || ItemDataUtils.hasData(stack, NBTConstants.MODULES, Tag.TAG_COMPOUND);
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
        if (stack.isEmpty()) {
            return 0;
        }
        
        ListTag enchantments = ItemDataUtils.getList(stack, NBTConstants.ENCHANTMENTS);
        return Math.max(MekanismUtils.getEnchantmentLevel(enchantments, enchantment), super.getEnchantmentLevel(stack, enchantment));
    }

    @Override
    public Map<Enchantment, Integer> getAllEnchantments(ItemStack stack) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.deserializeEnchantments(ItemDataUtils.getList(stack, NBTConstants.ENCHANTMENTS));
        super.getAllEnchantments(stack).forEach((enchantment, level) -> enchantments.merge(enchantment, level, Math::max));
        return enchantments;
    }

    @SuppressWarnings("removal")
    @Override
    public void onArmorTick(ItemStack stack, Level world, Player player) {
        super.onArmorTick(stack, world, player);
        for (mekanism.common.content.gear.Module<?> module : getModules(stack)) {
            module.tick(player);
        }
    }

    @Override
    public boolean areCapabilityConfigsLoaded() {
        return MekanismConfig.gear.isLoaded();
    }

    @Override
    public void gatherCapabilities(List<ItemCapability> capabilities, ItemStack stack) {
        GenderCapabilityHelper.addGenderCapability(this, capabilities::add);
        
        
        capabilities.add(RateLimitEnergyHandler.create(() -> getChargeRate(stack), () -> getMaxEnergy(stack), BasicEnergyContainer.manualOnly,
              BasicEnergyContainer.alwaysTrue));
        capabilities.add(RadiationShieldingHandler.create(item -> isModuleEnabled(item, MekanismModules.RADIATION_SHIELDING_UNIT) ?
                                                                  ItemHazmatSuitArmor.getShieldingByArmor(getType()) : 0));
        capabilities.add(LaserDissipationHandler.create(item -> isModuleEnabled(item, MekanismModules.LASER_DISSIPATION_UNIT) ? laserDissipation : 0,
              item -> isModuleEnabled(item, MekanismModules.LASER_DISSIPATION_UNIT) ? laserRefraction : 0));
        if (!gasTankSpecs.isEmpty()) {
            capabilities.add(RateLimitMultiTankGasHandler.create(gasTankSpecs));
        }
        if (!fluidTankSpecs.isEmpty()) {
            capabilities.add(RateLimitMultiTankFluidHandler.create(fluidTankSpecs));
        }
    }

    public List<ChemicalTankSpec<Gas>> getGasTankSpecs() {
        return gasTankSpecsView;
    }

    public List<FluidTankSpec> getFluidTankSpecs() {
        return fluidTankSpecsView;
    }

    @NotNull
    public GasStack useGas(ItemStack stack, Gas type, long amount) {
        Optional<IGasHandler> capability = stack.getCapability(Capabilities.GAS_HANDLER).resolve();
        if (capability.isPresent()) {
            IGasHandler gasHandlerItem = capability.get();
            return gasHandlerItem.extractChemical(new GasStack(type, amount), Action.EXECUTE);
        }
        return GasStack.EMPTY;
    }

    public GasStack getContainedGas(ItemStack stack, Gas type) {
        Optional<IGasHandler> capability = stack.getCapability(Capabilities.GAS_HANDLER).resolve();
        if (capability.isPresent()) {
            IGasHandler gasHandlerItem = capability.get();
            for (int i = 0; i < gasHandlerItem.getTanks(); i++) {
                GasStack gasInTank = gasHandlerItem.getChemicalInTank(i);
                if (gasInTank.getType() == type) {
                    return gasInTank;
                }
            }
        }
        return GasStack.EMPTY;
    }

    public FluidStack getContainedFluid(ItemStack stack, FluidStack type) {
        Optional<IFluidHandlerItem> capability = FluidUtil.getFluidHandler(stack).resolve();
        if (capability.isPresent()) {
            IFluidHandlerItem fluidHandlerItem = capability.get();
            for (int i = 0; i < fluidHandlerItem.getTanks(); i++) {
                FluidStack fluidInTank = fluidHandlerItem.getFluidInTank(i);
                if (fluidInTank.isFluidEqual(type)) {
                    return fluidInTank;
                }
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public void changeMode(@NotNull Player player, @NotNull ItemStack stack, int shift, DisplayChange displayChange) {
        for (mekanism.common.content.gear.Module<?> module : getModules(stack)) {
            if (module.handlesModeChange()) {
                module.changeMode(player, stack, shift, displayChange);
                return;
            }
        }
    }

    @Override
    public boolean supportsSlotType(ItemStack stack, @NotNull EquipmentSlot slotType) {
        
        return slotType == getEquipmentSlot() && getModules(stack).stream().anyMatch(mekanism.common.content.gear.Module::handlesModeChange);
    }

    @Override
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        if (getType() == ArmorItem.Type.CHESTPLATE && !entity.isShiftKeyDown()) {
            
            IModule<ModuleElytraUnit> module = getModule(stack, MekanismModules.ELYTRA_UNIT);
            if (module != null && module.isEnabled() && module.canUseEnergy(entity, MekanismConfig.gear.mekaSuitElytraEnergyUsage.get())) {
                
                
                IModule<ModuleJetpackUnit> jetpack = getModule(stack, MekanismModules.JETPACK_UNIT);
                return jetpack == null || !jetpack.isEnabled() || jetpack.getCustomInstance().getMode() != JetpackMode.HOVER ||
                       getContainedGas(stack, MekanismGases.HYDROGEN.get()).isEmpty();
            }
        }
        return false;
    }

    @Override
    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        
        
        if (!entity.level().isClientSide) {
            int nextFlightTicks = flightTicks + 1;
            if (nextFlightTicks % 10 == 0) {
                if (nextFlightTicks % 20 == 0) {
                    IModule<ModuleElytraUnit> module = getModule(stack, MekanismModules.ELYTRA_UNIT);
                    if (module != null && module.isEnabled()) {
                        module.useEnergy(entity, MekanismConfig.gear.mekaSuitElytraEnergyUsage.get());
                    }
                }
                entity.gameEvent(GameEvent.ELYTRA_GLIDE);
            }
        }
        return true;
    }

    @Override
    public boolean canUseJetpack(ItemStack stack) {
        return type == ArmorItem.Type.CHESTPLATE && (isModuleEnabled(stack, MekanismModules.JETPACK_UNIT) ? ChemicalUtil.hasChemical(stack, MekanismGases.HYDROGEN.get()) :
                                                     getModules(stack).stream().anyMatch(module -> module.isEnabled() && module.getData().isExclusive(ExclusiveFlag.OVERRIDE_JUMP.getMask())));
    }

    @Override
    public JetpackMode getJetpackMode(ItemStack stack) {
        IModule<ModuleJetpackUnit> module = getModule(stack, MekanismModules.JETPACK_UNIT);
        if (module != null && module.isEnabled()) {
            return module.getCustomInstance().getMode();
        }
        return JetpackMode.DISABLED;
    }

    @Override
    public void useJetpackFuel(ItemStack stack) {
        useGas(stack, MekanismGases.HYDROGEN.get(), 1);
    }

    private FloatingLong getMaxEnergy(ItemStack stack) {
        IModule<ModuleEnergyUnit> module = getModule(stack, MekanismModules.ENERGY_UNIT);
        return module == null ? MekanismConfig.gear.mekaSuitBaseEnergyCapacity.get() : module.getCustomInstance().getEnergyCapacity(module);
    }

    private FloatingLong getChargeRate(ItemStack stack) {
        IModule<ModuleEnergyUnit> module = getModule(stack, MekanismModules.ENERGY_UNIT);
        return module == null ? MekanismConfig.gear.mekaSuitBaseChargeRate.get() : module.getCustomInstance().getChargeRate(module);
    }

    @Override
    public int getDefense() {
        return getMaterial().getDefenseForType(getType());
    }

    @Override
    public float getToughness() {
        return getMaterial().getToughness();
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public void addToBuilder(Builder<Attribute, AttributeModifier> builder) {

    }

    public static float getDamageAbsorbed(Player player, DamageSource source, float amount) {
        return getDamageAbsorbed(player, source, amount, null);
    }

    public static boolean tryAbsorbAll(Player player, DamageSource source, float amount) {
        List<Runnable> energyUsageCallbacks = new ArrayList<>(4);
        if (getDamageAbsorbed(player, source, amount, energyUsageCallbacks) >= 1) {
            
            for (Runnable energyUsageCallback : energyUsageCallbacks) {
                energyUsageCallback.run();
            }
            return true;
        }
        return false;
    }

    private static float getDamageAbsorbed(Player player, DamageSource source, float amount, @Nullable List<Runnable> energyUseCallbacks) {
        if (amount <= 0) {
            return 0;
        }
        float ratioAbsorbed = 0;
        List<FoundArmorDetails> armorDetails = new ArrayList<>();
        
        for (ItemStack stack : player.getArmorSlots()) {
            if (!stack.isEmpty() && stack.getItem() instanceof ItemModifiableMekaSuitArmor armor) {
                IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
                if (energyContainer != null) {
                    FoundArmorDetails details = new FoundArmorDetails(energyContainer, armor);
                    armorDetails.add(details);
                    for (mekanism.common.content.gear.Module<?> module : details.armor.getModules(stack)) {
                        if (module.isEnabled()) {
                            ModuleDamageAbsorbInfo damageAbsorbInfo = getModuleDamageAbsorbInfo(module, source);
                            if (damageAbsorbInfo != null) {
                                float absorption = damageAbsorbInfo.absorptionRatio().getAsFloat();
                                ratioAbsorbed += absorbDamage(details.usageInfo, amount, absorption, ratioAbsorbed, damageAbsorbInfo.energyCost());
                                if (ratioAbsorbed >= 1) {
                                    
                                    break;
                                }
                            }
                        }
                    }
                    if (ratioAbsorbed >= 1) {
                        
                        break;
                    }
                }
            }
        }
        if (ratioAbsorbed < 1) {
            
            Float absorbRatio = null;
            for (FoundArmorDetails details : armorDetails) {
                if (absorbRatio == null) {
                    
                    
                    if (!source.is(MekanismTags.DamageTypes.MEKASUIT_ALWAYS_SUPPORTED) && source.is(DamageTypeTags.BYPASSES_ARMOR)) {
                        break;
                    }
                    
                    ResourceLocation damageTypeName = source.typeHolder().unwrapKey()
                          .map(ResourceKey::location)
                          
                          
                          .orElseGet(() -> player.level().registryAccess().registry(Registries.DAMAGE_TYPE)
                                .map(registry -> registry.getKey(source.type()))
                                .orElse(null)
                          );
                    if (damageTypeName != null) {
                        absorbRatio = MekanismConfig.gear.mekaSuitDamageRatios.get().get(damageTypeName);
                    }
                    if (absorbRatio == null) {
                        absorbRatio = MekanismConfig.gear.mekaSuitUnspecifiedDamageRatio.getAsFloat();
                    }
                    if (absorbRatio == 0) {
                        
                        
                        break;
                    }
                }
                float absorption = details.armor.absorption * absorbRatio;
                ratioAbsorbed += absorbDamage(details.usageInfo, amount, absorption, ratioAbsorbed, MekanismConfig.gear.mekaSuitEnergyUsageDamage);
                if (ratioAbsorbed >= 1) {
                    
                    break;
                }
            }
        }
        for (FoundArmorDetails details : armorDetails) {
            
            if (!details.usageInfo.energyUsed.isZero()) {
                if (energyUseCallbacks == null) {
                    details.energyContainer.extract(details.usageInfo.energyUsed, Action.EXECUTE, AutomationType.MANUAL);
                } else {
                    energyUseCallbacks.add(() -> details.energyContainer.extract(details.usageInfo.energyUsed, Action.EXECUTE, AutomationType.MANUAL));
                }
            }
        }
        return Math.min(ratioAbsorbed, 1);
    }

    @Nullable
    private static <MODULE extends ICustomModule<MODULE>> ModuleDamageAbsorbInfo getModuleDamageAbsorbInfo(IModule<MODULE> module, DamageSource damageSource) {
        return module.getCustomInstance().getDamageAbsorbInfo(module, damageSource);
    }

    private static float absorbDamage(EnergyUsageInfo usageInfo, float amount, float absorption, float currentAbsorbed, FloatingLongSupplier energyCost) {
        
        absorption = Math.min(1 - currentAbsorbed, absorption);
        float toAbsorb = amount * absorption;
        if (toAbsorb > 0) {
            FloatingLong usage = energyCost.get().multiply(toAbsorb);
            if (usage.isZero()) {
                
                
                return absorption;
            } else if (usageInfo.energyAvailable.greaterOrEqual(usage)) {
                
                
                usageInfo.energyUsed = usageInfo.energyUsed.plusEqual(usage);
                usageInfo.energyAvailable = usageInfo.energyAvailable.minusEqual(usage);
                return absorption;
            } else if (!usageInfo.energyAvailable.isZero()) {
                
                
                float absorbedPercent = usageInfo.energyAvailable.divide(usage).floatValue();
                usageInfo.energyUsed = usageInfo.energyUsed.plusEqual(usageInfo.energyAvailable);
                usageInfo.energyAvailable = FloatingLong.ZERO;
                return absorption * absorbedPercent;
            }
        }
        return 0;
    }

    private static class FoundArmorDetails {

        private final IEnergyContainer energyContainer;
        private final EnergyUsageInfo usageInfo;
        private final ItemModifiableMekaSuitArmor armor;

        public FoundArmorDetails(IEnergyContainer energyContainer, ItemModifiableMekaSuitArmor armor) {
            this.energyContainer = energyContainer;
            this.usageInfo = new EnergyUsageInfo(energyContainer.getEnergy());
            this.armor = armor;
        }
    }

    private static class EnergyUsageInfo {

        private FloatingLong energyAvailable;
        private FloatingLong energyUsed = FloatingLong.ZERO;

        public EnergyUsageInfo(FloatingLong energyAvailable) {
            
            this.energyAvailable = energyAvailable.copy();
        }
    }
}
