package moffy.ticex.mixin;

import net.minecraftforge.client.model.obj.ObjModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = ObjModel.ModelObject.class, remap = false)
public interface ObjModel$ModelObjectAccessor {
    @Accessor("meshes")
    List<?> getMeshes();
}
