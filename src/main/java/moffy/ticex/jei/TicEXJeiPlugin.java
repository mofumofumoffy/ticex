package moffy.ticex.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.config.IJeiConfigManager;
import moffy.ticex.TicEX;
import moffy.ticex.lib.TicEXBootstrap;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
@SuppressWarnings("unused")
/*
  Be responsible for the classes you reference.
  Avoid referencing complex classes like TicEXRegistry.
 */
public class TicEXJeiPlugin implements IModPlugin {

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return TicEX.getResource("jei_compat");
    }

    @Override
    public void registerItemSubtypes(@NotNull ISubtypeRegistration registration) {
        TicEXBootstrap.INSTANCE.each(registration, IJeiIntegration::registerItemSubtypes);
    }

    @Override
    public <T> void registerFluidSubtypes(@NotNull ISubtypeRegistration registration, @NotNull IPlatformFluidHelper<T> platformFluidHelper) {
        TicEXBootstrap.INSTANCE.each(integration ->
                integration.registerFluidSubtypes(registration, platformFluidHelper));
    }

    @Override
    public void registerIngredients(@NotNull IModIngredientRegistration registration) {
        TicEXBootstrap.INSTANCE.each(registration, IJeiIntegration::registerIngredients);
    }

    @Override
    public void registerExtraIngredients(@NotNull IExtraIngredientRegistration registration) {
        TicEXBootstrap.INSTANCE.each(registration, IJeiIntegration::registerExtraIngredients);
    }

    @Override
    public void registerIngredientAliases(@NotNull IIngredientAliasRegistration registration) {
        TicEXBootstrap.INSTANCE.each(registration, IJeiIntegration::registerIngredientAliases);
    }

    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
        TicEXBootstrap.INSTANCE.each(registration, IJeiIntegration::registerCategories);
    }

    @Override
    public void registerVanillaCategoryExtensions(@NotNull IVanillaCategoryExtensionRegistration registration) {
        TicEXBootstrap.INSTANCE.each(registration, IJeiIntegration::registerVanillaCategoryExtensions);
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        TicEXBootstrap.INSTANCE.each(registration, IJeiIntegration::registerRecipes);
    }

    @Override
    public void registerRecipeTransferHandlers(@NotNull IRecipeTransferRegistration registration) {
        TicEXBootstrap.INSTANCE.each(registration, IJeiIntegration::registerRecipeTransferHandlers);
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        TicEXBootstrap.INSTANCE.each(registration, IJeiIntegration::registerRecipeCatalysts);
    }

    @Override
    public void registerGuiHandlers(@NotNull IGuiHandlerRegistration registration) {
        TicEXBootstrap.INSTANCE.each(registration, IJeiIntegration::registerGuiHandlers);
    }

    @Override
    public void registerAdvanced(@NotNull IAdvancedRegistration registration) {
        TicEXBootstrap.INSTANCE.each(registration, IJeiIntegration::registerAdvanced);
    }

    @Override
    public void registerRuntime(@NotNull IRuntimeRegistration registration) {
        TicEXBootstrap.INSTANCE.each(registration, IJeiIntegration::registerRuntime);
    }

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
        TicEXBootstrap.INSTANCE.each(jeiRuntime, IJeiIntegration::onRuntimeAvailable);
    }

    @Override
    public void onRuntimeUnavailable() {
        TicEXBootstrap.INSTANCE.each(IJeiIntegration::onRuntimeUnavailable);
    }

    @Override
    public void onConfigManagerAvailable(@NotNull IJeiConfigManager configManager) {
        TicEXBootstrap.INSTANCE.each(configManager, IJeiIntegration::onConfigManagerAvailable);
    }
}
