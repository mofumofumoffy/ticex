package moffy.ticex.lib.hook;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;
import java.util.Comparator;

public interface EnergyModifierHook{
    default boolean isEnabled(){
        return true;
    }
    public int receiveEnergy(IToolStackView tool, ItemStack stack, int received, boolean simulate);
    public int extractEnergy(IToolStackView tool, ItemStack stack, int extracted, boolean simulate);
    public int getEnergyStored(IToolStackView tool, ItemStack stack);
    public int getMaxEnergyStored(IToolStackView tool, ItemStack stack);
    public boolean canExtract(IToolStackView tool, ItemStack stack);
    public boolean canReceive(IToolStackView tool, ItemStack stack);

    public static class DefaultClass implements EnergyModifierHook{
        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public int receiveEnergy(IToolStackView tool, ItemStack stack, int received, boolean simulate) {
            return 0;
        }

        @Override
        public int extractEnergy(IToolStackView tool, ItemStack stack, int extracted, boolean simulate) {
            return 0;
        }

        @Override
        public int getEnergyStored(IToolStackView tool, ItemStack stack) {
            return 0;
        }

        @Override
        public int getMaxEnergyStored(IToolStackView tool, ItemStack stack) {
            return 0;
        }

        @Override
        public boolean canExtract(IToolStackView tool, ItemStack stack) {
            return false;
        }

        @Override
        public boolean canReceive(IToolStackView tool, ItemStack stack) {
            return false;
        }
    }

    record AllMerger(Collection<EnergyModifierHook> modules) implements EnergyModifierHook{
        @Override
        public boolean isEnabled() {
            return !modules.stream().filter(EnergyModifierHook::isEnabled).toList().isEmpty();
        }

        @Override
        public int receiveEnergy(IToolStackView tool, ItemStack stack, int received, boolean simulate) {
            return modules.stream().map(module->module.receiveEnergy(tool, stack, received, simulate)).max(Comparator.naturalOrder()).orElse(0);
        }

        @Override
        public int extractEnergy(IToolStackView tool, ItemStack stack, int extracted, boolean simulate) {
            return modules.stream().map(module->module.receiveEnergy(tool, stack, extracted, simulate)).max(Comparator.naturalOrder()).orElse(0);
        }

        @Override
        public int getEnergyStored(IToolStackView tool, ItemStack stack) {
            return modules.stream().map(module->module.getEnergyStored(tool,stack)).min(Comparator.naturalOrder()).orElse(0);
        }

        @Override
        public int getMaxEnergyStored(IToolStackView tool, ItemStack stack) {
            return modules.stream().map(module->module.getMaxEnergyStored(tool,stack)).max(Comparator.naturalOrder()).orElse(0);
        }

        @Override
        public boolean canExtract(IToolStackView tool, ItemStack stack) {
            for(EnergyModifierHook modules : modules){
                if(modules.canExtract(tool, stack)){
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canReceive(IToolStackView tool, ItemStack stack) {
            for(EnergyModifierHook modules : modules){
                if(modules.canReceive(tool, stack)){
                    return true;
                }
            }
            return false;
        }
    }
}
