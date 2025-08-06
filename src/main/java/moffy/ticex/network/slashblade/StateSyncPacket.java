package moffy.ticex.network.slashblade;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import moffy.ticex.network.TicEXPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import slimeknights.tconstruct.library.tools.item.IModifiable;

import java.util.function.Supplier;

public class StateSyncPacket extends TicEXPacket.ClientBoundPacket {

    protected CompoundTag stateNbt;

    public StateSyncPacket(CompoundTag stateNbt) {
        this.stateNbt = stateNbt;
    }

    public StateSyncPacket(FriendlyByteBuf buf) {
        this.stateNbt = buf.readNbt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(stateNbt);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx
            .get()
            .enqueueWork(() -> {
                if (Minecraft.getInstance().level != null) {
                    ItemStack mainHandStack = Minecraft.getInstance().player.getMainHandItem();

                    if (mainHandStack.getItem() instanceof IModifiable) {
                        mainHandStack
                            .getCapability(ItemSlashBlade.BLADESTATE)
                            .ifPresent(stateClient -> {
                                stateClient.deserializeNBT(stateNbt);
                            });
                    }
                }
            });

        ctx.get().setPacketHandled(true);
    }
}
