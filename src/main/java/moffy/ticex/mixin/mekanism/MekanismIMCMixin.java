package moffy.ticex.mixin.mekanism;

import mekanism.api.MekanismIMC;
import mekanism.api.providers.IModuleDataProvider;
import moffy.ticex.modules.mekanism.TicEXMekanismModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MekanismIMC.class, remap = false)
public class MekanismIMCMixin {

    @Shadow
    private static void sendModuleIMC(String method, IModuleDataProvider<?>... moduleDataProviders) {}

    @Inject(at = @At("tail"), method = "addMekaSuitHelmetModules")
    private static void addMekaSuitHelmetModulesExtension(
        IModuleDataProvider<?>[] moduleDataProviders,
        CallbackInfo cb
    ) {
        sendModuleIMC(TicEXMekanismModule.ADD_MEKAPLATE_HELMET_MODULES, moduleDataProviders);
    }

    @Inject(at = @At("tail"), method = "addMekaSuitBodyarmorModules")
    private static void addMekaSuitBodyarmorModulesExtension(
        IModuleDataProvider<?>[] moduleDataProviders,
        CallbackInfo cb
    ) {
        sendModuleIMC(TicEXMekanismModule.ADD_MEKAPLATE_CHESTPLATE_MODULES, moduleDataProviders);
    }

    @Inject(at = @At("tail"), method = "addMekaSuitPantsModules")
    private static void addMekaSuitPantsModulesExtension(
        IModuleDataProvider<?>[] moduleDataProviders,
        CallbackInfo cb
    ) {
        sendModuleIMC(TicEXMekanismModule.ADD_MEKAPLATE_LEGGINGS_MODULES, moduleDataProviders);
    }

    @Inject(at = @At("tail"), method = "addMekaSuitBootsModules")
    private static void addMekaSuitBootsModulesExtension(
        IModuleDataProvider<?>[] moduleDataProviders,
        CallbackInfo cb
    ) {
        sendModuleIMC(TicEXMekanismModule.ADD_MEKAPLATE_BOOTS_MODULES, moduleDataProviders);
    }
}
