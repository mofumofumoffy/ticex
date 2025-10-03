package moffy.ticex.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import slimeknights.tconstruct.library.modifiers.modules.combat.MeleeAttributeModule;

@Mixin(value = MeleeAttributeModule.class, remap = false)
public class MeleeAttributeModuleMixin {
    @WrapWithCondition(method = "beforeMeleeHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;addTransientModifier(Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;)V"))
    public boolean beforeMeleeHitCheck(AttributeInstance instance, AttributeModifier pModifier) {
        return !instance.hasModifier(pModifier);
    }
}
