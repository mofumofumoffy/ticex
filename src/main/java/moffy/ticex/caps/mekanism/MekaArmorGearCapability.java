package moffy.ticex.caps.mekanism;

import mekanism.api.Action;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.gear.IModule;
import mekanism.api.gear.ModuleData;
import mekanism.api.math.FloatingLong;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.ItemCapabilityWrapper;
import mekanism.common.capabilities.chemical.item.ChemicalTankSpec;
import mekanism.common.capabilities.chemical.item.RateLimitMultiTankGasHandler;
import mekanism.common.capabilities.fluid.item.RateLimitMultiTankFluidHandler;
import mekanism.common.capabilities.laser.item.LaserDissipationHandler;
import mekanism.common.capabilities.radiation.item.RadiationShieldingHandler;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.mekasuit.ModuleJetpackUnit;
import mekanism.common.content.gear.shared.ModuleEnergyUnit;
import mekanism.common.item.gear.ItemHazmatSuitArmor;
import mekanism.common.item.interfaces.IJetpackItem;
import mekanism.common.registries.MekanismFluids;
import mekanism.common.registries.MekanismGases;
import mekanism.common.registries.MekanismModules;
import mekanism.common.util.ChemicalUtil;
import moffy.ticex.TicEX;
import moffy.ticex.lib.modules.mekanism.MekaGearCapability;
import moffy.ticex.lib.modules.mekanism.interfaces.IAbsorbableItem;
import moffy.ticex.lib.modules.mekanism.interfaces.IFluidTankItem;
import moffy.ticex.lib.modules.mekanism.interfaces.IGasTankItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MekaArmorGearCapability extends MekaGearCapability implements IAbsorbableItem, IJetpackItem, IGasTankItem, IFluidTankItem {
    private final List<ChemicalTankSpec<Gas>> gasTankSpecs = new ArrayList<>();
    private final List<ChemicalTankSpec<Gas>> gasTankSpecsView = Collections.unmodifiableList(gasTankSpecs);
    private final List<RateLimitMultiTankFluidHandler.FluidTankSpec> fluidTankSpecs = new ArrayList<>();
    private final List<RateLimitMultiTankFluidHandler.FluidTankSpec> fluidTankSpecsView = Collections.unmodifiableList(fluidTankSpecs);
    private final float absorption;
    private final double laserDissipation;
    private final double laserRefraction;
    public MekaArmorGearCapability(ArmorItem armorItem){
        super();
        switch (armorItem.getType()) {
            case HELMET -> {
                fluidTankSpecs.add(RateLimitMultiTankFluidHandler.FluidTankSpec.createFillOnly(MekanismConfig.gear.mekaSuitNutritionalTransferRate, MekanismConfig.gear.mekaSuitNutritionalMaxStorage,
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
    }

    @Override
    public void gatherCapabilities(ItemStack stack, List<ItemCapabilityWrapper.ItemCapability> capabilities) {
        super.gatherCapabilities(stack, capabilities);
        if(stack.getItem() instanceof ArmorItem armorItem){
            capabilities.add(RadiationShieldingHandler.create(item -> isModuleEnabled(item, MekanismModules.RADIATION_SHIELDING_UNIT) ?
                    ItemHazmatSuitArmor.getShieldingByArmor(armorItem.getType()) : 0));
            capabilities.add(LaserDissipationHandler.create(item -> isModuleEnabled(item, MekanismModules.LASER_DISSIPATION_UNIT) ? laserDissipation : 0,
                    item -> isModuleEnabled(item, MekanismModules.LASER_DISSIPATION_UNIT) ? laserRefraction : 0));
            if (!gasTankSpecs.isEmpty()) {
                capabilities.add(RateLimitMultiTankGasHandler.create(gasTankSpecs));
            }
            if (!fluidTankSpecs.isEmpty()) {
                capabilities.add(RateLimitMultiTankFluidHandler.create(fluidTankSpecs));
            }
        }
    }

    @Override
    public @NotNull GasStack useGas(ItemStack stack, Gas type, long amount) {
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
    public ResourceLocation getRadialId() {
        return TicEX.getResource("tinker_armor");
    }

    @Override
    public FloatingLong getChargeRate(ItemStack stack) {
        IModule<ModuleEnergyUnit> module = getModule(stack, MekanismModules.ENERGY_UNIT);
        return module == null ? MekanismConfig.gear.mekaSuitBaseChargeRate.get() : module.getCustomInstance().getChargeRate(module);
    }

    @Override
    public FloatingLong getMaxEnergy(ItemStack stack) {
        IModule<ModuleEnergyUnit> module = getModule(stack, MekanismModules.ENERGY_UNIT);
        return module == null ? MekanismConfig.gear.mekaSuitBaseEnergyCapacity.get() : module.getCustomInstance().getEnergyCapacity(module);
    }

    @Override
    public float getAbsorption(ArmorItem item) {
        return absorption;
    }

    @Override
    public boolean canUseJetpack(ItemStack stack) {
        if(stack.getItem() instanceof ArmorItem armorItem){
            return armorItem.getType() == ArmorItem.Type.CHESTPLATE && (isModuleEnabled(stack, MekanismModules.JETPACK_UNIT) ? ChemicalUtil.hasChemical(stack, MekanismGases.HYDROGEN.get()) :
                    getModules(stack).stream().anyMatch(module -> module.isEnabled() && module.getData().isExclusive(ModuleData.ExclusiveFlag.OVERRIDE_JUMP.getMask())));
        }
        return false;
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

    public List<ChemicalTankSpec<Gas>> getGasTankSpecs() {
        return gasTankSpecs;
    }

    public List<RateLimitMultiTankFluidHandler.FluidTankSpec> getFluidTankSpecs() {
        return fluidTankSpecs;
    }
}
