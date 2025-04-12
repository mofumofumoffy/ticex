package moffy.ticex.mixin.slashblade;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.network.ActiveStateSyncMessage;
import moffy.ticex.item.modifiable.ModifiableSlashBladeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@Mixin(ActiveStateSyncMessage.class)
public class ActiveStateSyncMessageMixin {
    @Inject(
        at = @At("invoke"),
        method = "handle",
        cancellable = true,
        remap = false
    )
    private static void handle(ActiveStateSyncMessage msg, Supplier<NetworkEvent.Context> ctx, CallbackInfo cb) {
        ctx.get().enqueueWork(() -> {

            if (!msg.activeTag.hasUUID("BladeUniqueId"))
                return;

            @SuppressWarnings("unused")
            ServerPlayer sender = ctx.get().getSender();

            Entity target = Minecraft.getInstance().level.getEntity(msg.id);

            if (target instanceof LivingEntity) {
            	
                ItemStack stack = ((LivingEntity) target).getItemInHand(InteractionHand.MAIN_HAND);
                if (stack.isEmpty())
                    return;
                
                stack.getCapability(ItemSlashBlade.BLADESTATE)
                        .filter((state) -> state.getUniqueId().equals(msg.activeTag.getUUID("BladeUniqueId")))
                        .ifPresent((state) -> {
                        	state.setActiveState(msg.activeTag);
                            if(stack.getItem() instanceof IModifiable){
                                ToolStack.from(stack).getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, state.serializeNBT());
                            } else {
                                var tag = stack.getOrCreateTag();
                                tag.put("bladeState", state.serializeNBT());
                            }
                        });
            }
        });
        ctx.get().setPacketHandled(true);
        cb.cancel();
    }
}
