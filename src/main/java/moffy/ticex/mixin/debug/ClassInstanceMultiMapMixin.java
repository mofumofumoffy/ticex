package moffy.ticex.mixin.debug;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.util.ClassInstanceMultiMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClassInstanceMultiMap.class)
public class ClassInstanceMultiMapMixin {
    @ModifyExpressionValue(method = "find", at = @At(value = "INVOKE", target = "Ljava/lang/Class;isAssignableFrom(Ljava/lang/Class;)Z"))
    public boolean alwaysAssignable(boolean original) {
        return true;
    }
}
