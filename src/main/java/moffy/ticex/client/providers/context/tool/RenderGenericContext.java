package moffy.ticex.client.providers.context.tool;

import com.mojang.blaze3d.vertex.VertexConsumer;
import moffy.ticex.client.providers.context.RenderContext;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

public record RenderGenericContext(
        RenderContext renderContext,
        ResourceLocation atlasLocation,
        RenderType renderType,
        BufferGetter bufferGetter,
        ModDataNBT persistentData,
        boolean onGui
) {
    public interface BufferGetter {
        VertexConsumer get(RenderType renderType);
    }
}
