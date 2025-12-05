package moffy.ticex.mixin.slashblade;

import moffy.ticex.modules.slashblade.IInputCommandEvent;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

@Pseudo
@Mixin(targets = "mods.flammpfeil.slashblade.event.handler.InputCommandEvent", remap = false)
public abstract class InputCommandEventMixin$Later implements IInputCommandEvent {
    @Shadow
    public abstract ServerPlayer getEntity();

    @Override
    public ServerPlayer ticex$getEntity() {
        return getEntity();
    }
}
