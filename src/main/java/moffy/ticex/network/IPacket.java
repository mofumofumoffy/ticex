package moffy.ticex.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface IPacket {
    void encode(FriendlyByteBuf buf);

    void handle(Supplier<NetworkEvent.Context> contextSupplier);
}
