package moffy.ticex.mixin.slashblade;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import mods.flammpfeil.slashblade.entity.IShootable;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import slimeknights.tconstruct.library.tools.item.IModifiable;

@Mixin(value = TargetSelector.class, remap = false)
public class TargetSelectorMixin {

    @Inject(
        at = @At("return"),
        method = "getTargettableEntitiesWithinAABB(Lnet/minecraft/world/level/Level;DLnet/minecraft/world/entity/Entity;)Ljava/util/List;",
        cancellable = true
    )
    private static <E extends Entity & IShootable> void getTargettableEntitiesWithinAABB(Level world, double reach, E owner, CallbackInfoReturnable<List<Entity>> cb){
        if(owner.getShooter() instanceof LivingEntity attacker){
            ItemStack mainHandStack = attacker.getMainHandItem();
            AABB aabb = owner.getBoundingBox().inflate(reach);
            if(mainHandStack.getItem() instanceof IModifiable){
                List<Entity> resultCopy = new ArrayList<>(cb.getReturnValue());
                if(resultCopy != null){
                    resultCopy.addAll(world.getEntities(attacker, aabb).stream().filter(t->t instanceof LivingEntity && attacker.distanceTo(t) <= reach).map(t -> (LivingEntity)t).toList());
                    cb.setReturnValue(resultCopy);
                }
            }
        }
    }

    @Inject(
        at = @At("return"),
        method = "getTargettableEntitiesWithinAABB(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/phys/AABB;D)Ljava/util/List;",
        cancellable = true
    )
    private static void getTargettableEntitiesWithinAABB(Level world, LivingEntity attacker, AABB aabb,double reach, CallbackInfoReturnable<List<Entity>> cb){
        ItemStack mainHandStack = attacker.getMainHandItem();
        if(mainHandStack.getItem() instanceof IModifiable){
            List<Entity> resultCopy = new ArrayList<>(cb.getReturnValue());
            resultCopy.addAll(world.getEntities(attacker, aabb).stream().filter(t->t instanceof LivingEntity && attacker.distanceTo(t) <= reach).map(t -> (LivingEntity)t).toList());
            cb.setReturnValue(resultCopy);
        }
    }
}
