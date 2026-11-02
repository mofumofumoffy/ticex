package moffy.ticex.caps.mekanism;

import mekanism.api.gear.IModuleHelper;
import meranha.mekaweapons.items.modules.WeaponsModules;
import moffy.ticex.lib.modules.mekanism.interfaces.IMekanicArrow;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MekanicArrowCapability implements IMekanicArrow, INBTSerializable<CompoundTag> {
    public static final Capability<IMekanicArrow> MEKANIC_ARROW_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    private ItemStack bowItemStack = ItemStack.EMPTY;
    private ItemStack originalAmmoStack = ItemStack.EMPTY;
    private ItemStack copiedOriginalAmmoStack = ItemStack.EMPTY;

    private boolean energizedArrow = false;

    @Override
    public ItemStack getBowItem() {
        return bowItemStack;
    }

    @Override
    public ItemStack getOriginalAmmo() {
        return originalAmmoStack;
    }

    @Override
    public ItemStack getCopiedOriginalAmmo() {
        return copiedOriginalAmmoStack;
    }

    @Override
    public void setBowItem(ItemStack bowItem) {
        this.bowItemStack = bowItem;
        this.energizedArrow = IModuleHelper.INSTANCE.isEnabled(bowItemStack, WeaponsModules.ARROWENERGY_UNIT);
    }

    @Override
    public void setOriginalAmmo(ItemStack originalAmmo) {
        this.originalAmmoStack = originalAmmo;
        this.copiedOriginalAmmoStack = originalAmmo.copy();
    }

    @Override
    public boolean isEnergizedArrow() {
        return energizedArrow;
    }

    @Override
    public void shrinkAmmo(int needed) {
        if(!isEnergizedArrow()){
            getOriginalAmmo().shrink(needed);
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.put("bow", this.bowItemStack.save(new CompoundTag()));
        nbt.putBoolean("energized_arrow", this.energizedArrow);
        nbt.put("ammo", this.originalAmmoStack.save(new CompoundTag()));
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.bowItemStack = ItemStack.of(nbt.getCompound("bow"));
        this.energizedArrow = nbt.getBoolean("energized_arrow");
        this.copiedOriginalAmmoStack = ItemStack.of(nbt.getCompound("ammo"));
    }

    public static class Provider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {

        MekanicArrowCapability mekanicArrowCapability = new MekanicArrowCapability();
        LazyOptional<MekanicArrowCapability> mekanicArrowCapabilityLazyOptional = LazyOptional.of(()->mekanicArrowCapability);

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if(cap == MEKANIC_ARROW_CAPABILITY){
                return mekanicArrowCapabilityLazyOptional.cast();
            }
            return LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return mekanicArrowCapability.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            mekanicArrowCapability.deserializeNBT(nbt);
        }
    }
}
