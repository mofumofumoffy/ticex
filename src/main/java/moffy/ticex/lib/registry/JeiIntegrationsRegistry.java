package moffy.ticex.lib.registry;

import moffy.ticex.jei.IJeiIntegration;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.Lazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class JeiIntegrationsRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(JeiIntegrationsRegistry.class);
    private final Map<ResourceLocation, Lazy<? extends IJeiIntegration>> entries;
    private final Map<ResourceLocation, IJeiIntegration> bakedIntegrations;

    public JeiIntegrationsRegistry() {
        this.entries = new HashMap<>();
        this.bakedIntegrations = new HashMap<>();
    }

    public void register(ResourceLocation resourceLocation, Class<? extends IJeiIntegration> reference) {
        this.entries.put(resourceLocation, Lazy.of(() -> createIntegration(reference)));
    }

    public <T extends IJeiIntegration> IJeiIntegration createIntegration(Class<T> referenceClass) {
        try {
            Constructor<T> constructor = referenceClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            LOGGER.error("Cannot load jei integration {}", referenceClass);
            return null;
        }
    }

    // run with jei

    public void init() {
        entries.forEach((rl, lazySupplier) -> {
            IJeiIntegration jeiIntegration = lazySupplier.get();
            if(jeiIntegration != null) {
                bakedIntegrations.put(rl, jeiIntegration);
            }
        });
    }

    public void each(Consumer<IJeiIntegration> consumer) {
        this.bakedIntegrations.values().forEach(consumer);
    }

    public <T> void each(T value, BiConsumer<IJeiIntegration, T> consumer) {
        this.bakedIntegrations.values().forEach(integration -> consumer.accept(integration, value));
    }
}
