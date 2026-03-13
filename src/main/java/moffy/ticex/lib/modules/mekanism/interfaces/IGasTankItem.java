package moffy.ticex.lib.modules.mekanism.interfaces;

import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface IGasTankItem {
    @NotNull
    GasStack useGas(ItemStack stack, Gas type, long amount);

    GasStack getContainedGas(ItemStack stack, Gas type);
}
