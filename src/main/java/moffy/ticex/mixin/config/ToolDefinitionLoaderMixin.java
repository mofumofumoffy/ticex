package moffy.ticex.mixin.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import moffy.ticex.TicEX;
import moffy.ticex.TicEXConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionData;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionLoader;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;

import java.util.Map;

@Mixin(value = ToolDefinitionLoader.class, remap = false)
public class ToolDefinitionLoaderMixin {
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At("HEAD"))
    private void modify(@NotNull Map<ResourceLocation, JsonElement> splashList, ResourceManager resourceManagerIn, ProfilerFiller profilerIn, CallbackInfo ci) {
        splashList.forEach(((resourceLocation, jsonElement) -> {
            TicEX.LOGGER.debug("At Resource Location {}, found ToolDefinitionData {}", resourceLocation, jsonElement);
            modifySlotsWithAU("ticex", "meka_tool", TicEXConfig.MEKA_EDGE_ABILITY_SLOTS.get(), TicEXConfig.MEKA_EDGE_UPGRADE_SLOTS.get(), resourceLocation, jsonElement);
        }));
    }

    private void modifySlotsWithAU(String ns, String pth, int aSlot, int uSlot, ResourceLocation rl, JsonElement json) {
        if (rl.getNamespace().equals(ns) && rl.getPath().equals(pth)) {
            JsonArray modules = json.getAsJsonObject().getAsJsonArray("modules");
            if (modules != null) {
                modules.forEach(elem -> {
                    JsonObject module = elem.getAsJsonObject();
                    if (module.has("type") && module.get("type").getAsString().equals("tconstruct:modifier_slots")) {
                        JsonObject slots = module.getAsJsonObject("slots");
                        if (slots != null) {
                            slots.addProperty("abilities", aSlot);
                            slots.addProperty("upgrades", uSlot);
                        }
                    }
                });
            }
        }
    }

    private void modifySlotsWithDU(String ns, String pth, int dSlot, int uSlot, ResourceLocation rl, JsonElement json) {
        if (rl.getNamespace().equals(ns) && rl.getPath().equals(pth)) {
            JsonArray modules = json.getAsJsonObject().getAsJsonArray("modules");
            if (modules != null) {
                modules.forEach(elem -> {
                    JsonObject module = elem.getAsJsonObject();
                    if (module.has("type") && module.get("type").getAsString().equals("tconstruct:modifier_slots")) {
                        JsonObject slots = module.getAsJsonObject("slots");
                        if (slots != null) {
                            slots.addProperty("defense", dSlot);
                            slots.addProperty("upgrades", uSlot);
                        }
                    }
                });
            }
        }
    }
}
