package moffy.ticex.block.transmuter.pattern;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.tags.ITagManager;

public record FluidTransmutationPattern(String tagPrefix) {
    public ITagManager<Fluid> getTagManager() {
        IForgeRegistry<Fluid> registry = ForgeRegistries.FLUIDS;
        ITagManager<Fluid> manager = registry.tags();
        if (manager == null) {
            throw new IllegalStateException("Expected " + registry.getRegistryName() + " to have tags.");
        }
        return manager;
    }

    public boolean isValidTag(TagKey<Fluid> tag) {
        if (tag != null && getTagManager().isKnownTagName(tag)) {
            ResourceLocation location = tag.location();
            return location.toString().startsWith(tagPrefix);
        }

        return false;
    }
}
