package moffy.ticex.network.curios;

import moffy.ticex.item.ResonanceTools;
import moffy.ticex.network.TicEXPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class TicEXShootGauntletPacket extends TicEXPacket.ServerBoundPacket {
    private final int playerId;

    public TicEXShootGauntletPacket(int playerId) {
        this.playerId = playerId;
    }

    public TicEXShootGauntletPacket(FriendlyByteBuf buf) {
        this.playerId = buf.readVarInt();
    }

    public void encode(@NotNull FriendlyByteBuf buf) {
        buf.writeVarInt(playerId);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        if (!ModList.get().isLoaded("curios")) {
            return;
        }

        NetworkEvent.Context context = contextSupplier.get();

        context
                .enqueueWork(() -> {
                    ServerPlayer sender = context.getSender();
                    if (playerId == sender.getId()) {
                        ResonanceTools.shoot(sender);
                    }
                });

        context.setPacketHandled(true);
    }
}
