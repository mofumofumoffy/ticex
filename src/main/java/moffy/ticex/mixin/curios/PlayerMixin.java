package moffy.ticex.mixin.curios;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

@Mixin(Player.class)
public class PlayerMixin {
    
    @Inject(
        at = @At("return"),
        method = "getItemBySlot",
        cancellable = true
    )
    public void getItemBySlot(EquipmentSlot pSlot, CallbackInfoReturnable<ItemStack> cb){
        if(pSlot != null && cb.getReturnValue().isEmpty()){
            Player player = player();
            CuriosApi.getCuriosInventory(player).ifPresent(handler->{
                String key = null;
                switch(pSlot){
                    case CHEST:
                        key = "incomparable_chest";
                        break;
                    case FEET:
                        key = "incomparable_feet";
                        break;
                    case HEAD:
                        key = "incomparable_head";
                        break;
                    case LEGS:
                        key = "incomparable_legs";
                        break;
                    case MAINHAND:
                        key = "incomparable_mainhand";
                        break;
                    default:
                        break;   
                }
                if(key != null){
                    handler.findCurio(key, 0).ifPresent(action->{
                        cb.setReturnValue(action.stack());
                    });
                }
            });
        }
    }

    
    
    private Player player(){
        return (Player)((Object)this);
    }
}
