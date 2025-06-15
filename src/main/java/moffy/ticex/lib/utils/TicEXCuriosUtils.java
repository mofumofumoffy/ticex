package moffy.ticex.lib.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class TicEXCuriosUtils {

    public static List<ItemStack> getAllToolStackInCurios(LivingEntity entity, Predicate<ItemStack> predicate){
        List<ItemStack>result = new ArrayList<>();
        LazyOptional<ICuriosItemHandler> handlerOptional = CuriosApi.getCuriosInventory(entity);
        if(handlerOptional.isPresent()){
            ICuriosItemHandler handler = handlerOptional.orElseThrow(IllegalStateException::new);
            IItemHandlerModifiable curiosInv = handler.getEquippedCurios();
            for(int i = 0; i < handler.getSlots(); i++){
                ItemStack curio = curiosInv.getStackInSlot(i);
                if(predicate.test(curio)){
                    result.add(curio);
                }
            }
        }
        return result;
    }

    public static ItemStack getToolStackInCurios(LivingEntity entity, Predicate<ItemStack> predicate){
        LazyOptional<ICuriosItemHandler> handlerOptional = CuriosApi.getCuriosInventory(entity);
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

    public static String getEquipmentSlotNameInCurios(LivingEntity entity, ItemStack stack){
        LazyOptional<ICuriosItemHandler> handlerOptional = CuriosApi.getCuriosInventory(entity);
        if(handlerOptional.isPresent()){
            ICuriosItemHandler handler = handlerOptional.orElseThrow(IllegalStateException::new);
            Optional<SlotResult> slotResultOptional = handler.findFirstCurio(curio->ItemStack.isSameItem(stack, curio));
            if(slotResultOptional.isPresent()){
                SlotResult slotResult = slotResultOptional.orElseThrow(IllegalStateException::new);
                return slotResult.slotContext().identifier();
            }
        }
        return null;
    }
}
