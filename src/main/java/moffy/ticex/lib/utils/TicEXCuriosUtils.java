package moffy.ticex.lib.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
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

    public static EquipmentSlot resolveSlot(SlotContext context){
        String identifier = context.identifier();
        if(identifier.equals("incomparable_head")){
            return EquipmentSlot.HEAD;
        } else if(identifier.equals("incomparable_chest")){
            return EquipmentSlot.CHEST;
        } else if(identifier.equals("incomparable_legs")){
            return EquipmentSlot.LEGS;
        } else if(identifier.equals("incomparable_feet")){
            return EquipmentSlot.FEET;
        } else if(identifier.equals("incomparable_offhand")){
            return EquipmentSlot.OFFHAND;
        }

        return EquipmentSlot.MAINHAND;
    }

    public static String resolveEquipmentSlot(EquipmentSlot slot){
        switch (slot) {
            case CHEST:
                return "incomparable_chest";
            case FEET:
                return "incomparable_feet";
            case HEAD:
                return "incomparable_head";
            case LEGS:
                return "incomparable_legs";
            case MAINHAND:
                return "incomparable_mainhand";
            case OFFHAND:
                return "incomparable_offhand";
            default:
                return null;
        }
    }
}
