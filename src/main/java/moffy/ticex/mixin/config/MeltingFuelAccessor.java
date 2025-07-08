package moffy.ticex.mixin.config;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;

@Mixin(value = MeltingFuel.class, remap = false)
public interface MeltingFuelAccessor {
    @Accessor("temperature")
    @Mutable
    void setTemperature(int temperature);

    @Accessor("rate")
    @Mutable
    void seTRate(int rate);
}
