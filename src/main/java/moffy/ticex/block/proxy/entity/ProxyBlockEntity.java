package moffy.ticex.block.proxy.entity;

import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProxyBlockEntity extends BlockEntity{
    protected boolean powered;
    protected LazyOptional<IItemHandler> proxiedCap = LazyOptional.empty();

    protected final ProxyItemHandler slot;
    protected LazyOptional<IItemHandler> slotCap;

    public ProxyBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(TicEXRegistry.INVENTORY_PROXY_ENTITY.get(), pPos, pBlockState);
        this.slot = new ProxyItemHandler(this);
        this.slotCap = LazyOptional.of(() -> this.slot);
    }

    public void onPowerChanged(boolean nowPowered) {
        if (this.powered != nowPowered) {
            this.powered = nowPowered;
            refreshProxy();
            setChanged();
            syncToClient();
        }
    }

    public void refreshProxy() {
        proxiedCap.invalidate();
        proxiedCap = LazyOptional.empty();

        ItemStack stack = slot.getStackInSlot(0);
        if (!stack.isEmpty()) {
            LazyOptional<IItemHandler> inner = stack.getCapability(ForgeCapabilities.ITEM_HANDLER);
            if (inner.isPresent()) {
                proxiedCap = inner;
            }
        }
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (powered) {
                if (!proxiedCap.isPresent()) refreshProxy();
                return proxiedCap.cast();
            } else {
                return slotCap.cast();
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        slotCap.invalidate();
        proxiedCap.invalidate();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("slot", slot.serializeNBT());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        slot.deserializeNBT(tag.getCompound("slot"));
        refreshProxy();
    }

    public ProxyItemHandler getSlot() {
        return slot;
    }

    public NonNullList<ItemStack> getDrops() {
        NonNullList<ItemStack> list = NonNullList.create();
        list.add(slot.getStackInSlot(0));
        return list;
    }

    public void syncToClient() {
        if (level instanceof ServerLevel sl) sl.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }
}
