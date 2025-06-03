package moffy.ticex.lib;

import java.util.function.Function;

import com.google.common.collect.Multimap;

import net.minecraft.world.item.ItemStack;

public interface IPropertyProviderModifier {
    public Function<ItemStack,Multimap<String, Object>> getPropertyProvider();
}
