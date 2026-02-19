package moffy.ticex.mixin.projecte;

import moffy.ticex.modifier.ModifierAbyssal;
import moffy.ticex.modifier.ModifierHurricane;
import moffy.ticex.modules.general.TicEXRegistry;
import moze_intel.projecte.network.packets.to_server.KeyPressPKT;
import moze_intel.projecte.utils.PEKeybind;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@Mixin(value = KeyPressPKT.class, remap = false)
public class KeyPressPKTMixin {

    @Shadow
    @Final
    private PEKeybind key;

    @Inject(at = @At("HEAD"), method = "handle", cancellable = true)
    public void handle(NetworkEvent.Context context, CallbackInfo ci) {
        ServerPlayer player = context.getSender();
        if (player == null || player.isSpectator()) {
            return;
        }
        if (key == PEKeybind.HELMET_TOGGLE) {
            ItemStack helm = player.getItemBySlot(EquipmentSlot.HEAD);
            if (!helm.isEmpty() && helm.getItem() instanceof IModifiable) {
                ToolStack tool = ToolStack.from(helm);
                if (tool.getModifierLevel(TicEXRegistry.ABYSSAL_MODIFIER.get()) > 0) {
                    ModifierAbyssal.toggleNightVision(tool, player);
                }
                ci.cancel();
            }

        } else if (key == PEKeybind.BOOTS_TOGGLE) {
            ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
            if (!boots.isEmpty() && boots.getItem() instanceof IModifiable) {
                ToolStack tool = ToolStack.from(boots);
                if (tool.getModifierLevel(TicEXRegistry.HURRICANE_MODIFIER.get()) > 0) {
                    ModifierHurricane.toggleStepAssist(tool, player);
                }
                ci.cancel();
            }
        }
    }
}
