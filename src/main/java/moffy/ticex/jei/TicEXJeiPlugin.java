package moffy.ticex.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.config.IJeiConfigManager;
import moffy.ticex.TicEX;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
@SuppressWarnings("unused")
public class TicEXJeiPlugin implements IModPlugin {
    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "jei_compat");
    }

    @Override
    public void registerItemSubtypes(@NotNull ISubtypeRegistration registration) {
        TicEXRegistry.JEI_INTEGRATIONS.each(registration, IJeiIntegration::registerItemSubtypes);
    }

    @Override
    public <T> void registerFluidSubtypes(@NotNull ISubtypeRegistration registration, @NotNull IPlatformFluidHelper<T> platformFluidHelper) {
        TicEXRegistry.JEI_INTEGRATIONS.each(integration ->
                integration.registerFluidSubtypes(registration, platformFluidHelper));
    }

    @Override
    public void registerIngredients(@NotNull IModIngredientRegistration registration) {
        TicEXRegistry.JEI_INTEGRATIONS.each(registration, IJeiIntegration::registerIngredients);
    }

    @Override
    public void registerExtraIngredients(@NotNull IExtraIngredientRegistration registration) {
        TicEXRegistry.JEI_INTEGRATIONS.each(registration, IJeiIntegration::registerExtraIngredients);
    }

    @Override
    public void registerIngredientAliases(@NotNull IIngredientAliasRegistration registration) {
        TicEXRegistry.JEI_INTEGRATIONS.each(registration, IJeiIntegration::registerIngredientAliases);
    }

    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
        TicEXRegistry.JEI_INTEGRATIONS.each(registration, IJeiIntegration::registerCategories);
    }

    @Override
    public void registerVanillaCategoryExtensions(@NotNull IVanillaCategoryExtensionRegistration registration) {
        TicEXRegistry.JEI_INTEGRATIONS.each(registration, IJeiIntegration::registerVanillaCategoryExtensions);
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        TicEXRegistry.JEI_INTEGRATIONS.each(registration, IJeiIntegration::registerRecipes);
    }

    @Override
    public void registerRecipeTransferHandlers(@NotNull IRecipeTransferRegistration registration) {
        TicEXRegistry.JEI_INTEGRATIONS.each(registration, IJeiIntegration::registerRecipeTransferHandlers);
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        TicEXRegistry.JEI_INTEGRATIONS.each(registration, IJeiIntegration::registerRecipeCatalysts);
    }

    @Override
    public void registerGuiHandlers(@NotNull IGuiHandlerRegistration registration) {
        TicEXRegistry.JEI_INTEGRATIONS.each(registration, IJeiIntegration::registerGuiHandlers);
    }

    @Override
    public void registerAdvanced(@NotNull IAdvancedRegistration registration) {
        TicEXRegistry.JEI_INTEGRATIONS.each(registration, IJeiIntegration::registerAdvanced);
    }

    @Override
    public void registerRuntime(@NotNull IRuntimeRegistration registration) {
        TicEXRegistry.JEI_INTEGRATIONS.each(registration, IJeiIntegration::registerRuntime);
    }

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
        TicEXRegistry.JEI_INTEGRATIONS.each(jeiRuntime, IJeiIntegration::onRuntimeAvailable);
    }

    @Override
    public void onRuntimeUnavailable() {
        TicEXRegistry.JEI_INTEGRATIONS.each(IJeiIntegration::onRuntimeUnavailable);
    }

    @Override
    public void onConfigManagerAvailable(@NotNull IJeiConfigManager configManager) {
        TicEXRegistry.JEI_INTEGRATIONS.each(configManager, IJeiIntegration::onConfigManagerAvailable);
    }
}
