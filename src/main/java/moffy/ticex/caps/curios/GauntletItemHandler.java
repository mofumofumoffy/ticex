package moffy.ticex.caps.curios;

import java.util.function.Supplier;

import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class GauntletItemHandler extends ToolInventoryCapability implements ICurio{

    protected ItemStack stack;

    public GauntletItemHandler(ItemStack stack, Supplier<? extends IToolStackView> tool) {
        super(tool);
        this.stack = stack;
    }

    @Override
    public int getSlots() {
        return 6;
    }

    @Override
    public ItemStack getStack() {
        return stack;
    }
}
