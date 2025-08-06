package moffy.ticex.event;

import moffy.ticex.TicEX;
import moffy.ticex.TicEXConfig;
import moffy.ticex.caps.curios.GauntletItemHandler;
import moffy.ticex.client.modules.curios.LayerResonanceTools;
import moffy.ticex.client.modules.curios.ResonanceToolProjectileRenderer;
import moffy.ticex.client.modules.ticex.TicEXKeyBindings;
import moffy.ticex.entity.curios.ResonanceToolProjectile;
import moffy.ticex.modules.general.TicEXRegistry;
import moffy.ticex.network.curios.TicEXShootGauntletPacket;
import moffy.ticex.network.curios.TicEXSyncEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;

public class TicEXCuriosEvent {
    public static void onLivingDeath(LivingDeathEvent event){
        LivingEntity livingEntity = event.getEntity();
        Entity sourceEntity = event.getSource().getEntity();
        Level level = livingEntity.level();
        if (!level.isClientSide() && sourceEntity instanceof Player) {
            RandomSource randomSource = level.getRandom();
            List<String> blacklist = TicEXConfig.GLOVE_DROP_BLACKLIST.get();
            ResourceLocation entityLocation = ForgeRegistries.ENTITY_TYPES.getKey(livingEntity.getType());
            if (blacklist.stream().anyMatch(id -> entityLocation.toString().equals(id))) {
                return;
            }

            if(randomSource.nextIntBetweenInclusive(0, 1000) <= 0){
                level.addFreshEntity(new ItemEntity(level, livingEntity.getX(), livingEntity.getY() - 1, livingEntity.getZ(), new ItemStack(TicEXRegistry.EXHAUSTED_GLOVE.get())));
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        addPlayerLayer(event, "default");
        addPlayerLayer(event, "slim");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @OnlyIn(Dist.CLIENT)
    public static void addPlayerLayer(EntityRenderersEvent.AddLayers event, String skin) {
        EntityRenderer<? extends Player> renderer = event.getSkin(skin);
        if (renderer instanceof LivingEntityRenderer livingRenderer) {
            livingRenderer.addLayer(new LayerResonanceTools<>(livingRenderer));
        }
    }

    @SuppressWarnings("unchecked")
    @OnlyIn(Dist.CLIENT)
    public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer((EntityType <ResonanceToolProjectile>) TicEXRegistry.RESONANCE_TOOL_PROJECTILE.get(), pContext ->
            new ResonanceToolProjectileRenderer(pContext, 2f)
        );
    }

    public static void shootGauntletTools(Player player) {
        CuriosApi.getCuriosInventory(player).ifPresent(iCuriosItemHandler -> {
            iCuriosItemHandler.findFirstCurio(TicEXRegistry.RESONANCE_GAUNTLET.get()).ifPresent(slotResult -> {
                ItemStack gauntletStack = slotResult.stack();
                gauntletStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler -> {
                    if (!(itemHandler instanceof GauntletItemHandler gauntletItemHandler)) {
                        return;
                    }

                    List<Integer> availableSlots = new ArrayList<>();
                    for (int i = 0; i < itemHandler.getSlots(); i++) {
                        if (!itemHandler.getStackInSlot(i).isEmpty() && gauntletItemHandler.getItemCooldown(i) == 0) {
                            availableSlots.add(i);
                        }
                    }

                    if (availableSlots.isEmpty()) {
                        return;
                    }

                    int shootSlot = availableSlots.get(player.getRandom().nextIntBetweenInclusive(0, availableSlots.size() - 1));

                    ItemStack toolStack = itemHandler.getStackInSlot(shootSlot);

                    float time = player.tickCount;
                    double baseAngle = 2 * Math.PI / availableSlots.size() * shootSlot;
                    double rotationAngle = baseAngle + (time * 0.07);

                    double x = LayerResonanceTools.RADIUS * Math.cos(-rotationAngle);
                    double z = LayerResonanceTools.RADIUS * Math.sin(-rotationAngle);

                    Vec3 originPos = new Vec3(x, 1.25f, 0.5 + z)
                            .add(player.position());

                    shootTool(player, toolStack, originPos);

                    gauntletItemHandler.setItemCooldown(shootSlot, 15);
                });
            });
        });
    }

    public static void shootTool(Player player, ItemStack stack, Vec3 pos) {
        Level level = player.level();
        if (!level.isClientSide) {
            ResonanceToolProjectile arrow = new ResonanceToolProjectile(player, level);
            arrow.setPos(pos);
            arrow.setItem(stack);

            Vec3 lookAngle = player.getLookAngle();

            float velocity = 3.0f;
            float inaccuracy = 0.0f;

            arrow.shoot(lookAngle.x, lookAngle.y, lookAngle.z, velocity, inaccuracy);
            level.addFreshEntity(arrow);

            TicEXSyncEntity packet = new TicEXSyncEntity(arrow);
            TicEX.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> arrow), packet);
        }
    }

    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(TicEXKeyBindings.SHOOT_GAUNTLET.get());
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            while (TicEXKeyBindings.SHOOT_GAUNTLET.get().consumeClick()) {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player == null) continue;

                TicEXShootGauntletPacket packet = new TicEXShootGauntletPacket(player.getId());
                TicEX.CHANNEL.sendToServer(packet);
            }
        }
    }
}
