package moffy.ticex.modifier.propeties;

import dan200.computercraft.api.lua.ILuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import moffy.ticex.modifier.ModifierAbyssal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class AbyssalProperty {
    public static BiFunction<Player, ItemStack, Map<String, Object>> getProperties() {
        return (user, stack) -> {
            Map<String, Object> result = new HashMap<>();

            result.put("isNightVisionEnabled", isNightVisionEnabled(user, stack));
            result.put("setNightVision", setNightVision(user, stack));

            return result;
        };
    }

    public static ILuaFunction isNightVisionEnabled(Player user, ItemStack stack){
        return (args) -> {
            if(stack.getItem() instanceof IModifiable){
                return MethodResult.of(ToolStack.from(stack).getPersistentData().getBoolean(ModifierAbyssal.ABYSSAL_DATA));
            }
            return MethodResult.of();
        };
    }

    public static ILuaFunction setNightVision(Player user, ItemStack stack){
        return (args) -> {
            if(stack.getItem() instanceof IModifiable){
                ToolStack tool = ToolStack.from(stack);
                boolean newValue = args.getBoolean(0);
                if(newValue != tool.getPersistentData().getBoolean(ModifierAbyssal.ABYSSAL_DATA)){
                    ModifierAbyssal.toggleNightVision(tool, user);
                }
                return MethodResult.of(true);
            }
            return MethodResult.of(false);
        };
    }
}
