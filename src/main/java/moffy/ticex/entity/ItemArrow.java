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
import org.jetbrains.annotations.Nullable;

public abstract class ItemArrow extends AbstractArrow {

    protected static final EntityDataAccessor<CompoundTag> ITEM_TAG = SynchedEntityData.defineId(ItemArrow.class,
            EntityDataSerializers.COMPOUND_TAG);

    @Nullable
    protected LivingEntity shooter;

    @Nullable
    protected ItemStack cacheStack = null;

    public ItemArrow(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);

        this.shooter = (LivingEntity) getOwner();
    }

    public ItemArrow(EntityType<? extends AbstractArrow> type, @Nullable LivingEntity shooter, Level level) {
        super(type, shooter, level);

        this.shooter = (LivingEntity) getOwner();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(ITEM_TAG, ItemStack.EMPTY.save(new CompoundTag()));
    }

    public ItemStack getItem(){
        if (cacheStack == null) {
            cacheStack = ItemStack.of(this.getEntityData().get(ITEM_TAG));
        }

        return cacheStack;
    }
    public void setItem(ItemStack stack){
        this.getEntityData().set(ITEM_TAG, stack.save(new CompoundTag()));
    }
}
