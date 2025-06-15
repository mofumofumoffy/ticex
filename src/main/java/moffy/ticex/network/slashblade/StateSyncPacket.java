package moffy.ticex.network.slashblade;

import java.util.function.Supplier;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import slimeknights.tconstruct.library.tools.item.IModifiable;

public class StateSyncPacket {
    protected CompoundTag stateNbt;

    public StateSyncPacket(CompoundTag stateNbt){
        this.stateNbt = stateNbt;
    }

    public CompoundTag getStateNbt() {
        return stateNbt;
    }

    public static StateSyncPacket decode(FriendlyByteBuf buf){
        return new StateSyncPacket(buf.readAnySizeNbt());
    }

    public static void encode(StateSyncPacket packet, FriendlyByteBuf buf) {
        buf.writeNbt(packet.stateNbt);
    }

    public static void handle(StateSyncPacket packet, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            if (Minecraft.getInstance().level != null) {
                ItemStack mainHandStack = Minecraft.getInstance().player.getMainHandItem();

                if(mainHandStack.getItem() instanceof IModifiable){
                    mainHandStack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent((stateClient)->{
                        stateClient.deserializeNBT(packet.getStateNbt());
                    });
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
