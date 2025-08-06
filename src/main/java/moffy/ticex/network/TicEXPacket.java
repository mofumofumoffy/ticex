package moffy.ticex.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class TicEXPacket {
    public TicEXPacket() {
    }

    public TicEXPacket(FriendlyByteBuf buf) {
    }

    public abstract void encode(FriendlyByteBuf buf);

    public abstract void handle(Supplier<NetworkEvent.Context> contextSupplier);

    public abstract static class ServerBoundPacket extends TicEXPacket {
        public ServerBoundPacket() {
        }

        public ServerBoundPacket(FriendlyByteBuf buf) {
            super(buf);
        }
    }

    public abstract static class ClientBoundPacket extends TicEXPacket {
        public ClientBoundPacket() {
        }

        public ClientBoundPacket(FriendlyByteBuf buf) {
            super(buf);
        }
    }
}
