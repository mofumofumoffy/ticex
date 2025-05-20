package moffy.ticex.lib.utils;

import java.util.function.Predicate;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class TicEXCuriosUtils {
    public static ItemStack findItem(Player player, Predicate<ItemStack> predicate){
        LazyOptional<ICuriosItemHandler> handlerOptional = CuriosApi.getCuriosInventory(player);
        if(handlerOptional.isPresent()){
            ICuriosItemHandler handler = handlerOptional.orElseThrow(IllegalStateException::new);
            IItemHandlerModifiable curiosInv = handler.getEquippedCurios();
            for(int i = 0; i < handler.getSlots(); i++){
                ItemStack curio = curiosInv.getStackInSlot(i);
                if(predicate.test(curio)){
                    return curio;
                }
            }
        }
        return null;
    }
}
