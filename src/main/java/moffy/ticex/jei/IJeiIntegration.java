package moffy.ticex.jei;

import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.config.IJeiConfigManager;

public interface IJeiIntegration {
    default void registerItemSubtypes(ISubtypeRegistration registration) {
    }

    default <T> void registerFluidSubtypes(ISubtypeRegistration registration, IPlatformFluidHelper<T> platformFluidHelper) {
    }

    default void registerIngredients(IModIngredientRegistration registration) {
    }

    default void registerExtraIngredients(IExtraIngredientRegistration registration) {
    }

    default void registerIngredientAliases(IIngredientAliasRegistration registration) {
    }

    default void registerCategories(IRecipeCategoryRegistration registration) {
    }

    default void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
    }

    default void registerRecipes(IRecipeRegistration registration) {
    }

    default void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
    }

    default void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
    }

    default void registerGuiHandlers(IGuiHandlerRegistration registration) {
    }

    default void registerAdvanced(IAdvancedRegistration registration) {
    }

    default void registerRuntime(IRuntimeRegistration registration) {
    }

    default void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
    }

    default void onRuntimeUnavailable() {
    }

    default void onConfigManagerAvailable(IJeiConfigManager configManager) {
    }
}
