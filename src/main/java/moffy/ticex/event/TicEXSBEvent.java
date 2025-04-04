package moffy.ticex.event;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import mods.flammpfeil.slashblade.capability.inputstate.CapabilityInputState;
import mods.flammpfeil.slashblade.event.InputCommandEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.InputCommand;
import mods.flammpfeil.slashblade.util.RayTraceHelper;
import mods.flammpfeil.slashblade.util.TargetSelector;
import moffy.ticex.TicEX;
import moffy.ticex.client.slashblade.LayerSBToolMainBlade;
import moffy.ticex.client.slashblade.SBToolBladeItemRenderer;
import moffy.ticex.entity.slashblade.SBToolItemEntity;
import moffy.ticex.item.modifiable.ModifiableSlashBladeItem;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.TickEvent;

public class TicEXSBEvent {

    public static void onInputChange(InputCommandEvent event) {
        ServerPlayer player = event.getEntity();
        ItemStack stack = event.getEntity().getMainHandItem();
        if (stack.isEmpty())
            return;
        if (!(stack.getItem() instanceof ModifiableSlashBladeItem))
            return;

        Entity targetEntity;
        
        if (event.getOld().contains(InputCommand.SNEAK) == event.getCurrent().contains(InputCommand.SNEAK))
            return;
        
        if ((event.getOld().contains(InputCommand.SNEAK) && !event.getCurrent().contains(InputCommand.SNEAK))) {
            // remove target
            targetEntity = null;
        } else {
            // search target

            Optional<HitResult> result = RayTraceHelper.rayTrace(player.level(), player, player.getEyePosition(1.0f),
                    player.getLookAngle(), 40, 40, (e) -> true);
            Optional<Entity> foundEntity = result.filter(r -> r.getType() == HitResult.Type.ENTITY).filter(r -> {
                EntityHitResult er = (EntityHitResult) r;
                Entity target = er.getEntity();

                if (target instanceof PartEntity) {
                    target = ((PartEntity<?>) target).getParent();
                }

                boolean isMatch = false;

                if (target instanceof LivingEntity)
                    isMatch = TargetSelector.lockon.test(player, (LivingEntity) target);

                return isMatch;
            }).map(r -> ((EntityHitResult) r).getEntity());

            if (!foundEntity.isPresent()) {
                List<LivingEntity> entities = player.level().getNearbyEntities(LivingEntity.class,
                        TargetSelector.lockon, player, player.getBoundingBox().inflate(12.0D, 6.0D, 12.0D));

                foundEntity = entities.stream().map(s -> (Entity) s)
                        .min(Comparator.comparingDouble(e -> e.distanceToSqr(player)));
            }

            targetEntity = foundEntity.map(e -> (e instanceof PartEntity) ? ((PartEntity<?>) e).getParent() : e)
                    .orElse(null);

        }

        stack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(s -> {
            s.setTargetEntityId(targetEntity);
        });
    }

    @OnlyIn(Dist.CLIENT)
    public static void onEntityUpdate(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.START)
            return;

        final Minecraft mcinstance = Minecraft.getInstance();
		if (mcinstance.player == null)
            return;

        LocalPlayer player = mcinstance.player;

        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty())
            return;
        if (!(stack.getItem() instanceof ModifiableSlashBladeItem))
            return;

        stack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(s -> {

            Entity target = s.getTargetEntity(player.level());

            if (target == null)
                return;
            if (!target.isAlive())
                return;

            LivingEntity entity = player;

            if (!entity.level().isClientSide())
                return;
            if (!entity.getCapability(CapabilityInputState.INPUT_STATE)
                    .filter(input -> input.getCommands().contains(InputCommand.SNEAK)).isPresent())
                return;

            float partialTicks = mcinstance.getFrameTime();

            float oldYawHead = entity.yHeadRot;
            float oldYawOffset = entity.yBodyRot;
            float oldPitch = entity.getXRot();
            float oldYaw = entity.getYRot();

            float prevYawHead = entity.yHeadRotO;
            float prevYawOffset = entity.yBodyRotO;
            float prevYaw = entity.yRotO;
            float prevPitch = entity.xRotO;

            entity.lookAt(EntityAnchorArgument.Anchor.EYES, target.position().add(0, target.getEyeHeight() / 2.0, 0));

            float step = 0.125f * partialTicks;

            step *= Math.min(1.0f, Math.abs(Mth.wrapDegrees(oldYaw - entity.yHeadRot) * 0.5));

            entity.setXRot(Mth.rotLerp(step, oldPitch, entity.getXRot()));
            entity.setYRot(Mth.rotLerp(step, oldYaw, entity.getYRot()));
            entity.setYHeadRot(Mth.rotLerp(step, oldYawHead, entity.getYHeadRot()));

            entity.yBodyRot = oldYawOffset;

            entity.yBodyRotO = prevYawOffset;
            entity.yHeadRotO = prevYawHead;
            entity.yRotO = prevYaw;
            entity.xRotO = prevPitch;
        });
    }


    @OnlyIn(Dist.CLIENT)
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        addPlayerLayer(event, "default");
        addPlayerLayer(event, "slim");

        addEntityLayer(event, EntityType.ZOMBIE);
        addEntityLayer(event, EntityType.HUSK);
        addEntityLayer(event, EntityType.ZOMBIE_VILLAGER);

        addEntityLayer(event, EntityType.WITHER_SKELETON);
        addEntityLayer(event, EntityType.SKELETON);
        addEntityLayer(event, EntityType.STRAY);

        addEntityLayer(event, EntityType.PIGLIN);
        addEntityLayer(event, EntityType.PIGLIN_BRUTE);
        addEntityLayer(event, EntityType.ZOMBIFIED_PIGLIN);
    }

    @SuppressWarnings({ "unchecked" })
    public static void addPlayerLayer(EntityRenderersEvent.AddLayers evt, String skin) {
        EntityRenderer<? extends Player> renderer = evt.getSkin(skin);

        if (renderer instanceof LivingEntityRenderer livingRenderer) {
            livingRenderer.addLayer(new LayerSBToolMainBlade<>(livingRenderer));
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void addEntityLayer(EntityRenderersEvent.AddLayers evt, EntityType type) {
        EntityRenderer<?> renderer = evt.getRenderer(type);

        if (renderer instanceof LivingEntityRenderer livingRenderer) {
            livingRenderer.addLayer(new LayerSBToolMainBlade<>(livingRenderer));
        }
    }

    public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer((EntityType<SBToolItemEntity>)TicEXRegistry.SLASHBLADE_TOOL_ITEM_ENTITY.get(), SBToolBladeItemRenderer::new);
    }
}
