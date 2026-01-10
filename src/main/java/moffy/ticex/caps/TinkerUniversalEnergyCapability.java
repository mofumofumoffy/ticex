package moffy.ticex.caps;

import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Comparator;
import java.util.function.Supplier;

public class TinkerUniversalEnergyCapability implements IEnergyStorage {

    private final ItemStack stack;
    private final IToolStackView tool;

    public TinkerUniversalEnergyCapability(ItemStack stack, Supplier<? extends IToolStackView> toolSupplier){
        this.stack = stack;
        this.tool = toolSupplier.get();
    }

    @Override
    public int receiveEnergy(int i, boolean b) {
        return tool.getModifierList().stream().map(entry -> entry.getHook(TicEXRegistry.ENERGY_HOOK).receiveEnergy(tool, stack, i, b)).max(Comparator.naturalOrder()).orElse(0);
    }

    @Override
    public int extractEnergy(int i, boolean b) {
        return tool.getModifierList().stream().map(entry -> entry.getHook(TicEXRegistry.ENERGY_HOOK).extractEnergy(tool, stack, i, b)).max(Comparator.naturalOrder()).orElse(0);
    }

    @Override
    public int getEnergyStored() {
        return tool.getModifierList().stream().map(entry -> entry.getHook(TicEXRegistry.ENERGY_HOOK).getEnergyStored(tool,stack)).min(Comparator.naturalOrder()).orElse(0);
    }

    @Override
    public int getMaxEnergyStored() {
        return tool.getModifierList().stream().map(entry -> entry.getHook(TicEXRegistry.ENERGY_HOOK).getMaxEnergyStored(tool, stack)).max(Comparator.naturalOrder()).orElse(0);
    }

    @Override
    public boolean canExtract() {
        return !tool.getModifierList().stream().map(entry -> entry.getHook(TicEXRegistry.ENERGY_HOOK).canExtract(tool, stack)).filter(b -> b).toList().isEmpty();
    }

    @Override
    public boolean canReceive() {
        return !tool.getModifierList().stream().map(entry -> entry.getHook(TicEXRegistry.ENERGY_HOOK).canReceive(tool, stack)).filter(b -> b).toList().isEmpty();
    }
}
