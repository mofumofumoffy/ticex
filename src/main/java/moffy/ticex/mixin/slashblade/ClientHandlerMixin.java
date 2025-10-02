package moffy.ticex.mixin.slashblade;

import mods.flammpfeil.slashblade.client.ClientHandler;
import moffy.ticex.client.modules.slashblade.LayerSBToolMainBlade;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.EntityRenderersEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientHandler.class, remap = false)
public class ClientHandlerMixin {

    @Inject(at = @At("HEAD"), method = "addPlayerLayer", cancellable = true)
    private static void addPlayerLayer(EntityRenderersEvent.AddLayers evt, String skin, CallbackInfo ci) {
        EntityRenderer<? extends Player> renderer = evt.getSkin(skin);

        if (ticex$addSBLayer(renderer)) {
            ci.cancel();
        }
    }

    @Unique
    @SuppressWarnings({"unchecked", "RedundantSuppression"})
    private static <T extends LivingEntity> boolean ticex$addSBLayer(EntityRenderer<T> entityRenderer) {
        if(entityRenderer instanceof LivingEntityRenderer<?, ?> livingEntityRenderer) {
            LivingEntityRenderer<T, EntityModel<T>> renderer = (LivingEntityRenderer<T, EntityModel<T>>) livingEntityRenderer;
            LayerSBToolMainBlade<T, EntityModel<T>> mainBladeLayer = new LayerSBToolMainBlade<>(renderer);

            renderer.addLayer(mainBladeLayer);
            return true;
        }
        return false;
    }
}
