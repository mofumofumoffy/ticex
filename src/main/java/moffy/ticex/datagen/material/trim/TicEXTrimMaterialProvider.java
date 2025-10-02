package moffy.ticex.datagen.material.trim;

import moffy.ticex.lib.TicEXMaterials;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.Util;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.registration.object.MetalItemObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.definition.MaterialId;

import java.util.Map;

public class TicEXTrimMaterialProvider {
    private static final String TRIM_FORMAT = TConstruct.makeDescriptionId("trim_material", "format");

    public TicEXTrimMaterialProvider() {
    }

    public static void register(RegistrySetBuilder builder) {
        builder.add(Registries.TRIM_MATERIAL, TicEXTrimMaterialProvider::registerTrimMaterials);
    }

    private static void registerTrimMaterials(BootstapContext<TrimMaterial> context) {
        material(context, TicEXMaterials.ETHERIC, TicEXRegistry.ETHERIC_INGOT.get(), 0x6AEF3C, 0.7F);
    }

    private static void material(BootstapContext<TrimMaterial> context, MaterialId material, MetalItemObject ingredient, int color, float modelIndex) {
        material(context, material, (ItemLike)ingredient.getIngot(), color, modelIndex);
    }

    private static void material(BootstapContext<TrimMaterial> context, MaterialId material, ItemLike ingredient, int color, float modelIndex) {
        context.register(ResourceKey.create(Registries.TRIM_MATERIAL, material), TrimMaterial.create(material.getSuffix(), ingredient.asItem(), modelIndex, Component.translatable(TRIM_FORMAT, new Object[]{Component.translatable(Util.makeDescriptionId("material", material))}).withStyle((style) -> style.withColor(color)), Map.of()));
    }
}
