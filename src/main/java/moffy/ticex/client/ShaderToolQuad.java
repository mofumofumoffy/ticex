package moffy.ticex.client;

import net.minecraft.client.renderer.block.model.BakedQuad;

public class ShaderToolQuad extends BakedQuad {

    private PartPredicate predicate;

    public ShaderToolQuad(BakedQuad original, PartPredicate predicate) {
        super(
            original.getVertices(),
            original.getTintIndex(),
            original.getDirection(),
            original.getSprite(),
            original.isShade()
        );
        this.predicate = predicate;
    }

    public PartPredicate getPredicate() {
        return predicate;
    }
}
