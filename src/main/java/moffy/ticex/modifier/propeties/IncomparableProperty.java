package moffy.ticex.modifier.propeties;

import dan200.computercraft.api.lua.ILuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import moffy.ticex.item.modifiable.ModifiableGauntlet;
import moffy.ticex.lib.ResonanceTools;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class IncomparableProperty {
    public static BiFunction<Player, ItemStack, Map<String, Object>> getProperties() {
        return (user, stack) -> {
            Map<String, Object> result = new HashMap<>();

            if(stack.getItem() instanceof ModifiableGauntlet){
                result.put("shootTool", shootTool(user, stack));
            }

            return result;
        };
    }

    public static ILuaFunction shootTool(Player user, ItemStack stack){
        return iArguments -> {
            LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER).execute(() -> {
                ResonanceTools.shootGauntletStack(user, stack, false);
            });
            return MethodResult.of(true);
        };
    }
}
