package moffy.ticex.client.render.shader;

import net.minecraft.client.renderer.block.model.BakedQuad;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierId;

public abstract class ShaderToolQuad extends BakedQuad {

    private final ShaderProvider.Tool shaderProvider;

    public ShaderToolQuad(BakedQuad original, ShaderProvider.Tool shaderProvider) {
        super(
                original.getVertices(),
                original.getTintIndex(),
                original.getDirection(),
                original.getSprite(),
                original.isShade()
        );
        this.shaderProvider = shaderProvider;
    }

    public ShaderProvider.Tool getShaderProvider() {
        return shaderProvider;
    }

    public static class Material extends ShaderToolQuad {
        private final MaterialVariantId materialId;

        public Material(BakedQuad original, ShaderProvider.Tool shaderProvider, MaterialVariantId materialId) {
            super(original, shaderProvider);
            this.materialId = materialId;
        }

        public MaterialVariantId getMaterialId() {
            return materialId;
        }
    }

    public static class Modifier extends ShaderToolQuad {
        private final ModifierId modifierId;

        public Modifier(BakedQuad original, ShaderProvider.Tool shaderProvider, ModifierId modifierId) {
            super(original, shaderProvider);
            this.modifierId = modifierId;
        }

        public ModifierId getModifierId() {
            return modifierId;
        }
    }
}
