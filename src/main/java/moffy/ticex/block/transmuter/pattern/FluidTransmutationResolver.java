package moffy.ticex.block.transmuter.pattern;

import com.mojang.datafixers.util.Pair;
import moffy.ticex.TicEXConfig;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import slimeknights.mantle.util.RegistryHelper;

import javax.annotation.Nullable;
import java.util.*;

public class FluidTransmutationResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(FluidTransmutationResolver.class);
    public static FluidTransmutationResolver INSTANCE = new FluidTransmutationResolver();

    private final List<FluidTransmutationPattern> patterns;
    private final List<FluidTransmutationExcludePattern> excludePatterns;
    private final Map<Fluid, FluidTransmutationPair> pairs;

    public FluidTransmutationResolver() {
        this.pairs = new HashMap<>();

        this.excludePatterns = new ArrayList<>();
        this.patterns = new ArrayList<>();
    }

    private boolean validateTag(TagKey<Fluid> tagKey) {
        return this.patterns.stream().anyMatch(pattern -> pattern.isValidTag(tagKey)) &&
                this.excludePatterns.stream().noneMatch(excludePattern -> excludePattern.isInvalidTag(tagKey));
    }

    private void resolveFluidTag(TagKey<Fluid> fluidTagKey) {
        List<Fluid> tagFluids = ForgeRegistries.FLUIDS.tags().getTag(fluidTagKey).stream()
                .filter(fluid -> fluid.isSource(fluid.defaultFluidState()))
                .toList();
        Fluid lastFluid = null;

        for (Fluid fluid : tagFluids) {
            if (lastFluid != null) {
                if (!pairs.containsKey(fluid)) {
                    pairs.put(lastFluid, new FluidTransmutationPair(lastFluid, fluid));
                    LOGGER.info("Conversion resolved {} -> {}", ForgeRegistries.FLUIDS.getKey(lastFluid), ForgeRegistries.FLUIDS.getKey(fluid));
                }
            }

            lastFluid = fluid;
        }

        if (tagFluids.size() >= 2) {
            Fluid firstFluid = tagFluids.get(0);
            pairs.put(lastFluid, new FluidTransmutationPair(lastFluid, firstFluid));
        }
    }

    public void initConfig() {
        for (String prefix : TicEXConfig.FLUID_TRANSMUTER_PATTERNS.get()) {
            patterns.add(new FluidTransmutationPattern(prefix));
        }

        for (String prefix : TicEXConfig.FLUID_TRANSMUTER_EXCLUDE_PATTERNS.get()) {
            excludePatterns.add(new FluidTransmutationExcludePattern(prefix));
        }
    }

    public void load() {
        this.pairs.clear();

        RegistryHelper.getRegistry(ForgeRegistries.Keys.FLUIDS).getTags()
                .map(Pair::getFirst)
                .filter(this::validateTag)
                .forEach(this::resolveFluidTag);
    }

    @Nullable
    public FluidTransmutationPair resolvePair(Fluid fluid) {
        if (pairs.containsKey(fluid)) {
            return pairs.get(fluid);
        }

        return null;
    }

    public Collection<FluidTransmutationPair> getPairs() {
        return pairs.values();
    }
}
