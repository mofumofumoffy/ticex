package moffy.ticex.mixin.mekanism;

import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.gear.IModule;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.mekasuit.ModuleElytraUnit;
import mekanism.common.content.gear.mekasuit.ModuleJetpackUnit;
import mekanism.common.item.interfaces.IJetpackItem;
import mekanism.common.registries.MekanismGases;
import mekanism.common.registries.MekanismModules;
import mekanism.common.util.StorageUtils;
import moffy.ticex.TicEX;
import moffy.ticex.lib.modules.mekanism.MekaGearCapability;
import moffy.ticex.lib.modules.mekanism.interfaces.IMekaGear;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.tools.item.armor.ModifiableArmorItem;

import java.util.Optional;

@Mixin(value = ModifiableArmorItem.class, remap = false)
public class ModifiableArmorItemMixin {
    @Inject(
            at = @At("RETURN"),
            method = "canElytraFly",
            cancellable = true
    )
    public void canElytraFlyWithMekaModule(ItemStack stack, LivingEntity entity, CallbackInfoReturnable<Boolean> cir){
        if(((ModifiableArmorItem)((Object)this)).getType() == ArmorItem.Type.CHESTPLATE && cir.getReturnValue() == false && stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent()){
            IMekaGear mekaGear = stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).orElseThrow(IllegalStateException::new);
                IModule<ModuleElytraUnit> elytra = mekaGear.getModule(stack, MekanismModules.ELYTRA_UNIT);
                IModule<ModuleJetpackUnit> jetpack = mekaGear.getModule(stack, MekanismModules.JETPACK_UNIT);
                if (elytra != null && elytra.isEnabled() && elytra.canUseEnergy(entity, MekanismConfig.gear.mekaSuitElytraEnergyUsage.get())) {
                    cir.setReturnValue(jetpack == null || !jetpack.isEnabled() || jetpack.getCustomInstance().getMode() != IJetpackItem.JetpackMode.HOVER ||
                            ticex_1_20_1$getContainedGas(stack, MekanismGases.HYDROGEN.get()).isEmpty());
                }
        }
    }

    @Unique
    private GasStack ticex_1_20_1$getContainedGas(ItemStack stack, Gas type) {
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
}
