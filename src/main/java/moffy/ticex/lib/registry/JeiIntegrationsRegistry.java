package moffy.ticex.lib.registry;

import cpw.mods.util.Lazy;
import moffy.ticex.jei.IJeiIntegration;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class JeiIntegrationsRegistry {
    private final Map<ResourceLocation, Lazy<? extends IJeiIntegration>> entries;
    private final Map<ResourceLocation, IJeiIntegration> integrations;

    public JeiIntegrationsRegistry() {
        this.entries = new HashMap<>();
        this.integrations = new HashMap<>();
    }

    public <INTEGRATION extends IJeiIntegration> void register(ResourceLocation resourceLocation, Lazy<INTEGRATION> lazySupplier) {
        this.entries.put(resourceLocation, lazySupplier);
    }

    // run with jei

    public void init() {
        entries.forEach((rl, lazySupplier) -> {
            IJeiIntegration jeiIntegration = lazySupplier.get();
            integrations.put(rl, jeiIntegration);
        });
    }

    public void each(Consumer<IJeiIntegration> consumer) {
        this.integrations.values().forEach(consumer);
    }

    public <T> void each(T value, BiConsumer<IJeiIntegration, T> consumer) {
        this.integrations.values().forEach(integration -> consumer.accept(integration, value));
    }
}
