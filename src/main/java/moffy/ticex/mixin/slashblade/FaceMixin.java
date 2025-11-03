package moffy.ticex.mixin.slashblade;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mods.flammpfeil.slashblade.client.renderer.model.obj.Face;
import moffy.ticex.lib.context.TicEXContexts;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Face.class, remap = false)
public class FaceMixin {
    @WrapOperation(method = "putVertex", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;uv(FF)Lcom/mojang/blaze3d/vertex/VertexConsumer;", remap = true))
    public VertexConsumer modifyUV(VertexConsumer instance, float u, float v, Operation<VertexConsumer> original) {
        TextureAtlasSprite sprite = TicEXContexts.SB_FACE_SPRITE.get();
        if(sprite == null) {
            return original.call(instance, u, v);
        }

        return original.call(
                instance,
                sprite.getU0() + u * (sprite.getU1() - sprite.getU0()),
                sprite.getV0() + v * (sprite.getV1() - sprite.getV0())
        );
    }
}
