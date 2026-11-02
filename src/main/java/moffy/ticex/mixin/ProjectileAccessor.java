package moffy.ticex.mixin;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Projectile.class)
public interface ProjectileAccessor {
    @Invoker("onHitEntity")
    void invokeHitEntity(EntityHitResult result);

    @Invoker("onHitBlock")
    void invokeHitBlock(BlockHitResult result);
}
