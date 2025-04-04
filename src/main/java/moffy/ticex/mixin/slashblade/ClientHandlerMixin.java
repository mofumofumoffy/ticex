package moffy.ticex.mixin.slashblade;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mods.flammpfeil.slashblade.client.ClientHandler;
import net.minecraftforge.client.event.EntityRenderersEvent;

@Mixin(ClientHandler.class)
public class ClientHandlerMixin {
    @Inject(
        at = @At("invoke"),
        method = "addLayers",
        cancellable = true,
        remap = false
    )
    private static void addLayersCanceller(EntityRenderersEvent.AddLayers event, CallbackInfo cb) {
        cb.cancel();
    }
}
