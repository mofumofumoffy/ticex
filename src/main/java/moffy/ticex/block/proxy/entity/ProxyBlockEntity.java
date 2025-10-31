package moffy.ticex.block.proxy.entity;

import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.*;

public class ProxyBlockEntity extends BlockEntity {
    protected boolean powered;
    protected LazyOptional<IItemHandler> proxiedCap = LazyOptional.empty();

    protected final BlockPos pos;
    protected final ProxyMainItemHandler mainItemHandler;
    protected final Map<UUID, ProxyPlayerItemHandler> cachedPlayerItemHandler;
    protected final ProxyAnvilHandler anvilHandler;
    protected LazyOptional<IItemHandler> slotCap;
    protected UUID placerUUID = null;

    public ProxyBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(TicEXRegistry.INVENTORY_PROXY_ENTITY.get(), pPos, pBlockState);

        this.pos = pPos;
        this.cachedPlayerItemHandler = new HashMap<>();
        this.mainItemHandler = new ProxyMainItemHandler(this);
        this.slotCap = LazyOptional.of(() -> this.mainItemHandler);
        this.anvilHandler = new ProxyAnvilHandler(this);
        this.powered = pBlockState.getValue(BlockStateProperties.POWERED);
    }

    public UUID getPlacerUUID() {
        return placerUUID;
    }

    public void setPlacerUUID(UUID placerUUID) {
        this.placerUUID = placerUUID;
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

        ItemStack stack = mainItemHandler.getStackInSlot(0);

        if (!stack.isEmpty()) {
            if(stack.is(Items.PLAYER_HEAD)){
                UUID uuid = ProxyPlayerItemHandler.getUUIDFromHead(stack, this.getLevel());
                if(uuid != null && !cachedPlayerItemHandler.containsKey(uuid)){
                    cachedPlayerItemHandler.put(uuid, new ProxyPlayerItemHandler(this, uuid));
                }
                proxiedCap = LazyOptional.of(() -> cachedPlayerItemHandler.get(uuid));
            } else if (stack.is(TinkerTables.tinkersAnvil.asItem()) || stack.is(TinkerTables.scorchedAnvil.asItem())){
                //proxiedCap = LazyOptional.of(() -> anvilHandler);
            } else {
                LazyOptional<IItemHandler> inner = stack.getCapability(ForgeCapabilities.ITEM_HANDLER);
                if (inner.isPresent()) {
                    proxiedCap = inner;
                }
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
        tag.put("slot", mainItemHandler.serializeNBT());
        tag.put("anvil", anvilHandler.serializeNBT());
        if(placerUUID != null){
            tag.putUUID("placer", placerUUID);
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        mainItemHandler.deserializeNBT(tag.getCompound("slot"));
        anvilHandler.deserializeNBT(tag.getCompound("anvil"));
        if(tag.contains("placer")){
            NbtUtils.loadUUID(tag.getCompound("placer"));
        }
        refreshProxy();
    }

    public ProxyMainItemHandler getMainItemHandler() {
        return mainItemHandler;
    }

    public NonNullList<ItemStack> getDrops() {
        NonNullList<ItemStack> list = NonNullList.create();
        list.add(mainItemHandler.getStackInSlot(0));
        for(int i = 0; i < anvilHandler.getSlots() ; i++){
            list.add(anvilHandler.getStackInSlot(i));
        }
        return list;
    }

    public void syncToClient() {
        if (level instanceof ServerLevel sl) sl.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    public LazyOptional<IItemHandler> getProxiedCap() {
        return proxiedCap;
    }
}
