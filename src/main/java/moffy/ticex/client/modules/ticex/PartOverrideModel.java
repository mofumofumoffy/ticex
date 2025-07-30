package moffy.ticex.client.modules.ticex;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.math.Transformation;
import moffy.ticex.client.rendering.shader.ShaderProvider;
import moffy.ticex.client.rendering.shader.ShaderToolQuad;
import moffy.ticex.client.rendering.ticex.TicEXRenders;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.EmptyModel;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;
import net.minecraftforge.client.model.obj.ObjLoader;
import net.minecraftforge.client.model.obj.ObjMaterialLibrary;
import net.minecraftforge.client.model.obj.ObjModel;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class PartOverrideModel implements IUnbakedGeometry<PartOverrideModel> {
    public static final IGeometryLoader<PartOverrideModel> LOADER = PartOverrideModel::deserialize;

    private final ObjModel objModel;

    private final List<PartOverrideEntry> prefixEntries;

    public PartOverrideModel(ObjModel objModel, List<PartOverrideEntry> prefixEntries) {
        this.objModel = objModel;
        this.prefixEntries = prefixEntries;
    }

    public static BakedQuad modifyColorQuad(BakedQuad originalQuad, int rgb) {
        int[] vertexData = originalQuad.getVertices().clone();

        Color color = new Color(rgb);
        int abgr = ((color.getAlpha() & 0xFF) << 24) | ((color.getBlue() & 0xFF) << 16) | ((color.getGreen() & 0xFF) << 8) | (color.getRed() & 0xFF);

        // modify vertices color
        for (int vertex = 0; vertex < 4; vertex++) {
            int colorIndex = vertex * 8 + 3;
            vertexData[colorIndex] = abgr;
        }

        return new BakedQuad(
                vertexData,
                originalQuad.getTintIndex(),
                originalQuad.getDirection(),
                originalQuad.getSprite(),
                originalQuad.isShade()
        );
    }

    public static PartOverrideModel deserialize(JsonObject json, JsonDeserializationContext context) {
        ObjModel objModel = ObjLoader.INSTANCE.read(json, context);

        List<PartOverrideEntry> prefixEntries = JsonHelper.parseList(json, "part_override", PartOverrideEntry::read);
        return new PartOverrideModel(objModel, prefixEntries);
    }

    public BakedModel bake(IGeometryBakingContext context, ModelBaker modelBaker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, net.minecraft.client.renderer.block.model.ItemOverrides overrides, ResourceLocation modelLocation) {
        TextureAtlasSprite particle = spriteGetter.apply(context.getMaterial("particle"));
        ResourceLocation renderTypeHint = context.getRenderTypeHint();
        RenderTypeGroup renderTypes = renderTypeHint != null ? context.getRenderType(renderTypeHint) : RenderTypeGroup.EMPTY;

        overrides = new PartItemOverrides(modelState, context);
        IModelBuilder<?> modelBuilder = IModelBuilder.of(false, false, false, ItemTransforms.NO_TRANSFORMS, overrides, particle, renderTypes);
        return modelBuilder.build();
    }

    public BakedQuad wrapMaterialQuad(BakedQuad bakedQuad, MaterialVariantId materialId) {
        ShaderProvider.Tool shaderProvider = TicEXRenders.TOOL_SHADERS.getShaderProvider(materialId);
        if (shaderProvider != null) {
            return new ShaderToolQuad.Material(bakedQuad, shaderProvider, materialId);
        }

        Optional<MaterialRenderInfo> renderInfoOpt = MaterialRenderInfoLoader.INSTANCE.getRenderInfo(materialId);
        if (renderInfoOpt.isPresent()) {
            MaterialRenderInfo renderInfo = renderInfoOpt.get();
            Color color = new Color(renderInfo.vertexColor());
            return modifyColorQuad(bakedQuad, color.getRGB());
        }

        return bakedQuad;
    }

    public BakedModel bakeInternal(ItemStack itemStack, IGeometryBakingContext owner, ModelState modelState, net.minecraft.client.renderer.block.model.ItemOverrides overrides) {
        if (!(itemStack.getItem() instanceof IModifiable)) return EmptyModel.BAKED;

        ToolStack tool = ToolStack.from(itemStack);

        Map<String, QuadWrapper> prefixQuadWrapperMap = new HashMap<>();
        for (PartOverrideEntry prefixEntry : prefixEntries) {
            MaterialVariant material = tool.getMaterial(prefixEntry.index);
            prefixQuadWrapperMap.put(prefixEntry.prefix, bakedQuad -> this.wrapMaterialQuad(bakedQuad, material.getId()));
        }

        return bakeModifiedObjModel(this.objModel, owner, Material::sprite, modelState, overrides, prefixQuadWrapperMap);
    }

    public BakedModel bakeModifiedObjModel(ObjModel objModel, IGeometryBakingContext context, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, net.minecraft.client.renderer.block.model.ItemOverrides overrides, Map<String, QuadWrapper> prefixQuadWrapperMap) {
        TextureAtlasSprite particle = spriteGetter.apply(context.getMaterial("particle"));
        ResourceLocation renderTypeHint = context.getRenderTypeHint();
        RenderTypeGroup renderTypes = renderTypeHint != null ? context.getRenderType(renderTypeHint) : RenderTypeGroup.EMPTY;
        IModelBuilder<?> builder = IModelBuilder.of(context.useAmbientOcclusion(), context.useBlockLight(), context.isGui3d(), context.getTransforms(), overrides, particle, renderTypes);
        this.addQuads(objModel, context, builder, spriteGetter, modelState, prefixQuadWrapperMap);
        return builder.build();
    }

    protected void addQuads(ObjModel objModel, IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, Map<String, QuadWrapper> prefixQuadWrapperMap) {
        objModel.parts.values().stream()
                .filter((part) ->
                        owner.isComponentVisible(part.name(), true))
                .forEach((part) ->
                        addGroupQuads(part, owner, modelBuilder, spriteGetter, modelState, prefixQuadWrapperMap));
    }

    protected void addGroupQuads(ObjModel.ModelGroup modelGroup, IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, Map<String, QuadWrapper> prefixQuadWrapperMap) {
        Map<String, ObjModel.ModelObject> parts = modelGroup.parts;

        addPartQuads(modelGroup, owner, modelBuilder, spriteGetter, modelState, prefixQuadWrapperMap);

        parts.forEach((key, object) -> {
            addPartQuads(object, owner, modelBuilder, spriteGetter, modelState, prefixQuadWrapperMap);
        });
    }

    public void addPartQuads(ObjModel.ModelObject object, IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, Map<String, QuadWrapper> prefixQuadWrapperMap) {
        for (var mesh : object.meshes) {
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
                    modelBuilder.addUnculledFace(wrapBakedQuad(object.name, quad.getLeft(), prefixQuadWrapperMap));
                } else {
                    modelBuilder.addCulledFace(quad.getRight(), wrapBakedQuad(object.name, quad.getLeft(), prefixQuadWrapperMap));
                }
            }

        }
    }

    public BakedQuad wrapBakedQuad(String objectName, BakedQuad bakedQuad, Map<String, QuadWrapper> prefixQuadWrapperMap) {
        for (String prefix : prefixQuadWrapperMap.keySet()) {
            if (objectName.startsWith(prefix)) {
                return prefixQuadWrapperMap.get(prefix).apply(bakedQuad);
            }
        }

        return bakedQuad;
    }

    public interface QuadWrapper {
        BakedQuad apply(BakedQuad bakedQuad);
    }

    public record PartOverrideEntry(String prefix, int index) {
        public static PartOverrideEntry read(JsonElement json, String key) {
            JsonObject jsonObject = json.getAsJsonObject();

            String prefix = GsonHelper.getAsString(jsonObject, "prefix");
            int index = GsonHelper.getAsInt(jsonObject, "index");
            return new PartOverrideEntry(prefix, index);
        }
    }

    public class PartItemOverrides extends ItemOverrides {
        private final ModelState modelState;
        private final IGeometryBakingContext owner;
        Cache<MaterialToolKey, BakedModel> cache = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .build();

        public PartItemOverrides(ModelState modelState, IGeometryBakingContext owner) {
            this.modelState = modelState;
            this.owner = owner;
        }

        @Override
        public @Nullable BakedModel resolve(@NotNull BakedModel pModel, @NotNull ItemStack pStack, @Nullable ClientLevel pLevel, @Nullable LivingEntity pEntity, int pSeed) {
            try {
                return cache.get(new MaterialToolKey(MaterialIdNBT.from(pStack).getMaterials(), pStack.getItem()),
                        () -> PartOverrideModel.this.bakeInternal(pStack, owner, modelState, PartItemOverrides.this));
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        record MaterialToolKey(List<MaterialVariantId> materialVariantIds, Item item) {
        }
    }
}
