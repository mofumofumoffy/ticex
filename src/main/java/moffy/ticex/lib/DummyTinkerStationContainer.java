package moffy.ticex.lib;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;

public class DummyTinkerStationContainer implements ITinkerStationContainer {
    private NonNullList<ItemStack> inventory = NonNullList.withSize(6, ItemStack.EMPTY);

    @Override
    public @Nullable MaterialRecipe getInputMaterial(int i) {
        return null;
    }

    @Override
    public @NotNull ItemStack getTinkerableStack() {
        return inventory.get(0);
    }

    @Override
    public @NotNull ItemStack getInput(int i) {
        return inventory.get(i + 1);
    }

    @Override
    public int getInputCount() {
        return 6;
    }

    public void setInput(int index, ItemStack stack) {
        if (index >= 0 && index < inventory.size() - 1) {
            inventory.set(index + 1, stack);
        }
    }
}
