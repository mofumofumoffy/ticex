package moffy.ticex.entity;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FakeLivingEntity extends LivingEntity {

    private final List<ItemStack> emptyList;
    private float fakeHealth;

    public FakeLivingEntity(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.emptyList = new ArrayList<>();
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return emptyList;
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot pSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack) {}

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        this.fakeHealth = this.fakeHealth - pAmount;
        return false;
    }

    @Override
    public void setHealth(float pHealth) {
        this.fakeHealth = pHealth;
    }

    public float getFakeHealth() {
        return this.fakeHealth;
    }

    @Override
    public void kill() {}
}
