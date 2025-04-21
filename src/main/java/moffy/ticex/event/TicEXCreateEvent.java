package moffy.ticex.event;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.Cache;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.equipment.armor.CardboardArmorHandler;
import com.simibubi.create.content.logistics.box.PackageRenderer;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.utility.TickBasedCache;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import moffy.ticex.modules.TicEXRegistry;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingVisibilityEvent;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class TicEXCreateEvent {

    private static final Cache<UUID, Integer> BOXES_PLAYERS_ARE_HIDING_AS = new TickBasedCache<>(20, true);

    @SuppressWarnings({ "removal", "deprecation" })
    public static void playerHitboxChangesWhenHidingAsBox(EntityEvent.Size event) {
		Entity entity = event.getEntity();
		if (!entity.isAddedToWorld())
			return;
		if (!testForStealth(entity))
			return;
		
		event.setNewSize(EntityDimensions.fixed(0.6F, 0.8F));
		event.setNewEyeHeight(0.6F);
		
		if (!entity.level()
			.isClientSide() && entity instanceof Player p)
			AllAdvancements.CARDBOARD_ARMOR.awardTo(p);
	}

	public static void playersStealthWhenWearingCardboard(LivingVisibilityEvent event) {
		LivingEntity entity = event.getEntity();
		if (!testForStealth(entity))
			return;
		event.modifyVisibility(0);
	}

	public static void mobsMayLoseTargetWhenItIsWearingCardboard(LivingTickEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity.tickCount % 16 != 0)
			return;
		if (!(entity instanceof Mob mob))
			return;

		if (testForStealth(mob.getTarget())) {
			mob.setTarget(null);
			if (mob.targetSelector != null)
				mob.targetSelector.getRunningGoals()
					.forEach(wrappedGoal -> {
						if (wrappedGoal.getGoal() instanceof TargetGoal tg)
							tg.stop();
					});
		}

		if (entity instanceof NeutralMob nMob && entity.level() instanceof ServerLevel sl) {
			UUID uuid = nMob.getPersistentAngerTarget();
			if (uuid != null && testForStealth(sl.getEntity(uuid)))
				nMob.stopBeingAngry();
		}

		if (testForStealth(mob.getLastHurtByMob())) {
			mob.setLastHurtByMob(null);
			mob.setLastHurtByPlayer(null);
		}
	}

    @OnlyIn(Dist.CLIENT)
    public static void keepCacheAliveDesignDespiteNotRendering(PlayerTickEvent event) {
		if (event.phase == Phase.START)
			return;
		Player player = event.player;
		if (!CardboardArmorHandler.testForStealth(player))
			return;
		try {
			getCurrentBoxIndex(player);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

    @OnlyIn(Dist.CLIENT)
    public static void playerRendersAsBoxWhenSneaking(RenderPlayerEvent.Pre event) {
		Player player = event.getEntity();
		if (!CardboardArmorHandler.testForStealth(player))
			return;

		event.setCanceled(true);

		if (player == Minecraft.getInstance().player
			&& Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON)
			return;

		PoseStack ms = event.getPoseStack();
		ms.pushPose();
		ms.translate(0, 2 / 16f, 0);

		float movement = (float) player.position()
			.subtract(player.xo, player.yo, player.zo)
			.length();

		if (player.onGround())
			ms.translate(0,
				Math.min(Math.abs(Mth.cos((AnimationTickHolder.getRenderTime() % 256) / 2.0f)) * 2 / 16f, movement * 5),
				0);

		float interpolatedYaw = Mth.lerp(event.getPartialTick(), player.yRotO, player.getYRot());

		try {
			PartialModel model = AllPartialModels.PACKAGES_TO_HIDE_AS.get(getCurrentBoxIndex(player));
			PackageRenderer.renderBox(player, interpolatedYaw, ms, event.getMultiBufferSource(),
				event.getPackedLight(), model);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		ms.popPose();
	}

    public static boolean testForStealth(Entity entityIn) {
		if (!(entityIn instanceof LivingEntity entity))
			return false;
		if (entity.getPose() != Pose.CROUCHING)
			return false;
		if (entity instanceof Player player && player.getAbilities().flying)
			return false;
		for(ItemStack armorStack:entity.getArmorSlots()){
            if(!(armorStack.getItem() instanceof IModifiable))return false;

            ToolStack armor = ToolStack.from(armorStack);
            if(armor.getModifierLevel(TicEXRegistry.CARDBOARD_MODIFIER.get()) <= 0){
                return false;
            }
        }
		return true;
	}

    private static Integer getCurrentBoxIndex(Player player) throws ExecutionException {
		return BOXES_PLAYERS_ARE_HIDING_AS.get(player.getUUID(),
			() -> player.level().random.nextInt(AllPartialModels.PACKAGES_TO_HIDE_AS.size()));
	}
}
