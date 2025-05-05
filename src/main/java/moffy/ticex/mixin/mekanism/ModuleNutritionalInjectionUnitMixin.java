package moffy.ticex.mixin.mekanism;

import java.util.Optional;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mekanism.api.gear.IHUDElement;
import mekanism.api.gear.IModule;
import mekanism.api.gear.IModuleHelper;
import mekanism.api.math.FloatingLong;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.mekasuit.ModuleNutritionalInjectionUnit;
import mekanism.common.registries.MekanismFluids;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.StorageUtils;
import moffy.ticex.item.modifiable.ModifiableMekaSuitArmor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

@Mixin(value = ModuleNutritionalInjectionUnit.class, remap = false)
public class ModuleNutritionalInjectionUnitMixin {

    @Shadow
    @Final
    private static ResourceLocation icon;

    @Inject(
        at = @At("head"),
        method = "tickServer",
        cancellable = true
    )
    public void tickServer(IModule<ModuleNutritionalInjectionUnit> module, Player player, CallbackInfo cb) {
        ItemStack container = module.getContainer();
        if(container.getItem() instanceof IModifiable){
            FloatingLong usage = (FloatingLong)MekanismConfig.gear.mekaSuitEnergyUsageNutritionalInjection.get();
            if (MekanismUtils.isPlayingMode(player) && player.canEat(false)) {
                ModifiableMekaSuitArmor item = (ModifiableMekaSuitArmor)container.getItem();
                int needed = Math.min(20 - player.getFoodData().getFoodLevel(), item.getContainedFluid(container, MekanismFluids.NUTRITIONAL_PASTE.getFluidStack(1)).getAmount() / MekanismConfig.general.nutritionalPasteMBPerFood.get());
                int toFeed = Math.min(module.getContainerEnergy().divideToInt(usage), needed);
                if (toFeed > 0) {
                    module.useEnergy(player, usage.multiply((long)toFeed));
                    FluidUtil.getFluidHandler(container).ifPresent((handler) -> {
                    handler.drain(MekanismFluids.NUTRITIONAL_PASTE.getFluidStack(toFeed * MekanismConfig.general.nutritionalPasteMBPerFood.get()), FluidAction.EXECUTE);
                    });
                    player.getFoodData().eat(needed, MekanismConfig.general.nutritionalPasteSaturation.get());
                }
            }
            cb.cancel();
        }
    }

    @Inject(
        at = @At("head"),
        method = "addHUDElements",
        cancellable = true
    )
    public void addHUDElements(IModule<ModuleNutritionalInjectionUnit> module, Player player, Consumer<IHUDElement> hudElementAdder, CallbackInfo cb) {
        ItemStack container = module.getContainer();
        if(container.getItem() instanceof IModifiable){
            Optional<IFluidHandlerItem> capability = FluidUtil.getFluidHandler(container).resolve();
            if (capability.isPresent()) {
                IFluidHandlerItem handler = (IFluidHandlerItem)capability.get();
                int max = MekanismConfig.gear.mekaSuitNutritionalMaxStorage.getAsInt();
                handler.drain(MekanismFluids.NUTRITIONAL_PASTE.getFluidStack(max), FluidAction.SIMULATE);
            }

            FluidStack stored = ((ModifiableMekaSuitArmor)container.getItem()).getContainedFluid(container, MekanismFluids.NUTRITIONAL_PASTE.getFluidStack(1));
            double ratio = StorageUtils.getRatio((long)stored.getAmount(), (long)MekanismConfig.gear.mekaSuitNutritionalMaxStorage.get());
            hudElementAdder.accept(IModuleHelper.INSTANCE.hudElementPercent(icon, ratio));
            cb.cancel();
        }
   }
}
