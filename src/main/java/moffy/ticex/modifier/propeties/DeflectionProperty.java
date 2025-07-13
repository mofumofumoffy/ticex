package moffy.ticex.modifier.propeties;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import dan200.computercraft.api.lua.ILuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import moffy.ticex.modifier.ModifierDeflection;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class DeflectionProperty {
    public static BiFunction<Player, ItemStack, Map<String, Object>> getProperties() {
        return (user, stack) -> {
            Map<String, Object> result = new HashMap<>();

            result.put("getModuleProps", isDisabled(user, stack));
            result.put("setConfigValue", setDisabled(user, stack));

            return result;
        };
    }

    public static ILuaFunction isDisabled(Player user, ItemStack stack){
        return (args) -> {
            return MethodResult.of(ToolStack.from(stack).getPersistentData().getBoolean(ModifierDeflection.DEFLECTION_DISABLED));
        };
    }

    public static ILuaFunction setDisabled(Player user, ItemStack stack){
        return (args) -> {
            boolean newValue = args.getBoolean(0);
            ToolStack.from(stack).getPersistentData().putBoolean(ModifierDeflection.DEFLECTION_DISABLED, newValue);
            return MethodResult.of();
        };
    }
}
