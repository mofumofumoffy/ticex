package moffy.ticex.mixin.mekanism;

import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import mekanism.client.render.hud.MekaSuitEnergyLevel;
import moffy.ticex.lib.modules.mekanism.MekaGearCapability;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = MekaSuitEnergyLevel.class, remap = false)
public class MekaSuitEnergyLevelMixin {
    @Expression("? instanceof ?")
    @ModifyExpressionValue(method = "render", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
    private boolean render(boolean original,
                           @Local(name = "stack") ItemStack stack) {
        return original || stack.getCapability(MekaGearCapability.MEKA_GEAR_CAPABILITY).isPresent();
    }
}
