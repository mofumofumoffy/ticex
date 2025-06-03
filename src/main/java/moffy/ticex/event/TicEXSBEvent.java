package moffy.ticex.event;

import mods.flammpfeil.slashblade.event.BladeMotionEvent;
import mods.flammpfeil.slashblade.event.InputCommandEvent;
import moffy.ticex.TicEX;
import moffy.ticex.item.modifiable.ModifiableSlashBladeItem;
import moffy.ticex.network.slashblade.StateSyncPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.network.PacketDistributor;

public class TicEXSBEvent {
    public static void onBladeMotion(BladeMotionEvent event) {
        if(event.getEntity() instanceof ServerPlayer player){
            syncState(player);
        }
    }

    public static void onLivingFall(LivingFallEvent event) {
        if(event.getEntity() instanceof ServerPlayer player){
            syncState(player);
        }
    }

    public static void onPlayerFlyableFall(PlayerFlyableFallEvent event) {
        if(event.getEntity() instanceof ServerPlayer player){
            syncState(player);
        }
    }

    public static void onLivingDeath(LivingDeathEvent event) {
        if(event.getSource().getEntity() instanceof ServerPlayer player){
            syncState(player);
        }
    }

    public static void onLivingExperienceDrop(LivingExperienceDropEvent event) {
        if(event.getAttackingPlayer() instanceof ServerPlayer player){
            syncState(player);
        }
    }

    public static void onLivingHurt(LivingHurtEvent event) {
        if(event.getSource().getEntity() instanceof ServerPlayer player){
            syncState(player);
        }
    }

    public static void onInputCommand(InputCommandEvent event) {
        syncState(event.getEntity());
    }

    public static void syncState(ServerPlayer player){
        ItemStack mainHandStack = player.getMainHandItem();
        if(mainHandStack.getItem() instanceof ModifiableSlashBladeItem){
            StateSyncPacket packet = new StateSyncPacket(mainHandStack);
            TicEX.CHANNEL.send(PacketDistributor.PLAYER.with(()->player), packet);
        }
    }
}
