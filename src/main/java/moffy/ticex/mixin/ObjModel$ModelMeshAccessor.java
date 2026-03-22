package moffy.ticex.mixin;

import net.minecraftforge.client.model.obj.ObjMaterialLibrary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(targets = "net.minecraftforge.client.model.obj.ObjModel$ModelMesh", remap = false)
public interface ObjModel$ModelMeshAccessor {
    @Accessor("mat")
    ObjMaterialLibrary.Material getMaterial();

    @Accessor("faces")
    List<int[][]> getFaces();
}
