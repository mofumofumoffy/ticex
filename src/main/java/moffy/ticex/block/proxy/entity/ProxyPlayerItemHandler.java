package moffy.ticex.block.proxy.entity;

import com.mojang.authlib.GameProfile;
import moffy.ticex.TicEX;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.PlayerArmorInvWrapper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import net.minecraftforge.items.wrapper.PlayerOffhandInvWrapper;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProxyPlayerItemHandler extends ItemStackHandler {
    protected final ProxyBlockEntity proxyBlockEntity;
    protected final UUID uuid;

    public ProxyPlayerItemHandler(ProxyBlockEntity proxyBlockEntity, UUID uuid){
        super();
        this.proxyBlockEntity = proxyBlockEntity;
        this.uuid = uuid;
    }

    @SuppressWarnings({"removal", "UnstableApiUsage", "deprecation"})
    public @Nullable IItemHandler getHandler(){
        UUID placerUUID = proxyBlockEntity.getPlacerUUID();
        if(placerUUID != null && proxyBlockEntity.getPlacerUUID().equals(this.uuid) && this.proxyBlockEntity.getLevel() instanceof ServerLevel serverLevel){
            Player player = serverLevel.getServer().getPlayerList().getPlayer(this.uuid);
            if (player == null || player.isRemoved()) return null;

            // ---- Player inventory wrappers ----
            Inventory inv = player.getInventory();
            List<IItemHandlerModifiable> parts = new ArrayList<>();
            parts.add(new PlayerMainInvWrapper(inv));
            parts.add(new PlayerArmorInvWrapper(inv));
            parts.add(new PlayerOffhandInvWrapper(inv));

            // ---- Curios (if loaded) ----
            if (ModList.get().isLoaded("curios")) {
                CuriosApi.getCuriosHelper().getCuriosHandler(player).ifPresent(curios -> {
                    curios.getCurios().forEach((slotId, stacksHandler) -> {
                        IItemHandlerModifiable equip = stacksHandler.getStacks();
                        parts.add(equip);
                    });
                });
            }

            if (parts.isEmpty()) return null;
            return new CombinedInvWrapper(parts.toArray(IItemHandlerModifiable[]::new));
        }

        return null;
    }

    public static @Nullable UUID getUUIDFromHead(ItemStack stack, @Nullable Level level) {
        if (!(stack.is(Items.PLAYER_HEAD))) return null;

        CompoundTag tag = stack.getTag();
        if (tag == null) return null;

        if (!tag.contains("SkullOwner")) return null;

        GameProfile profile = null;

        UUIDHolder uuidHolder = new UUIDHolder();

        if (tag.get("SkullOwner") instanceof CompoundTag owner) {
            profile = NbtUtils.readGameProfile(owner);
            if (profile != null && profile.getId() != null) {
                uuidHolder.setUUID(profile.getId());
            }
        }

        else if (tag.contains("SkullOwner", Tag.TAG_STRING)) {
            String name = tag.getString("SkullOwner");
            if (!name.isBlank()) {
                profile = new GameProfile(null, name);
            }
        }

        if (uuidHolder.isNull() && level instanceof ServerLevel) {
            SkullBlockEntity.updateGameprofile(profile, (completed)->{
                uuidHolder.setUUID(completed.getId());
            });
        }
        return uuidHolder.getUUID();
    }

    @Override
    public int getSlots() {
        IItemHandler playerHandler = getHandler();
        if(playerHandler != null){
            return playerHandler.getSlots();
        }
        return super.getSlots();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        IItemHandler playerHandler = getHandler();
        if(playerHandler != null){
            return playerHandler.getStackInSlot(slot);
        }
        return super.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        IItemHandler playerHandler = getHandler();
        if(playerHandler != null){
            playerHandler.insertItem(slot, stack, simulate);
        }
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        IItemHandler playerHandler = getHandler();
        if(playerHandler != null){
            return playerHandler.extractItem(slot, amount, simulate);
        }
        return super.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        IItemHandler playerHandler = getHandler();
        if(playerHandler != null){
            return playerHandler.getSlotLimit(slot);
        }
        return super.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        IItemHandler playerHandler = getHandler();
        if(playerHandler != null){
            return playerHandler.isItemValid(slot, stack);
        }
        return super.isItemValid(slot, stack);
    }

    protected static class UUIDHolder{
        private UUID uuid;

        public UUIDHolder(){
            uuid = null;
        }

        public boolean isNull(){
            return uuid == null;
        }

        public UUID getUUID() {
            return uuid;
        }

        public void setUUID(UUID uuid) {
            this.uuid = uuid;
        }
    }
}
