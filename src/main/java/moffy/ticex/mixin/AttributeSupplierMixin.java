package moffy.ticex.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

@Deprecated
@Mixin(AttributeSupplier.Builder.class)
public class AttributeSupplierMixin {
    @Inject(
        at = {@At("head")},
        method = {"build"},
        cancellable = true
    )
    public void addExtraAttribute(CallbackInfoReturnable<AttributeSupplier> info){
        if(TicEXRegistry.DAMAGE_TAKEN != null && TicEXRegistry.HEALING_RECEIVED != null){
            ((AttributeSupplier.Builder)((Object)this)).add(TicEXRegistry.HEALING_RECEIVED.get()).add(TicEXRegistry.DAMAGE_TAKEN.get());
        }
    }
}
