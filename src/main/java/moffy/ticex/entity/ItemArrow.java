package moffy.ticex.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class ItemArrow extends AbstractArrow {

    protected static final EntityDataAccessor<CompoundTag> ITEM_TAG = SynchedEntityData.defineId(ItemArrow.class,
            EntityDataSerializers.COMPOUND_TAG);

    protected LivingEntity shooter;

    public ItemArrow(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
    }

    public ItemArrow(EntityType<? extends AbstractArrow> type, LivingEntity shooter, Level level) {
        super(type, shooter, level);
        this.shooter = shooter;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(ITEM_TAG, ItemStack.EMPTY.save(new CompoundTag()));
    }

    public ItemStack getItem(){
        return ItemStack.of(this.getEntityData().get(ITEM_TAG));
    }
    public void setItem(ItemStack stack){
        this.getEntityData().set(ITEM_TAG, stack.save(new CompoundTag()));
    }
}
