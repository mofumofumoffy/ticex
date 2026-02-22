package moffy.ticex.client.render.slashblade;

import moffy.ticex.TicEX;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import java.util.Optional;

public class SBToolRenderType {

    public enum PartType {
        BLADE(0, "blade"),
        HANDLE(2, "handle"),
        SAYA(1, "saya");

        private static final ResourceLocation BLADE_TEXTURE_LOC = ResourceLocation.fromNamespaceAndPath(
                TicEX.MODID,
                "textures/obj_tool/slashblade_tool/"
        );
        private static final ResourceLocation DEFAULT_BLADE_TEXTURE_LOC = ResourceLocation.fromNamespaceAndPath(
                TicEX.MODID,
                "textures/obj_tool/slashblade_tool/"
        );

        private final int index;
        private final String name;

        PartType(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public static PartType byIndex(int layerIndex) {
            return switch (layerIndex) {
                case 0 -> BLADE;
                case 2 -> HANDLE;
                case 1 -> SAYA;
                default -> null;
            };
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

        public boolean textureExsists(ResourceLocation location) {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            Optional<Resource> resource = resourceManager.getResource(location);
            return resource.isPresent();
        }

        public ResourceLocation tryTexture(MaterialVariantId material, Runnable whenIsDefault) {
            String suffix = "_" + material.getId().getNamespace() + "_" + material.getId().getPath();
            if (material.hasVariant()) {
                suffix += "_" + material.getVariant();
            }

            if (textureExsists(BLADE_TEXTURE_LOC.withSuffix(this.name + suffix + ".png"))) {
                return BLADE_TEXTURE_LOC.withSuffix(this.name + suffix + ".png");
            }

            if (whenIsDefault != null) {
                whenIsDefault.run();
            }
            return DEFAULT_BLADE_TEXTURE_LOC.withSuffix(this.name + ".png");
        }
    }
}
