package moffy.ticex.mixin.slashblade;

import java.util.EnumSet;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mods.flammpfeil.slashblade.capability.inputstate.CapabilityInputState;
import mods.flammpfeil.slashblade.event.InputCommandEvent;
import mods.flammpfeil.slashblade.network.MoveCommandMessage;
import mods.flammpfeil.slashblade.util.EnumSetConverter;
import mods.flammpfeil.slashblade.util.InputCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

@Mixin(MoveCommandMessage.class)
public class MoveCommandMessageMixin {
    @Inject(
        at = @At("invoke"),
        method = "handle",
        cancellable = true,
        remap = false
    )
    private static void handle(MoveCommandMessage msg, Supplier<NetworkEvent.Context> ctx, CallbackInfo cb) {
         ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            ItemStack stack = sender.getItemInHand(InteractionHand.MAIN_HAND);
            if (stack.isEmpty())
                return;

            sender.getCapability(CapabilityInputState.INPUT_STATE).ifPresent((state) -> {
                EnumSet<InputCommand> old = state.getCommands().clone();

                state.getCommands().clear();
                state.getCommands().addAll(EnumSetConverter.convertToEnumSet(InputCommand.class, msg.command));

                EnumSet<InputCommand> current = state.getCommands().clone();

                long currentTime = sender.level().getGameTime();
                current.forEach(c -> {
                    if (!old.contains(c))
                        state.getLastPressTimes().put(c, currentTime);
                });

                InputCommandEvent.onInputChange(sender, state, old, current);
            });
        });
        ctx.get().setPacketHandled(true);
        cb.cancel();
    }
}
