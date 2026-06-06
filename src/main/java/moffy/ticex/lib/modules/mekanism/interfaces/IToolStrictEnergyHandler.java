package moffy.ticex.lib.modules.mekanism.interfaces;

import mekanism.api.IContentsListener;
import mekanism.api.energy.ISidedStrictEnergyHandler;
import mekanism.api.math.FloatingLong;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public interface IToolStrictEnergyHandler extends ISidedStrictEnergyHandler, IContentsListener {
    default boolean canHandleEnergy() {
        return true;
    }

    ItemStack getStack();

    default IToolStackView getTool(){
        ItemStack stack = getStack();
        if(stack.getItem() instanceof IModifiable){
            return ToolStack.from(stack);
        }
        return null;
    }

    @Override
    FloatingLong getEnergy(int i, @Nullable Direction direction);
}
