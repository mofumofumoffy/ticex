package moffy.ticex.event;

import mods.flammpfeil.slashblade.event.BladeMotionEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import moffy.ticex.TicEX;
import moffy.ticex.client.render.slashblade.KoshiraeIconDecorator;
import moffy.ticex.client.render.slashblade.SBToolBladeItemRenderer;
import moffy.ticex.item.modifiable.ModifiableSlashBladeItem;
import moffy.ticex.modules.general.TicEXRegistry;
import moffy.ticex.modules.slashblade.IInputCommandEvent;
import moffy.ticex.network.slashblade.StateSyncPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterItemDecorationsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TicEXSBEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(TicEXSBEvent.class);

    public static void onBladeMotion(BladeMotionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncState(player);
        }
    }

    public static void onLivingFall(LivingFallEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncState(player);
        }
    }

    public static void onPlayerFlyableFall(PlayerFlyableFallEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncState(player);
        }
    }

    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            syncState(player);
        }
    }

    public static void onLivingExperienceDrop(LivingExperienceDropEvent event) {
        if (event.getAttackingPlayer() instanceof ServerPlayer player) {
            syncState(player);
        }
    }

    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
                syncState(player);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerItemDecorators(RegisterItemDecorationsEvent event) {
        event.register(TicEXRegistry.REFORGED_SLASHBLADE.get(), new KoshiraeIconDecorator());
    }

    @SuppressWarnings("unchecked")
    public static void registerEventsByVersion() {
        Class<Event> eventType = null;
        try { eventType = (Class<Event>) Class.forName("mods.flammpfeil.slashblade.event.handler.InputCommandEvent"); } catch (ClassNotFoundException ignored) { }
        try { eventType = (Class<Event>) Class.forName("mods.flammpfeil.slashblade.event.InputCommandEvent"); } catch (ClassNotFoundException ignored) { }
        if(eventType == null) {
            LOGGER.error("InputCommandEvent not found");
            return;
        }

        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL,  false, eventType, event -> {
            if(event instanceof IInputCommandEvent iInputCommandEvent) {
                syncState(iInputCommandEvent.ticex$getEntity());
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(
                TicEXRegistry.SLASHBLADE_TOOL_ITEM_ENTITY.get(),
            SBToolBladeItemRenderer::new
        );
    }

    public static void syncState(ServerPlayer player) {
        ItemStack mainHandStack = player.getMainHandItem();
        if (mainHandStack.getItem() instanceof ModifiableSlashBladeItem) {
            mainHandStack
                .getCapability(ItemSlashBlade.BLADESTATE)
                .ifPresent(state -> {
                    CompoundTag nbt = state.serializeNBT();
                    mainHandStack.getOrCreateTag().put("bladeState", nbt.copy());
                    StateSyncPacket packet = new StateSyncPacket(nbt);
                    TicEX.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
                });
        }
    }
}
