package moffy.ticex.client.modules.ticex;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;
import net.minecraftforge.client.model.obj.ObjMaterialLibrary;
import net.minecraftforge.client.model.obj.ObjModel;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector4f;

import java.util.Map;
import java.util.function.Function;

public abstract class AbstractObjOverrideModel<MODEL extends AbstractObjOverrideModel<MODEL>> implements IUnbakedGeometry<MODEL> {

    protected final ObjModel objModel;

    public AbstractObjOverrideModel(ObjModel objModel) {
        this.objModel = objModel;
    }

    public BakedModel bakeModifiedObjModel(ObjModel objModel, IGeometryBakingContext context, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ObjQuadWrapper quadWrapper) {
        TextureAtlasSprite particle = spriteGetter.apply(context.getMaterial("particle"));
        ResourceLocation renderTypeHint = context.getRenderTypeHint();
        RenderTypeGroup renderTypes = renderTypeHint != null ? context.getRenderType(renderTypeHint) : RenderTypeGroup.EMPTY;
        IModelBuilder<?> builder = IModelBuilder.of(context.useAmbientOcclusion(), context.useBlockLight(), context.isGui3d(), context.getTransforms(), overrides, particle, renderTypes);
        this.addQuads(objModel, context, builder, spriteGetter, modelState, quadWrapper);
        return builder.build();
    }

    protected void addQuads(ObjModel objModel, IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ObjQuadWrapper quadWrapper) {
        objModel.parts.values().stream()
                .filter((part) ->
                        owner.isComponentVisible(part.name(), true))
                .forEach((part) ->
                        addGroupQuads(part, owner, modelBuilder, spriteGetter, modelState, quadWrapper));
    }

    protected void addGroupQuads(ObjModel.ModelGroup modelGroup, IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ObjQuadWrapper quadWrapper) {
        Map<String, ObjModel.ModelObject> parts = modelGroup.parts;

        addPartQuads(modelGroup, modelGroup, owner, modelBuilder, spriteGetter, modelState, quadWrapper);

        parts.forEach((key, partObject) -> {
            addPartQuads(partObject, modelGroup, owner, modelBuilder, spriteGetter, modelState, quadWrapper);
        });
    }

    public void addPartQuads(ObjModel.ModelObject partObject, ObjModel.ModelGroup modelGroup, IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ObjQuadWrapper quadWrapper) {
        for (var mesh : partObject.meshes) {
            ObjMaterialLibrary.Material mat = mesh.mat;
            if (mat == null) {
                continue;
            }

            TextureAtlasSprite texture = spriteGetter.apply(UnbakedGeometryHelper.resolveDirtyMaterial(mat.diffuseColorMap, owner));
            int tintIndex = mat.diffuseTintIndex;
            Vector4f colorTint = mat.diffuseColor;
            Transformation rootTransform = owner.getRootTransform();
            Transformation transform = rootTransform.isIdentity() ? modelTransform.getRotation() : modelTransform.getRotation().compose(rootTransform);

            for (int[][] face : mesh.faces) {
                Pair<BakedQuad, Direction> quad = objModel.makeQuad(face, tintIndex, colorTint, mat.ambientColor, texture, transform);
                if (quad.getRight() == null) {
                    modelBuilder.addUnculledFace(quadWrapper.apply(quad.getLeft(), modelGroup, partObject));
                } else {
                    modelBuilder.addCulledFace(quad.getRight(), quadWrapper.apply(quad.getLeft(), modelGroup, partObject));
                }
            }

        }
    }

    public interface ObjQuadWrapper {
        BakedQuad apply(BakedQuad bakedQuad, ObjModel.ModelGroup groupObject, ObjModel.ModelObject partObject);
    }
}
