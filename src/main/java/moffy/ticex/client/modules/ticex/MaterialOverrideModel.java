package moffy.ticex.client.modules.ticex;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
import net.minecraftforge.client.model.obj.ObjLoader;
import net.minecraftforge.client.model.obj.ObjModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class MaterialOverrideModel extends AbstractObjOverrideModel<MaterialOverrideModel> {
    public static final IGeometryLoader<MaterialOverrideModel> LOADER = MaterialOverrideModel::deserialize;
    private static final Logger LOGGER = LoggerFactory.getLogger(MaterialOverrideModel.class);
    private final List<MaterialOverrideEntry> prefixEntries;

    public MaterialOverrideModel(ObjModel objModel, List<MaterialOverrideEntry> prefixEntries) {
        super(objModel);
        this.prefixEntries = prefixEntries;
    }

    public static MaterialOverrideModel deserialize(JsonObject json, JsonDeserializationContext context) {
        ObjModel objModel = ObjLoader.INSTANCE.read(json, context);

        List<MaterialOverrideEntry> prefixEntries = JsonHelper.parseList(json, "material_overrides", MaterialOverrideEntry::read);
        return new MaterialOverrideModel(objModel, prefixEntries);
    }

    public static BakedQuad modifyQuadColor(BakedQuad originalQuad, int color) {
        int[] vertexData = originalQuad.getVertices().clone();

        // modify vertices color
        for (int vertex = 0; vertex < 4; vertex++) {
            int colorIndex = vertex * 8 + 3;
            vertexData[colorIndex] = color;
        }

        return new BakedQuad(
                vertexData,
                originalQuad.getTintIndex(),
                originalQuad.getDirection(),
                originalQuad.getSprite(),
                originalQuad.isShade()
        );
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker modelBaker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
        TextureAtlasSprite particle = spriteGetter.apply(context.getMaterial("particle"));
        ResourceLocation renderTypeHint = context.getRenderTypeHint();
        RenderTypeGroup renderTypes = renderTypeHint != null ? context.getRenderType(renderTypeHint) : RenderTypeGroup.EMPTY;

        overrides = new PartItemOverrides(context, modelState);
        IModelBuilder<?> modelBuilder = IModelBuilder.of(false, false, false, ItemTransforms.NO_TRANSFORMS, overrides, particle, renderTypes);
        return modelBuilder.build();
    }

    public BakedModel bakeInternal(ItemStack itemStack, IGeometryBakingContext owner, ModelState modelState, ItemOverrides overrides) {
        if (!(itemStack.getItem() instanceof IModifiable)) {
            LOGGER.error("Failed to bake model for item: {}", itemStack.getItem());
            return EmptyModel.BAKED;
        }
        ;

        ToolStack tool = ToolStack.from(itemStack);

        return bakeModifiedObjModel(this.objModel, owner, Material::sprite, modelState, overrides,
                (bakedQuad, group, object) -> modifyQuad(tool, bakedQuad, group, object));
    }

    public BakedQuad modifyQuad(ToolStack tool, BakedQuad bakedQuad, ObjModel.ModelGroup group, ObjModel.ModelObject partObject) {
        int size = tool.getMaterials().size();
        for (MaterialOverrideEntry prefixEntry : prefixEntries) {
            if (partObject.name.startsWith(prefixEntry.prefix) || partObject.name.equals(prefixEntry.name)) {
                int index = prefixEntry.index;
                if (index < 0 || index >= size) continue;
                MaterialVariant material = tool.getMaterial(index);

                return wrapMaterialQuad(bakedQuad, material.getId());
            }
        }

        return bakedQuad;
    }

    public BakedQuad wrapMaterialQuad(BakedQuad bakedQuad, MaterialVariantId materialId) {
        ShaderProvider.Tool shaderProvider = TicEXRenders.TOOL_SHADERS.getShaderProvider(materialId);
        if (shaderProvider != null) {
            return new ShaderToolQuad.Material(bakedQuad, shaderProvider, materialId);
        }

        Optional<MaterialRenderInfo> renderInfoOpt = MaterialRenderInfoLoader.INSTANCE.getRenderInfo(materialId);
        if (renderInfoOpt.isPresent()) {
            MaterialRenderInfo renderInfo = renderInfoOpt.get();
            int argb = renderInfo.vertexColor();

            int a = (argb >> 24) & 0xFF;
            int r = (argb >> 16) & 0xFF;
            int g = (argb >> 8) & 0xFF;
            int b = (argb) & 0xFF;

            int abgr = (a << 24) | (b << 16) | (g << 8) | r;
            return modifyQuadColor(bakedQuad, abgr);
        }

        return bakedQuad;
    }

    public record MaterialOverrideEntry(String prefix, String name, int index) {
        public static MaterialOverrideEntry read(JsonElement json, String key) {
            JsonObject jsonObject = json.getAsJsonObject();

            String prefix = jsonObject.has("prefix") ? GsonHelper.getAsString(jsonObject, "prefix") : "";
            String name = jsonObject.has("name") ? GsonHelper.getAsString(jsonObject, "name") : "";
            int index = GsonHelper.getAsInt(jsonObject, "index");
            return new MaterialOverrideEntry(prefix, name, index);
        }
    }

    public class PartItemOverrides extends ItemOverrides {
        private final IGeometryBakingContext owner;
        private final ModelState modelState;
        Cache<MaterialToolKey, BakedModel> cache = CacheBuilder.newBuilder()
                .expireAfterAccess(5L, TimeUnit.MINUTES)
                .build();

        public PartItemOverrides(IGeometryBakingContext owner, ModelState modelState) {
            this.owner = owner;
            this.modelState = modelState;
        }

        @Override
        public @Nullable BakedModel resolve(@NotNull BakedModel pModel, @NotNull ItemStack pStack, @Nullable ClientLevel pLevel, @Nullable LivingEntity pEntity, int pSeed) {
            try {
                return cache.get(new MaterialToolKey(MaterialIdNBT.from(pStack).getMaterials(), pStack.getItem()),
                        () -> MaterialOverrideModel.this.bakeInternal(pStack, owner, modelState, PartItemOverrides.this));
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        record MaterialToolKey(List<MaterialVariantId> materialVariantIds, Item item) {
        }
    }
}
