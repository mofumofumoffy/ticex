package moffy.ticex.network.curios;

import moffy.ticex.event.TicEXCuriosEvent;
import moffy.ticex.network.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class TicEXShootGauntletPacket implements IPacket {
    private final int playerId;

    public TicEXShootGauntletPacket(int playerId) {
        this.playerId = playerId;
    }

    public static TicEXShootGauntletPacket decode(FriendlyByteBuf buf) {
        int playerId = buf.readVarInt();
        return new TicEXShootGauntletPacket(
                playerId
        );
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
                        TicEXCuriosEvent.shootGauntletTools(sender, null);
                    }
                });
    }
}
