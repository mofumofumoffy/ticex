package moffy.ticex.mixin;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraftforge.client.model.obj.ObjModel;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(value = ObjModel.class, remap = false)
public interface ObjModelAccessor {
    @Accessor("parts")
    Map<String, ObjModel.ModelGroup> getParts();

    @Invoker("makeQuad")
    Pair<BakedQuad, Direction> invokeMakeQuad(int[][] indices, int tintIndex, Vector4f colorTint, Vector4f ambientColor, TextureAtlasSprite texture, Transformation transform);
}
