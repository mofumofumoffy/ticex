package moffy.ticex.lib.registry;

import java.util.function.Supplier;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.library.tools.part.PartCastItem;

public class TicEXItemDeferredRegisterExtension extends TicEXItemDeferredRegister{

    public TicEXItemDeferredRegisterExtension(DeferredRegister<Item> register, String modID) {
        super(register, modID);
    }
    
    public CastItemObject registerCast(String name, Supplier<? extends Item> constructor) {
        ItemObject<Item> cast = this.register(name + "_cast", constructor);
        ItemObject<Item> sandCast = this.register(name + "_sand_cast", constructor);
        ItemObject<Item> redSandCast = this.register(name + "_red_sand_cast", constructor);
        return new CastItemObject(this.resource(name), cast, sandCast, redSandCast);
    }

    public CastItemObject registerCast(String name, Item.Properties props) {
        return this.registerCast(name, () -> {
            return new Item(props);
        });
    }

    public CastItemObject registerCast(ItemObject<? extends IMaterialItem> item, Item.Properties props) {
        return this.registerCast(item.getId().getPath(), () -> {
            return new PartCastItem(props, item);
        });
    }
}
