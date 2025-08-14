package moffy.ticex.mixin.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import moffy.ticex.TicEX;
import moffy.ticex.TicEXConfig;
import moffy.ticex.lib.config.SlotValues;
import moffy.ticex.lib.config.ToolSlotPreset;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionData;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionLoader;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;

import java.util.Map;
import java.util.Optional;

@Mixin(value = ToolDefinitionLoader.class, remap = false)
public class ToolDefinitionLoaderMixin {
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"))
    private void modify(@NotNull Map<ResourceLocation, JsonElement> splashList, ResourceManager resourceManagerIn, ProfilerFiller profilerIn, CallbackInfo ci) {
        try {
            if(TicEXConfig.USE_MORE_CONFIG.get()){
                splashList.forEach((resourceLocation, jsonElement) -> {
                    TicEXConfig.SLOTS_CONFIG.forEach((rl, spec) -> {
                        if (rl.equals(resourceLocation)) {
                            SlotValues slots = SlotValues.fromSpec(spec);
                            if (slots != null) {
                                modifySlots(resourceLocation, jsonElement, slots);
                            }
                        }
                    });
                });
            }
        } catch (IllegalStateException ignored){}
    }

    @Unique
    private void modifySlots(ResourceLocation rl, JsonElement json, SlotValues slots) {
        JsonArray modules = json.getAsJsonObject().getAsJsonArray("modules");
        if (modules != null) {
            modules.forEach(elem -> {
                JsonObject module = elem.getAsJsonObject();
                if (module.has("type") && "tconstruct:modifier_slots".equals(module.get("type").getAsString())) {
                    JsonObject slotObj = module.getAsJsonObject("slots");
                    if (slotObj != null) {
                        if (slots.abilities != null) slotObj.addProperty("abilities", slots.abilities);
                        if (slots.defense != null) slotObj.addProperty("defense", slots.defense);
                        if (slots.upgrades != null) slotObj.addProperty("upgrades", slots.upgrades);
                    }
                }
            });
        }
    }
}
