package moffy.ticex.mixin.mekanism;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import mekanism.common.content.gear.mekasuit.ModuleNutritionalInjectionUnit;
import mekanism.common.item.gear.ItemMekaSuitArmor;
import mekanism.common.registries.MekanismFluids;
import moffy.ticex.lib.modules.mekanism.MekaGearCapability;
import moffy.ticex.lib.modules.mekanism.interfaces.IFluidTankItem;
import moffy.ticex.lib.modules.mekanism.interfaces.IMekaGear;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(value = ModuleNutritionalInjectionUnit.class, remap = false)
public class ModuleNutritionalInjectionUnitMixin {

    @WrapOperation(method = "tickServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;", remap = true))
    public Item disableDefaultArmor(ItemStack instance, Operation<Item> original) {
        Predicate<ItemStack> hasCap = stack -> stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent();
        if(hasCap.test(instance)) {
            return null;
        }
        return original.call(instance);
    }

    @Redirect(method = "tickServer", at = @At(value = "INVOKE", target = "Lmekanism/common/item/gear/ItemMekaSuitArmor;getContainedFluid(Lnet/minecraft/world/item/ItemStack;Lnet/minecraftforge/fluids/FluidStack;)Lnet/minecraftforge/fluids/FluidStack;"))
    public FluidStack redirectGearCap(ItemMekaSuitArmor armor, ItemStack container, FluidStack fluidHandlerItem,
                                   @Local(argsOnly = true) Player player) {
        Predicate<ItemStack> hasCap = stack -> stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent();
        if(hasCap.test(container)) {
            IMekaGear mekaGear = container.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).orElseThrow(IllegalStateException::new);
            if(mekaGear instanceof IFluidTankItem fluidTankGear) {
                return fluidTankGear.getContainedFluid(container, MekanismFluids.NUTRITIONAL_PASTE.getFluidStack(1));
            }
        }

        return FluidStack.EMPTY;
    }

    @WrapOperation(method = "addHUDElements", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;", remap = true))
    public Item disableHUDDefaultArmor(ItemStack instance, Operation<Item> original) {
        Predicate<ItemStack> hasCap = stack -> stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent();
        if(hasCap.test(instance)) {
            return null;
        }
        return original.call(instance);
    }

    @Redirect(method = "addHUDElements", at = @At(value = "INVOKE", target = "Lmekanism/common/item/gear/ItemMekaSuitArmor;getContainedFluid(Lnet/minecraft/world/item/ItemStack;Lnet/minecraftforge/fluids/FluidStack;)Lnet/minecraftforge/fluids/FluidStack;"))
    public FluidStack redirectHUDGearCap(ItemMekaSuitArmor instance, ItemStack container, FluidStack fluidHandlerItem) {
        Predicate<ItemStack> hasCap = stack -> stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent();
        if (hasCap.test(container)) {
            IMekaGear mekaGear = container.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).orElseThrow(IllegalStateException::new);

            if(mekaGear instanceof IFluidTankItem fluidTankGear){
                return fluidTankGear.getContainedFluid(
                        container,
                        MekanismFluids.NUTRITIONAL_PASTE.getFluidStack(1)
                );
            }
        }
        return FluidStack.EMPTY;
    }
}
