package moffy.ticex.mixin.slashblade;

import mods.flammpfeil.slashblade.client.ClientHandler;
import moffy.ticex.client.modules.slashblade.LayerSBToolMainBlade;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.EntityRenderersEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientHandler.class, remap = false)
public class ClientHandlerMixin {

    @SuppressWarnings("unchecked")
    @Inject(at = @At("head"), method = "addPlayerLayer", cancellable = true)
    private static void addPlayerLayer(EntityRenderersEvent.AddLayers evt, String skin, CallbackInfo cb) {
        EntityRenderer<? extends Player> renderer = evt.getSkin(skin);

        if (renderer instanceof LivingEntityRenderer livingRenderer) {
            livingRenderer.addLayer(new LayerSBToolMainBlade<>(livingRenderer));
            cb.cancel();
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Inject(at = @At("head"), method = "addEntityLayer", cancellable = true)
    private static void addEntityLayer(EntityRenderersEvent.AddLayers evt, EntityRenderer<?> renderer, CallbackInfo ci) {
        if (renderer instanceof LivingEntityRenderer livingRenderer) {
            livingRenderer.addLayer(new LayerSBToolMainBlade<>(livingRenderer));
            ci.cancel();
        }
    }
}
