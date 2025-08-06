package moffy.ticex.entity.curios;

import moffy.ticex.TicEX;
import moffy.ticex.TicEXConfig;
import moffy.ticex.entity.ItemArrow;
import moffy.ticex.modules.general.TicEXRegistry;
import moffy.ticex.network.curios.TicEXSyncEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;

import javax.annotation.Nullable;

public class ResonanceToolProjectile extends ItemArrow {
    private boolean projectileHit;
    private int remainTicks;

    protected static final EntityDataAccessor<Integer> TARGET_TAG = SynchedEntityData.defineId(ResonanceToolProjectile.class,
            EntityDataSerializers.INT);

    public ResonanceToolProjectile(EntityType<? extends AbstractArrow> type, Level level){
        super(type, level);
    }

    @SuppressWarnings("unchecked")
    public ResonanceToolProjectile(@Nullable LivingEntity shooter, Level level) {
        super((EntityType<? extends AbstractArrow>) TicEXRegistry.RESONANCE_TOOL_PROJECTILE.get(), shooter, level);
        this.getEntityData().set(TARGET_TAG, shooter.getId());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(TARGET_TAG, -1);
    }

    @Nullable
    public LivingEntity getArrowTarget() {
        int id = getEntityData().get(TARGET_TAG);
        Entity entity = level().getEntity(id);
        if (entity instanceof LivingEntity livingEntity) return livingEntity;
        return null;
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        projectileHit = true;
        super.onHitBlock(pResult);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Entity target = pResult.getEntity();
        ItemStack toolStack = this.getItem();
        if (target == this.shooter || shooter == null) {
            return;
        }

        if (this.projectileHit) {
            return;
        }

        if (this.shooter instanceof Player player &&
                target instanceof LivingEntity livingTarget &&
                toolStack.getItem() instanceof IModifiable && !level().isClientSide) {
            livingTarget.invulnerableTime = 1;
            ToolAttackUtil.attackEntity(toolStack, player, livingTarget);
        }
        projectileHit = true;

        this.setDeltaMovement(this.getDeltaMovement().scale(-0.1));
        this.setYRot(this.getYRot() + 180.0F);
        this.yRotO += 180.0F;
    }

    @Override
    public void playSound(@NotNull SoundEvent pSound, float pVolume, float pPitch) {
    }

    public boolean isValidTarget(LivingEntity target) {
        if (target == shooter || shooter == null || target.isDeadOrDying()) {
            return false;
        }

        Vec3 start = shooter.getEyePosition();
        Vec3 end = target.getEyePosition();

        ClipContext context = new ClipContext(
                start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, shooter
        );
        BlockHitResult hit = target.level().clip(context);

        if (hit.getType() != HitResult.Type.MISS && hit.getLocation().distanceToSqr(start) < start.distanceToSqr(end)) {
            return false;
        }

        return true;
    }

    private boolean shouldRemoveProjectile(int maxRemainTicks) {
        return maxRemainTicks != -1 && remainTicks >= maxRemainTicks;
    }

    private void handleDespawn() {
        remainTicks++;

        int maxRemainTicks = TicEXConfig.GAUNTLET_REMAIN_TICKS.get();
        if (shouldRemoveProjectile(maxRemainTicks)) {
            discard();
        }
    }

    private void shootAtTarget(LivingEntity target) {
        final float velocity = 2.0F;
        final float inaccuracy = 1.0F;

        Vec3 targetPosition = new Vec3(
                target.getX(),
                target.getY(0.5D),
                target.getZ()
        );
        Vec3 velocityVec = getDeltaMovement()
                .normalize()
                .scale(distanceTo(target) * 0.1)
                .add(targetPosition.subtract(this.position()).normalize());

        this.shoot(velocityVec.x, velocityVec.y, velocityVec.z, velocity, inaccuracy);

        TicEXSyncEntity packet = new TicEXSyncEntity(this);
        TicEX.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), packet);
    }

    @Nullable
    public LivingEntity findNearestTarget() {
        return level().getNearestEntity(
                LivingEntity.class,
                TargetingConditions.forCombat()
                        .selector(this::isValidTarget),
                null,
                getX(),
                getY(),
                getZ(),
                getBoundingBox().inflate(35)
        );
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            return;
        }


        handleDespawn();

        if (this.isRemoved()) {
            return;
        }
        if (projectileHit) {
            return;
        }

        LivingEntity arrowTarget = getArrowTarget();

        if (arrowTarget == null || !isValidTarget(arrowTarget)) {
            arrowTarget = findNearestTarget();
            if (arrowTarget == null) {
                return;
            }

            getEntityData().set(TARGET_TAG, arrowTarget.getId());
        }

        shootAtTarget(arrowTarget);
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }
}
