package moffy.ticex.jei.ticex;

import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import moffy.ticex.TicEX;
import moffy.ticex.block.transmuter.pattern.FluidTransmutationPair;
import moffy.ticex.block.transmuter.pattern.FluidTransmutationResolver;
import moffy.ticex.client.modules.ticex.screen.FluidTransmuterScreen;
import moffy.ticex.jei.IJeiIntegration;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.plugin.jei.util.GuiContainerTankHandler;

import java.util.ArrayList;
import java.util.List;

public class TicEXJEIIntegration implements IJeiIntegration {
    public TicEXJEIIntegration() {
    }

    @SuppressWarnings("SameParameterValue")
    private static <T> RecipeType<T> type(String name, Class<T> clazz) {
        return RecipeType.create(TicEX.MODID, name, clazz);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new FluidTransmuterCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<FluidTransmutationPair> transmutationRecipes = new ArrayList<>(FluidTransmutationResolver.INSTANCE.getPairs());
        registration.addRecipes(JeiConstants.FLUID_TRANSMUTATION_RECIPE, transmutationRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
                new ItemStack(TicEXRegistry.FLUID_TRANSMUTER.get()),
                JeiConstants.FLUID_TRANSMUTATION_RECIPE
        );
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGenericGuiContainerHandler(FluidTransmuterScreen.class, new GuiContainerTankHandler<>());
    }

    public static class JeiConstants {
        public static final RecipeType<FluidTransmutationPair> FLUID_TRANSMUTATION_RECIPE =
                type("fluid_transmutation", FluidTransmutationPair.class);
    }

}
