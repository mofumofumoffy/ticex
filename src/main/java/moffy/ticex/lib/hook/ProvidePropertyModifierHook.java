package moffy.ticex.lib.hook;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ProvidePropertyModifierHook {
    public BiFunction<Player, ItemStack, Map<String, Object>> getPropertyProvider();

    public static class DefaultClass implements ProvidePropertyModifierHook{
        @Override
        public BiFunction<Player, ItemStack, Map<String, Object>> getPropertyProvider() {
            return (player, stack)->new HashMap<>();
        }
    }

    record AllMerger(Collection<ProvidePropertyModifierHook> modules) implements ProvidePropertyModifierHook{

        @Override
        public BiFunction<Player, ItemStack, Map<String, Object>> getPropertyProvider() {
            return (player, stack)->{
                Map<String, Object> result = new HashMap<>();
                for(ProvidePropertyModifierHook module : modules){
                    result.putAll(module.getPropertyProvider().apply(player, stack));
                }
                return result;
            };
        }

    }
}
