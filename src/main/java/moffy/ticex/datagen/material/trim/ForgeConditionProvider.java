package moffy.ticex.datagen.material.trim;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class ForgeConditionProvider implements DataProvider {

    private final Multimap<ResourceLocation, ICondition> conditions = MultimapBuilder.hashKeys().arrayListValues().build();
    private final PackOutput.PathProvider pathProvider;

    public ForgeConditionProvider(PackOutput output, PackOutput.Target packTarget) {
        this.pathProvider = output.createPathProvider(packTarget, "");
    }

    public void addCondition(ResourceLocation location, ICondition condition) {
        this.conditions.put(location, condition);
    }

    public void addConditions(ResourceLocation[] locations, ICondition condition) {
        for (ResourceLocation location : locations) {
            this.conditions.put(location, condition);
        }
    }

    public abstract void gatherConditions();

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
        this.gatherConditions();

        List<? extends CompletableFuture<?>> trimFutures =
                conditions.keySet().stream()
                        .map(resourceLocation -> this.accept(cachedOutput, resourceLocation, this.conditions.get(resourceLocation).toArray(ICondition[]::new)))
                        .toList();

        return CompletableFuture.allOf(trimFutures.toArray(CompletableFuture[]::new));
    }

    public CompletableFuture<?> accept(CachedOutput cachedOutput, ResourceLocation path, ICondition[] conditions) {
        Path jsonPath = pathProvider.json(path);

        return CompletableFuture.runAsync(() -> {
            try {
                JsonElement json = readJson(jsonPath);
                JsonObject jsonObject = json.getAsJsonObject();

                jsonObject.add("forge:conditions", CraftingHelper.serialize(conditions));

                DataProvider.saveStable(cachedOutput, json, jsonPath);
            } catch (IOException err) {
                LOGGER.error("Failed to save file to {}", jsonPath, err);
            }

        }, Util.backgroundExecutor());
    }

    public static JsonElement readJson(Path pPath) throws IOException {
        JsonElement jsonElement;
        try (Reader reader = Files.newBufferedReader(pPath)) {
            jsonElement = JsonParser.parseReader(reader);
        }
        return jsonElement;
    }
}
