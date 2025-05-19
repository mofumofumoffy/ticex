package moffy.ticex.entity.avaritia;

import moffy.ticex.entity.ItemArrow;
import moffy.ticex.modules.general.TicEXRegistry;
import committee.nova.mods.avaritia.common.entity.GapingVoidEntity;
import committee.nova.mods.avaritia.init.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class EndestShotProjectile extends ItemArrow{

    private LivingEntity shooter;

    public EndestShotProjectile(EntityType<? extends EndestShotProjectile> type, Level level) {
        super(type, level);
    }

    public EndestShotProjectile(Level level, LivingEntity shooter) {
        super(level, shooter);
    }

    public void setShooter(LivingEntity shooter){
        this.shooter = shooter;
    }

    public ItemStack getItem(){
        return new ItemStack(ModItems.endest_pearl.get());
    }

    public void onHitEntity(EntityHitResult result){
        Entity entity = result.getEntity();

        if (!this.level().isClientSide) {
                GapingVoidEntity ent;
            if (this.shooter != null) {
                ent = new GapingVoidEntity(this.level(), this.shooter);
            } else {
                ent = new GapingVoidEntity(this.level());
            }
            Direction dir = entity.getDirection();
            Vec3 offset = Vec3.ZERO;
            if (dir != null) {
                offset = new Vec3((double)dir.getStepX(), (double)dir.getStepY(), (double)dir.getStepZ());
            }
            if (this.shooter != null) {
                ent.setUser(this.shooter);
            }

            ent.moveTo(entity.getX() + offset.x * 0.25, entity.getY() + offset.y * 0.25, entity.getZ() + offset.z * 0.25, entity.getYRot(), 0.0F);
            this.level().addFreshEntity(ent);
            this.remove(RemovalReason.KILLED);
        }
    }

    public void onHitBlock(BlockHitResult result){
        super.onHitBlock(result);
        BlockPos pos = result.getBlockPos();
        if (!this.level().isClientSide) {
            GapingVoidEntity ent;
            if (this.shooter != null) {
                ent = new GapingVoidEntity(this.level(), this.shooter);
            } else {
                ent = new GapingVoidEntity(this.level());
            }

            Direction dir = result.getDirection();
            Vec3 offset = Vec3.ZERO;
            if (dir != null) {
                offset = new Vec3((double)dir.getStepX(), (double)dir.getStepY(), (double)dir.getStepZ());
            }

            if (this.shooter != null) {
                ent.setUser(this.shooter);
            }

            ent.moveTo((double)pos.getX() + offset.x * 0.25, (double)pos.getY() + offset.y * 0.25, (double)pos.getZ() + offset.z * 0.25, this.getYRot(), 0.0F);
            this.level().addFreshEntity(ent);
            this.remove(RemovalReason.KILLED);
        }
    }
}
