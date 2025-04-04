package moffy.ticex.mixin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;

import moffy.ticex.TicEX;
import moffy.ticex.client.PartPredicate;
import moffy.ticex.client.ShaderToolQuad;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.client.model.util.MantleItemLayerModel;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.mantle.util.ReversedListBuilder;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo.TintedSprite;
import slimeknights.tconstruct.library.client.model.UniqueGuiModel;
import slimeknights.tconstruct.library.client.model.tools.MaterialModel;
import slimeknights.tconstruct.library.client.model.tools.ToolModel;
import slimeknights.tconstruct.library.client.modifiers.IBakedModifierModel;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

@Mixin(ToolModel.class)
public class ToolModelMixin {

    @Shadow( remap = false )
    private static BitSet SMALL_TOOL_TYPES;

    @Shadow( remap = false )
    private static void addModifierQuads(Function<Material, TextureAtlasSprite> spriteGetter, Map<ModifierId, IBakedModifierModel> modifierModels, List<?> firstModifiers, IToolStackView tool, Consumer<Collection<BakedQuad>> quadConsumer, @Nullable ItemLayerPixels pixels, Transformation transforms, boolean isLarge){}

    @Shadow( remap = false )
    private static IModelBuilder<?> makeModelBuilder(IGeometryBakingContext context, ItemOverrides overrides, TextureAtlasSprite particle) {
        return null;
    }

    @Inject(
        at = @At("invoke"),
        method = "bakeInternal",
        cancellable = true,
        remap = false
    )
    private static void bakeInternalWithShader(IGeometryBakingContext owner, Function<Material, TextureAtlasSprite> spriteGetter, @Nullable Transformation largeTransforms, List<?> parts, Map<ModifierId, IBakedModifierModel> modifierModels, List<?> firstModifiers, List<MaterialVariantId> materials, @Nullable IToolStackView tool, ItemOverrides overrides, CallbackInfoReturnable<BakedModel> cb){
        if(tool != null && (TicEXRegistry.TOOL_SHADERS.isToolTarget(tool))){
            Transformation smallTransforms = Transformation.identity();

            
            ReversedListBuilder<Collection<BakedQuad>> smallQuads = new ReversedListBuilder<>();
            ItemLayerPixels smallPixels = new ItemLayerPixels();
            
            ReversedListBuilder<Collection<BakedQuad>> largeQuads = largeTransforms != null ? new ReversedListBuilder<>() : smallQuads;
            ItemLayerPixels largePixels = largeTransforms != null ? new ItemLayerPixels() : smallPixels;

            
            if (!modifierModels.isEmpty()) {
                addModifierQuads(spriteGetter, modifierModels, firstModifiers, tool, smallQuads::add, smallPixels, smallTransforms, false);
                
                if (largeTransforms != null) {
                    addModifierQuads(spriteGetter, modifierModels, firstModifiers, tool, largeQuads::add, largePixels, largeTransforms, true);
                }
            }

            
            TextureAtlasSprite particle = null;
            for (int i = parts.size() - 1; i >= 0; i--) {
                Object part = parts.get(i);

                
                if (reflectMethod(part.getClass(), "hasMaterials", part).equals(true)) {
                    
                    int index = (int)reflectMethod(part.getClass(), "index", part);
                    MaterialVariantId material = index < materials.size() ? materials.get(index) : IMaterial.UNKNOWN_ID;
                    TintedSprite materialSprite = MaterialModel.getMaterialSprite(spriteGetter, owner.getMaterial((String)reflectMethod(part.getClass(), "getName", part, false)), material);
                    particle = materialSprite.sprite();

                    
                    addShaderQuads(material, MantleItemLayerModel.getQuadsForSprite(materialSprite.color(), -1, materialSprite.sprite(), smallTransforms, materialSprite.emissivity(), smallPixels), smallQuads::add);
                    if (largeTransforms != null) {
                        addShaderQuads(material, MaterialModel.getQuadsForMaterial(spriteGetter, owner.getMaterial((String)reflectMethod(part.getClass(), "getName", part, true)), material, -1, largeTransforms, largePixels), largeQuads::add);
                    }
                } else {
                        
                        particle = spriteGetter.apply(owner.getMaterial((String)reflectMethod(part.getClass(), "getName", part, false)));
                        
                        smallQuads.add(MantleItemLayerModel.getQuadsForSprite(-1, -1, particle, smallTransforms, 0, smallPixels));
                    if (largeTransforms != null) {
                        largeQuads.add(MantleItemLayerModel.getQuadsForSprite(-1, -1, spriteGetter.apply(owner.getMaterial((String)reflectMethod(part.getClass(), "getName", part, true))), largeTransforms, 0, largePixels));
                    }
                }
            }
            
            if (particle == null) {
            particle = spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, MissingTextureAtlasSprite.getLocation()));
            TConstruct.LOG.error("Created tool model without a particle sprite, this means it somehow has no parts. This should not be possible");
            }

            
            IModelBuilder<?> smallModelBuilder = makeModelBuilder(owner, overrides, particle);
            IModelBuilder<?> guiModelBuilder = makeModelBuilder(owner, overrides, particle);
            smallQuads.build(quads -> quads.forEach(quad -> {
            smallModelBuilder.addUnculledFace(quad);
            if (quad.getDirection() == Direction.SOUTH) {
                guiModelBuilder.addUnculledFace(quad);
            }
            }));
            if (largeTransforms == null) {
                cb.setReturnValue(wrapModel(tool, new UniqueGuiModel.Baked(smallModelBuilder.build(), guiModelBuilder.build())));
            }
            IModelBuilder<?> largeModelBuilder = makeModelBuilder(owner, overrides, particle);
            largeQuads.build(quads -> quads.forEach(largeModelBuilder::addUnculledFace));
            cb.setReturnValue(wrapModel(tool, new BakedLargeToolModel(largeModelBuilder.build(), smallModelBuilder.build(), guiModelBuilder.build())));
        }
    }

    @Inject(
        at = @At("return"),
        method = "bakeInternal",
        cancellable = true,
        remap = false
    )
    private static void bakeInternalWithCustomModel(IGeometryBakingContext owner, Function<Material, TextureAtlasSprite> spriteGetter, @Nullable Transformation largeTransforms, List<?> parts, Map<ModifierId, IBakedModifierModel> modifierModels, List<?> firstModifiers, List<MaterialVariantId> materials, @Nullable IToolStackView tool, ItemOverrides overrides, CallbackInfoReturnable<BakedModel> cb){
        cb.setReturnValue(wrapModel(tool, cb.getReturnValue()));
    }

    private static void addShaderQuads(MaterialVariantId id, List<BakedQuad> quads, Consumer<Collection<BakedQuad>>addFn){
        addFn.accept(quads.stream().map(quad-> quad == null ? null : (BakedQuad)new ShaderToolQuad(quad, new PartPredicate(id))).toList());  
    }

   private static Object reflectMethod(Class<?> cls, String methodName, Object object, Object... params){
        try{
            Method method = Arrays.stream(cls.getDeclaredMethods())
            .filter(m -> m.getName().equals(methodName))
            .filter(m -> {
                Class<?>[] paramTypes = m.getParameterTypes();
                if (paramTypes.length != params.length) return false;
                for (int i = 0; i < paramTypes.length; i++) {
                    if (!isAssignable(paramTypes[i], params[i].getClass())) {
                        return false;
                    }
                }
                return true;
            })
            .findFirst()
            .orElseThrow(NoSuchMethodException::new);
            method.setAccessible(true);
            return method.invoke(object, params);
        } catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException e){
            TicEX.LOGGER.error("", e);
        }
        return null;
   }

   private static boolean isAssignable(Class<?> target, Class<?> actual) {
        if (target.isPrimitive()) {
            return switch (target.getName()) {
                case "int" -> actual == Integer.class;
                case "double" -> actual == Double.class;
                case "boolean" -> actual == Boolean.class;
                case "long" -> actual == Long.class;
                case "float" -> actual == Float.class;
                case "char" -> actual == Character.class;
                case "byte" -> actual == Byte.class;
                case "short" -> actual == Short.class;
                default -> false;
            };
        }
        return target.isAssignableFrom(actual);
    }

    private static BakedModel wrapModel(IToolStackView tool, BakedModel originalModel){
        if(tool != null){
            for(Item predicate : TicEXRegistry.CUSTOM_MODELS.keySet()){
                if(ForgeRegistries.ITEMS.getKey(tool.getItem()).equals(ForgeRegistries.ITEMS.getKey(predicate))){
                    return TicEXRegistry.CUSTOM_MODELS.get(predicate).apply(originalModel);
                }
            }
        }
        return originalModel;
    }

    private static class BakedLargeToolModel extends BakedModelWrapper<BakedModel> {
    private final BakedModel small;
    private final BakedModel gui;
    public BakedLargeToolModel(BakedModel large, BakedModel small, BakedModel gui) {
      super(large);
      this.small = small;
      this.gui = gui;
    }

    @Override
    public BakedModel applyTransform(ItemDisplayContext cameraTransformType, PoseStack mat, boolean applyLeftHandTransform) {
      BakedModel model = originalModel;
      if (cameraTransformType == ItemDisplayContext.GUI) {
        model = gui;
      } else if (SMALL_TOOL_TYPES.get(cameraTransformType.ordinal())) {
        model = small;
      }
      return model.applyTransform(cameraTransformType, mat, applyLeftHandTransform);
    }
  }
}
