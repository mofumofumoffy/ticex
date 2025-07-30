package moffy.ticex.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class ResonanceToolProjectile extends ItemArrow{

    protected ItemStack toolStack;

    public ResonanceToolProjectile(EntityType<? extends AbstractArrow> type, LivingEntity shooter, Level level, ItemStack toolStack) {
        super(type, shooter, level);
        this.toolStack = toolStack;
    }

    @Override
    public ItemStack getItem() {
        return toolStack;
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        if(!this.level().isClientSide()){
            this.kill();
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

}
