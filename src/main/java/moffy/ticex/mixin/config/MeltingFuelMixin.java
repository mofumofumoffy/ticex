package moffy.ticex.mixin.config;

import moffy.ticex.TicEXConfig;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;

@Mixin(value = MeltingFuel.class, remap = false)
public class MeltingFuelMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void modifyTemp(ResourceLocation id, FluidIngredient input, int duration, int temperature, int rate, CallbackInfo ci) {
        try {
            if(TicEXConfig.USE_MORE_CONFIG != null && TicEXConfig.USE_MORE_CONFIG.get()){
                for (int i = 0; i < 20; i++) {
                    if (id.getNamespace().equals("ticex") && id.getPath().equals("smeltery/melting/fuel/rf_furnace_fuel_" + i)) {
                        TicEXConfig.RF_FURNACE_FUEL_TEMP.get().getConfiguredValue(i).ifPresent(value -> ((MeltingFuelAccessor) this).setTemperature(value));
                        TicEXConfig.RF_FURNACE_FUEL_RATE.get().getConfiguredValue(i).ifPresent(value -> ((MeltingFuelAccessor) this).setRate(value));
                    }
                }
            }
        } catch (IllegalStateException ignored){}
    }
}
