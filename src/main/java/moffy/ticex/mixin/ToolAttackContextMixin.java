package moffy.ticex.mixin;

import java.lang.reflect.Field;

import org.spongepowered.asm.mixin.Mixin;

import moffy.ticex.TicEX;
import moffy.ticex.lib.CriticalAccessor;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;

@Mixin(value = ToolAttackContext.class, remap = false)
public class ToolAttackContextMixin implements CriticalAccessor{

    @Override
    public void setCritical(boolean critical) {
        try {
            Field field = this.getClass().getDeclaredField("isCritical");
            field.setAccessible(true);
            field.set(this, critical);
        } catch (Exception e) {
            TicEX.LOGGER.error("", e);
            return;
        }
    }

}
