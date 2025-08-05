package moffy.ticex.event;

import moffy.ticex.TicEX;
import moffy.ticex.client.modules.curios.LayerResonanceTools;
import moffy.ticex.client.modules.curios.ResonanceToolProjectileRenderer;
import moffy.ticex.client.modules.ticex.TicEXKeyBindings;
import moffy.ticex.entity.curios.ResonanceToolProjectile;
import moffy.ticex.modules.general.TicEXRegistry;
import moffy.ticex.network.curios.TicEXShootGauntletPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TicEXCuriosEvent {
    public static void onLivingDeath(LivingDeathEvent event){
        LivingEntity livingEntity = event.getEntity();
        Level level = livingEntity.level();
        if(!level.isClientSide()){
            RandomSource randomSource = level.getRandom();
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

    public static void shootGauntletTools(Player player, @Nullable LivingEntity target){
        CuriosApi.getCuriosInventory(player).ifPresent(iCuriosItemHandler -> {
            iCuriosItemHandler.findFirstCurio(TicEXRegistry.RESONANCE_GAUNTLET.get()).ifPresent(slotResult -> {
                ItemStack gauntletStack = slotResult.stack();
                gauntletStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
                    LivingEntity arrowTarget = target;
                    if(arrowTarget == null){
                        arrowTarget = player.level().getNearestEntity(
                                LivingEntity.class,
                                TargetingConditions.forCombat()
                                        .selector(livingEntity -> livingEntity != player),
                                player,
                                player.getX(),
                                player.getY(),
                                player.getZ(),
                                player.getBoundingBox().inflate(48)
                        );
                    }
                    List<Integer> availableSlots = new ArrayList<>();
                    for(int i = 0; i < iItemHandler.getSlots(); i++){
                        if(!iItemHandler.getStackInSlot(i).isEmpty()){
                            availableSlots.add(i);
                        }
                    }
                    if(!availableSlots.isEmpty()){
                        ItemStack toolStack = iItemHandler.getStackInSlot(availableSlots.get(player.getRandom().nextIntBetweenInclusive(0, availableSlots.size() - 1)));
                        if(arrowTarget != null){
                            shootTool(player, arrowTarget, toolStack);
                        }
                    }
                });
            });
        });
    }

    public static void shootTool(Player player, LivingEntity livingTarget, ItemStack toolStack){
        ResonanceToolProjectile arrow = new ResonanceToolProjectile(player, player.level());
        arrow.setPos(player.getEyePosition().add(player.getLookAngle().scale(1)));

        arrow.setItem(toolStack);
        double dx = livingTarget.getX() - player.getX();
        double dy = livingTarget.getY(0.5D) - player.getEyeY();
        double dz = livingTarget.getZ() - player.getZ();
        player.level().addFreshEntity(arrow);

        arrow.shoot(dx, dy, dz, 3.0F, 1.0F);
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
