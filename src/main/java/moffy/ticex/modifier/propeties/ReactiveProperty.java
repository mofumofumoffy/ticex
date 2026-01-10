package moffy.ticex.modifier.propeties;

import com.hollingsworth.arsnouveau.common.event.ReactiveEvents;
import dan200.computercraft.api.lua.ILuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class ReactiveProperty {
    public static BiFunction<Player, ItemStack, Map<String, Object>> getProperties() {
        return (user, stack) -> {
            Map<String, Object> result = new HashMap<>();

            result.put("castArsSpell", castArsSpell(user, stack));

            return result;
        };
    }

    public static ILuaFunction castArsSpell(Player user, ItemStack stack) {
        return (args)->{
            LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER).execute(() -> {
                ReactiveEvents.castSpell(user, stack);
            });
            return MethodResult.of(true);
        };
    }
}
