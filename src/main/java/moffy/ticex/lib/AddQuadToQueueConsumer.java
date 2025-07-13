package moffy.ticex.lib;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import moffy.ticex.client.PartPredicate;
import moffy.ticex.client.ShaderToolRenderUtils;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;

@FunctionalInterface
public interface AddQuadToQueueConsumer {
    public void accept(RenderType renderType, List<BakedQuad> quads, List<ShaderToolRenderUtils.RenderTask> queue, @Nullable Set<PartPredicate> seen);
}
