package moffy.ticex.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class ItemArrow extends AbstractArrow{

    protected LivingEntity shooter;

    public ItemArrow(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
    }

    public ItemArrow(EntityType<? extends AbstractArrow> type, LivingEntity shooter, Level level){
        super(type, shooter, level);
        this.shooter = shooter;
    }
    
    public abstract ItemStack getItem();
}
