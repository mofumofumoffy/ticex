package moffy.ticex.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;

@Mixin(value = ToolAttackContext.class, remap = false)
public interface CriticalAccessor {

    @Mutable
    @Accessor("criticalModifier")
    void setCriticalModifier(float criticalModifier);
}
