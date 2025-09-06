package moffy.ticex.lib.modules.mekanism.interfaces;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.api.math.FloatingLong;
import mekanism.api.math.FloatingLongSupplier;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.Module;
import mekanism.common.tags.MekanismTags;
import mekanism.common.util.StorageUtils;
import moffy.ticex.lib.modules.mekanism.MekaGearCapability;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public interface IAbsorbableItem {
    static float getDamageAbsorbed(Player player, DamageSource source, float amount) {
        return getDamageAbsorbed(player, source, amount, null);
    }

    static boolean tryAbsorbAll(Player player, DamageSource source, float amount) {
        List<Runnable> energyUsageCallbacks = new ArrayList<>(4);
        if (getDamageAbsorbed(player, source, amount, energyUsageCallbacks) >= 1) {
            for (Runnable energyUsageCallback : energyUsageCallbacks) {
                energyUsageCallback.run();
            }
            return true;
        }
        return false;
    }


    static float getDamageAbsorbed(Player player, DamageSource source, float amount, @Nullable List<Runnable> energyUseCallbacks) {
        if (amount <= 0) {
            return 0;
        }
        float ratioAbsorbed = 0;
        List<FoundArmorDetails> armorDetails = new ArrayList<>();
        for (ItemStack stack : player.getArmorSlots()) {
            if (!stack.isEmpty()) {
                LazyOptional<IMekaGear> mekaGearLazyOptional = stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY);
                if (mekaGearLazyOptional.isPresent()) {
                    IMekaGear mekaGear = mekaGearLazyOptional.orElseThrow(IllegalStateException::new);
                    if(mekaGear instanceof IAbsorbableItem absorbableGear){
                        ratioAbsorbed += absorbableGear.getRatioAbsorbed(stack, source, amount, armorDetails);
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
                float absorption = details.absorption * absorbRatio;
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

    static float absorbDamage(EnergyUsageInfo usageInfo, float amount, float absorption, float currentAbsorbed, FloatingLongSupplier energyCost) {
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

    @Nullable
    default <MODULE extends ICustomModule<MODULE>> ICustomModule.ModuleDamageAbsorbInfo getModuleDamageAbsorbInfo(IModule<MODULE> module, DamageSource damageSource) {
        return module.getCustomInstance().getDamageAbsorbInfo(module, damageSource);
    }

    default float getRatioAbsorbed(ItemStack stack, DamageSource source, float amount, List<FoundArmorDetails> foundArmorDetailsList){
        float ratioAbsorbed = 0;

        if (!stack.isEmpty() && stack.getItem() instanceof ArmorItem armor) {
            LazyOptional<IMekaGear> mekaGearLazyOptional = stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY);
            if (mekaGearLazyOptional.isPresent()) {
                IMekaGear mekaGear = mekaGearLazyOptional.orElseThrow(IllegalStateException::new);
                IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
                if (energyContainer != null) {
                    FoundArmorDetails details = new FoundArmorDetails(energyContainer, armor, getAbsorption(armor));
                    foundArmorDetailsList.add(details);
                    for (Module<?> module : mekaGear.getModules(stack)) {
                        if (module.isEnabled()) {
                            ICustomModule.ModuleDamageAbsorbInfo damageAbsorbInfo = getModuleDamageAbsorbInfo(module, source);
                            if (damageAbsorbInfo != null) {
                                float absorption = damageAbsorbInfo.absorptionRatio().getAsFloat();
                                ratioAbsorbed += absorbDamage(details.usageInfo, amount, absorption, ratioAbsorbed, damageAbsorbInfo.energyCost());
                                if (ratioAbsorbed >= 1) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        return ratioAbsorbed;
    }

    float getAbsorption(ArmorItem item);

    class FoundArmorDetails {

        private final IEnergyContainer energyContainer;
        private final EnergyUsageInfo usageInfo;
        private final ArmorItem armor;
        private final float absorption;

        public FoundArmorDetails(IEnergyContainer energyContainer, ArmorItem armor, float absorption) {
            this.energyContainer = energyContainer;
            this.usageInfo = new EnergyUsageInfo(energyContainer.getEnergy());
            this.armor = armor;
            this.absorption = absorption;
        }
    }

    class EnergyUsageInfo {
        private FloatingLong energyAvailable;
        private FloatingLong energyUsed = FloatingLong.ZERO;

        public EnergyUsageInfo(FloatingLong energyAvailable) {
            this.energyAvailable = energyAvailable.copy();
        }
    }
}
