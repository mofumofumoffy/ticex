package moffy.ticex.network.curios;

import moffy.ticex.network.TicEXPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TicEXSyncEntityMovements extends TicEXPacket.ServerBoundPacket {
    public final int entityId;
    public final Vec3 position;
    public final Vec3 velocityVec;
    public final Vec2 rotationVec;

    public TicEXSyncEntityMovements(Entity entity) {
        this(
                entity.getId(),
                entity.position(),
                entity.getDeltaMovement(),
                entity.getRotationVector()
        );
    }

    public TicEXSyncEntityMovements(int entityId, Vec3 position, Vec3 velocityVec, Vec2 rotationVec) {
        this.entityId = entityId;
        this.position = position;
        this.velocityVec = velocityVec;
        this.rotationVec = rotationVec;
    }

    public TicEXSyncEntityMovements(FriendlyByteBuf buf) {
        super(buf);
        this.entityId = buf.readVarInt();
        this.position = new Vec3(buf.readVector3f());
        this.velocityVec = new Vec3(buf.readVector3f());
        this.rotationVec = new Vec2(buf.readFloat(), buf.readFloat());
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeVector3f(this.position.toVector3f());
        buf.writeVector3f(this.velocityVec.toVector3f());
        buf.writeFloat(this.rotationVec.x);
        buf.writeFloat(this.rotationVec.y);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            Entity entity = level.getEntity(entityId);

            entity.setPos(this.position);
            entity.setDeltaMovement(this.velocityVec);

            entity.setXRot(this.rotationVec.x);
            entity.setYRot(this.rotationVec.y);
        });

        context.setPacketHandled(true);
    }
}
