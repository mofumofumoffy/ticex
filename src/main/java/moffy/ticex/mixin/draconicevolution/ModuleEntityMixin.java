package moffy.ticex.mixin.draconicevolution;

import codechicken.lib.gui.modular.elements.GuiElement;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.entities.FilteredModuleEntity;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import moffy.ticex.caps.draconicevolution.EvolvedModuleHost;
import moffy.ticex.modifier.ModifierEvolved;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModuleEntity.class)
public class ModuleEntityMixin {

    @Shadow(remap = false)
    protected ModuleHost host;

    @Inject(at = @At("TAIL"), method = "clientModuleClicked", cancellable = true, remap = false)
    public boolean clientModuleClickedExtension(
        GuiElement<?> parent,
        Player player,
        int x,
        int y,
        int width,
        int height,
        double mouseX,
        double mouseY,
        int button,
        CallbackInfoReturnable<Boolean> cb
    ) {
        if ((ModuleEntity<?>) ((Object) this) instanceof FilteredModuleEntity && host instanceof EvolvedModuleHost) {
            EvolvedModuleHost evolvedModuleHost = (EvolvedModuleHost) host;
            evolvedModuleHost
                .getToolSupplier()
                .getPersistentData()
                .put(ModifierEvolved.MODULE_HOST_LOCATION, evolvedModuleHost.serializeNBT());
        }
        return cb.getReturnValue();
    }
}
