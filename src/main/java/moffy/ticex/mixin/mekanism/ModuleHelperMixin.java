package moffy.ticex.mixin.mekanism;

import java.util.Map;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.google.common.collect.ImmutableSet;

import mekanism.api.gear.ModuleData;
import mekanism.api.providers.IModuleDataProvider;
import mekanism.common.Mekanism;
import mekanism.common.content.gear.ModuleHelper;
import moffy.ticex.modules.TicEXRegistry;
import moffy.ticex.modules.mekanism.TicEXMekanismModule;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import slimeknights.tconstruct.library.tools.item.IModifiable;

@Mixin(value = ModuleHelper.class, remap = false, priority = 900)
public abstract class ModuleHelperMixin {

    @Shadow
    private void logDebugReceivedIMC(String imcMethod, String senderModId, IModuleDataProvider<?> moduleDataProvider){}

    @Shadow
    protected Map<Item, Set<ModuleData<?>>> supportedModules;

    @Inject(
        method = "processIMC",
        at = @At(
            value = "INVOKE",
            ordinal = 4,
            shift = At.Shift.AFTER,
            target = "Lmekanism/common/content/gear/ModuleHelper;mapSupportedModules(Lnet/minecraftforge/fml/event/lifecycle/InterModProcessEvent;Ljava/lang/String;Lmekanism/api/providers/IItemProvider;Ljava/util/Map;)V"
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    void processIMC(InterModProcessEvent event, CallbackInfo ci, Map<ModuleData<?>, ImmutableSet.Builder<Item>> supportedContainersBuilderMap) {
        mixinMapSupportedModules(event, TicEXMekanismModule.ADD_MEKAPLATE_HELMET_MODULES, TicEXRegistry.MEKAPLATE_ARMOR.get(ArmorItem.Type.HELMET), supportedContainersBuilderMap);
        mixinMapSupportedModules(event, TicEXMekanismModule.ADD_MEKAPLATE_CHESTPLATE_MODULES, TicEXRegistry.MEKAPLATE_ARMOR.get(ArmorItem.Type.CHESTPLATE), supportedContainersBuilderMap);
        mixinMapSupportedModules(event, TicEXMekanismModule.ADD_MEKAPLATE_LEGGINGS_MODULES, TicEXRegistry.MEKAPLATE_ARMOR.get(ArmorItem.Type.LEGGINGS), supportedContainersBuilderMap);
        mixinMapSupportedModules(event, TicEXMekanismModule.ADD_MEKAPLATE_BOOTS_MODULES, TicEXRegistry.MEKAPLATE_ARMOR.get(ArmorItem.Type.BOOTS), supportedContainersBuilderMap);
    }

    private void mixinMapSupportedModules(InterModProcessEvent event, String imcMethod, IModifiable item, Map<ModuleData<?>, ImmutableSet.Builder<Item>> supportedContainersBuilderMap){
        ImmutableSet.Builder<ModuleData<?>> supportedModulesBuilder = ImmutableSet.builder();
        event.getIMCStream(imcMethod::equals).forEach(message -> {
            Object body = message.messageSupplier().get();
            if (body instanceof IModuleDataProvider<?> moduleDataProvider) {
                supportedModulesBuilder.add(moduleDataProvider.getModuleData());
                logDebugReceivedIMC(imcMethod, message.senderModId(), moduleDataProvider);
            } else if (body instanceof IModuleDataProvider<?>[] providers) {
                for (IModuleDataProvider<?> moduleDataProvider : providers) {
                    supportedModulesBuilder.add(moduleDataProvider.getModuleData());
                    logDebugReceivedIMC(imcMethod, message.senderModId(), moduleDataProvider);
                }
            } else {
                Mekanism.logger.warn("Received IMC message for '{}' from mod '{}' with an invalid body.", imcMethod, message.senderModId());
            }
        });
        Set<ModuleData<?>> supported = supportedModulesBuilder.build();
        if (!supported.isEmpty() && item instanceof Item) {
            supportedModules.put((Item)item, supported);
            for (ModuleData<?> data : supported) {
                supportedContainersBuilderMap.computeIfAbsent(data, d -> ImmutableSet.builder()).add((Item)item);
            }
        }
    }
}
