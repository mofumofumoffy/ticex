package moffy.ticex.caps.draconicevolution;

import com.brandon3055.brandonscore.api.power.OPStorage;

import net.minecraftforge.energy.IEnergyStorage;

public class EvolvedEnergyStorage implements IEnergyStorage{
    protected OPStorage opStorage;

    public EvolvedEnergyStorage(OPStorage opStorage){
        this.opStorage = opStorage;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return this.opStorage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return this.opStorage.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return this.opStorage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return this.opStorage.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return this.opStorage.canExtract();
    }

    @Override
    public boolean canReceive() {
        return this.opStorage.canReceive();
    }
}
