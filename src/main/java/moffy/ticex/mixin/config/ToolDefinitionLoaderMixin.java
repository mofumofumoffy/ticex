package moffy.ticex.mixin.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import moffy.ticex.TicEXConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionLoader;

import java.util.Map;

@Mixin(value = ToolDefinitionLoader.class, remap = false)
public class ToolDefinitionLoaderMixin {
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"))
    private void modify(@NotNull Map<ResourceLocation, JsonElement> splashList, ResourceManager resourceManagerIn, ProfilerFiller profilerIn, CallbackInfo ci) {
        try {
            if(TicEXConfig.USE_MORE_CONFIG != null && TicEXConfig.USE_MORE_CONFIG.get()){
                var slotConfigMap = TicEXConfig.SLOTS_CONFIG.get().toNestedMap();
                slotConfigMap.forEach((key, value) -> {
                    splashList.forEach((resourceLocation, jsonElement) -> {
                        if(key.equals(resourceLocation.toString())){
                            if(value instanceof Map<?, ?> slotMap){
                                slotMap.forEach((key1, value1) -> {
                                    if(key1 instanceof String slotName && value1 instanceof Integer slotSize){
                                        ticex$modifySlots(jsonElement, slotName, slotSize);
                                    }
                                });
                            }
                        }
                    });
                });
            }
        } catch (IllegalStateException ignored){}
    }

    @Unique
    private void ticex$modifySlots(JsonElement json, String slotName, int value) {
        JsonArray modules = json.getAsJsonObject().getAsJsonArray("modules");
        if (modules != null) {
            modules.forEach(elem -> {
                JsonObject module = elem.getAsJsonObject();
                if (module.has("type") && "tconstruct:modifier_slots".equals(module.get("type").getAsString())) {
                    JsonObject slotObj = module.getAsJsonObject("slots");
                    if (slotObj != null && SlotType.isValidName(slotName)) {
                        slotObj.addProperty(slotName, value);
                    }
                }
            });
        }
    }
}
