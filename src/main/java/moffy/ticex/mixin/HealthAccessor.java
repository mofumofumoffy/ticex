package moffy.ticex.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.LivingEntity;

@Mixin(LivingEntity.class)
public interface HealthAccessor {
    @Accessor("DATA_HEALTH_ID")
    public EntityDataAccessor<Float> getTicEXHealthDataKey(); 
}
