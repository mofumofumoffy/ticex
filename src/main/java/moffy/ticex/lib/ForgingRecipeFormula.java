package moffy.ticex.lib;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;

public class ForgingRecipeFormula {
    public final NonNullList<ItemStack> input = NonNullList.withSize(6, ItemStack.EMPTY);
    @Nullable
    public ITinkerStationRecipe recipe;
    private final DummyTinkerStationContainer dummy = new DummyTinkerStationContainer();

    private static @Nullable ITinkerStationRecipe getRecipeFromGrid(ITinkerStationContainer inv, Level world) {
        return world.getRecipeManager().getRecipeFor(TinkerRecipeTypes.TINKER_STATION.get(), inv, world)
                .filter((recipe) -> recipe.isSpecial() || !recipe.isIncomplete()).orElse(null);
    }
}
