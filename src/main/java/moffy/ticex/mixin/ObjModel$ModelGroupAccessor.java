package moffy.ticex.mixin;

import net.minecraftforge.client.model.obj.ObjModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = ObjModel.ModelGroup.class, remap = false)
public interface ObjModel$ModelGroupAccessor {
    @Accessor("parts")
    Map<String, ObjModel.ModelObject> getParts();
}
