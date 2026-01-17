package moffy.ticex.mixin;

import net.minecraftforge.client.model.obj.ObjMaterialLibrary;
import net.minecraftforge.client.model.obj.ObjModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = ObjModel.ModelMesh.class, remap = false)
public interface ObjModel$ModelMeshAccessor {
    @Accessor("mat")
    ObjMaterialLibrary.Material getMaterial();

    @Accessor("faces")
    List<int[][]> getFaces();
}
