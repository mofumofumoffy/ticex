package moffy.ticex.network.slashblade;

import java.util.function.Supplier;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import slimeknights.tconstruct.library.tools.item.IModifiable;

public class StateSyncPacket {
    protected ItemStack bladeStack;

    public StateSyncPacket(ItemStack bladeStack){
        this.bladeStack = bladeStack;
    }

    public ItemStack getBladeStack() {
        return bladeStack;
    }

    public static StateSyncPacket decode(FriendlyByteBuf buf){
        return new StateSyncPacket(buf.readItem());
    }

    public static void encode(StateSyncPacket packet, FriendlyByteBuf buf) {
        buf.writeItem(packet.getBladeStack());
    }

    public static void handle(StateSyncPacket packet, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            if (Minecraft.getInstance().level != null) {
                ItemStack mainHandStack = Minecraft.getInstance().player.getMainHandItem();

                if(mainHandStack.getItem() instanceof IModifiable){
                    mainHandStack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent((stateClient)->{
                        ItemStack sent = packet.getBladeStack();

                        if(sent.getItem() instanceof IModifiable){
                            sent.getCapability(ItemSlashBlade.BLADESTATE).ifPresent((stateServer)->{
                                stateClient.deserializeNBT(sent.getOrCreateTag().getCompound("bladeState"));
                            });
                        }
                    });
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
