package moffy.ticex.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import moffy.ticex.lib.hook.CriticalModifierHook;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;

@Mixin(value = ToolAttackUtil.class, remap = false)
public class ToolAttackUtilMixin {
    @WrapOperation(
            method = "getCriticalModifier",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/ForgeHooks;getCriticalHit(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;ZF)Lnet/minecraftforge/event/entity/player/CriticalHitEvent;")
    )
    private static CriticalHitEvent modifyCriticalHit(
            Player player, Entity target, boolean vanillaCritical, float damageModifier, Operation<CriticalHitEvent> original, @Local(name = "isCritical") boolean isCritical, @Local(name = "criticalModifier") float criticalModifier
    ){
        CriticalModifierHook.CriticalContext context = CriticalModifierHook.modifyCritical(player, isCritical, criticalModifier);
        return original.call(player, target, context.isCritical(), context.criticalModifier());
    }
}
