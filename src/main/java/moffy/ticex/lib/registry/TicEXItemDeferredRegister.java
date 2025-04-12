package moffy.ticex.lib.registry;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import slimeknights.mantle.registration.deferred.DeferredRegisterWrapper;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;

public class TicEXItemDeferredRegister extends DeferredRegisterWrapper<Item>  {

    protected TicEXItemDeferredRegister(DeferredRegister<Item> register, String modID) {
        super(register, modID);
    }

    public <I extends Item> ItemObject<I> register(String name, Supplier<? extends I> sup) {
        return new ItemObject<>(this.register.register(name, sup));
     }
    
    public ItemObject<Item> register(String name, Item.Properties props) {
      return this.register(name, () -> {
         return new Item(props);
      });
   }

   @SuppressWarnings("rawtypes")
    public <T extends Enum<T>, I extends Item> EnumObject<T, I> registerEnum(T[] values, String name, Function<T, ? extends I> mapper) {
        return registerEnum((Enum[])values, (String)name, (BiFunction)((fullName, type) -> {
            return this.register((String)fullName, ()->{
                return mapper.apply((T)type);
            });
        }));
    }

    @SuppressWarnings("rawtypes")
    public <T extends Enum<T>, I extends Item> EnumObject<T, I> registerEnum(String name, T[] values, Function<T, ? extends I> mapper) {
        return registerEnum((String)name, (Enum[])values, (BiFunction)((fullName, type) -> {
            return this.register((String)fullName, () -> {
                return mapper.apply((T)type);
            });
        }));
    }
}
