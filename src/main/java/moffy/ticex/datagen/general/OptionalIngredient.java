package moffy.ticex.datagen.general;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public class OptionalIngredient extends Ingredient {

    protected OptionalIngredient(Stream<? extends Value> pValues) {
        super(pValues);
    }

    public static @NotNull Ingredient ofOptional(ResourceLocation... locations) {
        return new OptionalIngredient(Arrays.stream(locations)
                .map(ItemValue::new)
        );
    }

    public static @NotNull Ingredient ofOptional(Value... values) {
        return new OptionalIngredient(Stream.of(values));
    }

    @SuppressWarnings("deprecation")
    public static class ItemValue implements Value {
        private final ResourceLocation location;

        public ItemValue(ResourceLocation location) {
            this.location = location;
        }

        @Override
        public @NotNull Collection<ItemStack> getItems() {
            Optional<Item> optional = BuiltInRegistries.ITEM.getOptional(location);
            return optional.stream()
                    .map(ItemStack::new)
                    .toList();
        }

        @Override
        public @NotNull JsonObject serialize() {
            JsonObject json = new JsonObject();
            json.addProperty("item", location.toString());
            return json;
        }
    }
}
