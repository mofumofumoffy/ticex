package moffy.ticex.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Transformation;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMaps;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mekanism.api.gear.IModule;
import mekanism.api.gear.IModuleHelper;
import mekanism.api.gear.ModuleData;
import mekanism.api.providers.IModuleDataProvider;
import mekanism.client.model.BaseModelCache.MekanismModelData;
import mekanism.client.model.BaseModelCache.OBJModelData;
import mekanism.client.model.MekanismModelCache;
import mekanism.client.render.MekanismRenderType;
import mekanism.client.render.armor.MekaSuitArmor.ModelPos;
import mekanism.client.render.lib.QuadTransformation;
import mekanism.client.render.lib.QuadUtils;
import mekanism.client.render.lib.QuickHash;
import mekanism.client.render.lib.effect.BoltRenderer;
import mekanism.common.Mekanism;
import mekanism.common.content.gear.shared.ModuleColorModulationUnit;
import mekanism.common.item.gear.ItemMekaTool;
import mekanism.common.lib.Color;
import mekanism.common.lib.effect.BoltEffect;
import mekanism.common.lib.effect.BoltEffect.BoltRenderInfo;
import mekanism.common.lib.effect.BoltEffect.SpawnFunction;
import mekanism.common.registries.MekanismModules;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.MekanismUtils;
import moffy.ticex.item.modifiable.ItemModifiableMekaSuitArmor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ModelEvent.BakingCompleted;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import slimeknights.tconstruct.library.client.armor.MultilayerArmorModel;
import slimeknights.tconstruct.library.client.armor.ArmorModelManager.ArmorModel;

public final class MekaPlateMultilayerModel extends MultilayerArmorModel{

    public static MekaPlateMultilayerModel HEAD = new MekaPlateMultilayerModel(EquipmentSlot.HEAD, EquipmentSlot.CHEST);
    public static MekaPlateMultilayerModel CHESTPLATE = new MekaPlateMultilayerModel(EquipmentSlot.CHEST, EquipmentSlot.HEAD);
    public static MekaPlateMultilayerModel LEGGINGS = new MekaPlateMultilayerModel(EquipmentSlot.LEGS, EquipmentSlot.FEET);
    public static MekaPlateMultilayerModel BOOTS = new MekaPlateMultilayerModel(EquipmentSlot.FEET, EquipmentSlot.LEGS);
    
    private EquipmentSlot type;
    private EquipmentSlot adjacentType;

    private LivingEntity living;
    private ItemStack stack;

    private static final String LED_TAG = "led";
    private static final String INACTIVE_TAG = "inactive_";
    private static final String OVERRIDDEN_TAG = "override_";
    private static final String EXCLUSIVE_TAG = "excl_";
    private static final String SHARED_TAG = "shared_";
    private static final String GLASS_TAG = "glass";

    private static final Table<EquipmentSlot, ModuleData<?>, ModuleModelSpec> moduleModelSpec = HashBasedTable.create();

    private static final Map<UUID, BoltRenderer> boltRenderMap = new Object2ObjectOpenHashMap<>();

    private final LoadingCache<QuickHash, ArmorQuads> cache = CacheBuilder.newBuilder().build(new CacheLoader<>() {
        @NotNull
        @Override
        @SuppressWarnings("unchecked")
        public ArmorQuads load(@NotNull QuickHash key) {
            return createQuads((Object2BooleanMap<ModuleModelSpec>) key.objs()[0], (Set<EquipmentSlot>) key.objs()[1], (boolean) key.objs()[2], (boolean) key.objs()[3]);
        }
    });

    private static Color getColor(ItemStack stack) {
      if (!stack.isEmpty()) {
         IModule<ModuleColorModulationUnit> colorModulation = IModuleHelper.INSTANCE.load(stack, MekanismModules.COLOR_MODULATION_UNIT);
         if (colorModulation != null) {
            return ((ModuleColorModulationUnit)colorModulation.getCustomInstance()).getColor();
         }
      }

      return Color.WHITE;
   }

    public MekaPlateMultilayerModel(EquipmentSlot type, EquipmentSlot adjacentType){
        this.type = type;
        this.adjacentType = adjacentType;
        MekaPlateModelCache.INSTANCE.reloadCallback(cache::invalidateAll);
    }

    @Override
    public Model setup(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> base,
            ArmorModel model) {
        this.living = living;
        this.stack = stack;
        return super.setup(living, stack, slot, base, model);
    }


    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        super.renderToBuffer(matrices, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        if (this.base != null) {
            renderMekaSuit(this.base, matrices, Minecraft.getInstance().renderBuffers().bufferSource(), packedLightIn, packedOverlayIn, getColor(stack), 0.05F + 0.5F * Mth.sin(living.tickCount * (float)Math.PI), stack.hasFoil(), living);
        }
    }

    private void renderMekaSuit(HumanoidModel<? extends LivingEntity> baseModel, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer,
          int light, int overlayLight, Color color, float partialTicks, boolean hasEffect, LivingEntity entity) {
        ArmorQuads armorQuads = cache.getUnchecked(key(entity));
        render(baseModel, renderer, matrix, light, overlayLight, color, hasEffect, entity, armorQuads.opaqueQuads(), false);

        if (type == EquipmentSlot.CHEST) {
            BoltRenderer boltRenderer = boltRenderMap.computeIfAbsent(entity.getUUID(), id -> new BoltRenderer());
            if (IModuleHelper.INSTANCE.isEnabled(entity.getItemBySlot(EquipmentSlot.CHEST), MekanismModules.GRAVITATIONAL_MODULATING_UNIT)) {
                BoltEffect leftBolt = new BoltEffect(BoltRenderInfo.ELECTRICITY, new Vec3(-0.01, 0.35, 0.37), new Vec3(-0.01, 0.15, 0.37), 10)
                      .size(0.012F).lifespan(6).spawn(SpawnFunction.noise(3, 1));
                BoltEffect rightBolt = new BoltEffect(BoltRenderInfo.ELECTRICITY, new Vec3(0.025, 0.35, 0.37), new Vec3(0.025, 0.15, 0.37), 10)
                      .size(0.012F).lifespan(6).spawn(SpawnFunction.noise(3, 1));
                boltRenderer.update(0, leftBolt, partialTicks);
                boltRenderer.update(1, rightBolt, partialTicks);
            }
            
            matrix.pushPose();
            ModelPos.BODY.translate(baseModel, matrix, entity);
            boltRenderer.render(partialTicks, matrix, Minecraft.getInstance().renderBuffers().bufferSource());
            matrix.popPose();
        }

        
        render(baseModel, renderer, matrix, light, overlayLight, Color.WHITE, hasEffect, entity, armorQuads.transparentQuads(), true);
    }

    private void render(HumanoidModel<? extends LivingEntity> baseModel, MultiBufferSource renderer, PoseStack matrix, int light, int overlayLight,
          Color color, boolean hasEffect, LivingEntity entity, Map<ModelPos, List<BakedQuad>> quadMap, boolean transparent) {
        if (!quadMap.isEmpty()) {
            RenderType renderType = transparent ? RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS) : MekanismRenderType.MEKASUIT;
            VertexConsumer builder = ItemRenderer.getFoilBufferDirect(renderer, renderType, false, hasEffect);
            for (Map.Entry<ModelPos, List<BakedQuad>> entry : quadMap.entrySet()) {
                matrix.pushPose();
                entry.getKey().translate(baseModel, matrix, entity);
                putQuads(entry.getValue(), builder, matrix.last(), light, overlayLight, color);
                matrix.popPose();
            }
        }
    }

    private void putQuads(List<BakedQuad> quads, VertexConsumer builder, PoseStack.Pose pose, int light, int overlayLight, Color color) {
        for (BakedQuad quad : quads) {
            builder.putBulkData(pose, quad, color.rf(), color.gf(), color.bf(), color.af(), light, overlayLight, false);
        }
    }

    public QuickHash key(LivingEntity player) {
        Object2BooleanMap<ModuleModelSpec> modules = new Object2BooleanOpenHashMap<>();
        Set<EquipmentSlot> wornParts = EnumSet.noneOf(EquipmentSlot.class);
        IModuleHelper moduleHelper = IModuleHelper.INSTANCE;
        for (EquipmentSlot slotType : EnumUtils.ARMOR_SLOTS) {
            ItemStack wornItem = player.getItemBySlot(slotType);
            if (!wornItem.isEmpty() && wornItem.getItem() instanceof ItemModifiableMekaSuitArmor) {
                wornParts.add(slotType);
                for (Map.Entry<ModuleData<?>, ModuleModelSpec> entry : moduleModelSpec.row(slotType).entrySet()) {
                    if (moduleHelper.isEnabled(wornItem, entry.getKey())) {
                        ModuleModelSpec spec = entry.getValue();
                        modules.put(spec, spec.isActive(player));
                    }
                }
            }
        }
        return new QuickHash(modules.isEmpty() ? Object2BooleanMaps.emptyMap() : modules, wornParts.isEmpty() ? Collections.emptySet() : wornParts,
              MekanismUtils.getItemInHand(player, HumanoidArm.LEFT).getItem() instanceof ItemMekaTool,
              MekanismUtils.getItemInHand(player, HumanoidArm.RIGHT).getItem() instanceof ItemMekaTool);
    }

    private record ArmorQuads(Map<ModelPos, List<BakedQuad>> opaqueQuads, Map<ModelPos, List<BakedQuad>> transparentQuads) {

        public ArmorQuads {
            if (opaqueQuads.isEmpty()) {
                opaqueQuads = Collections.emptyMap();
            }
            if (transparentQuads.isEmpty()) {
                transparentQuads = Collections.emptyMap();
            }
        }
    }

    private record ModuleModelSpec(ModuleData<?> module, EquipmentSlot slotType, String name, Predicate<LivingEntity> isActive) {

        /**
         * Score closest to zero is considered best, negative one for no match at all.
         */
        public int score(String name) {
            return name.indexOf(this.name + "_");
        }

        public boolean isActive(LivingEntity entity) {
            return isActive.test(entity);
        }

        public String processOverrideName(String part) {
            return MekaPlateMultilayerModel.processOverrideName(part, name);
        }
    }

    public static void registerModule(String name, IModuleDataProvider<?> moduleDataProvider, EquipmentSlot slotType, Predicate<LivingEntity> isActive) {
        ModuleData<?> module = moduleDataProvider.getModuleData();
        moduleModelSpec.put(slotType, module, new ModuleModelSpec(module, slotType, name, isActive));
    }

    private record OverrideData(MekanismModelData modelData, String name) {
    }

    private ArmorQuads createQuads(Object2BooleanMap<ModuleModelSpec> modules, Set<EquipmentSlot> wornParts, boolean hasMekaToolLeft, boolean hasMekaToolRight) {
        Map<MekanismModelData, Map<ModelPos, Set<String>>> specialQuadsToRender = new Object2ObjectOpenHashMap<>();
        Map<MekanismModelData, Map<ModelPos, Set<String>>> specialLEDQuadsToRender = new Object2ObjectOpenHashMap<>();
        
        Map<String, OverrideData> overrides = new Object2ObjectOpenHashMap<>();
        Set<String> ignored = new HashSet<>();

        if (!modules.isEmpty()) {
            Map<MekanismModelData, Set<String>> allMatchedParts = new Object2ObjectOpenHashMap<>();
            for (ModuleOBJModelData modelData : MekaPlateModelCache.INSTANCE.MEKASUIT_MODULES) {
                Set<String> matchedParts = allMatchedParts.computeIfAbsent(modelData, d -> new HashSet<>());
                for (Object2BooleanMap.Entry<ModuleModelSpec> entry : modules.object2BooleanEntrySet()) {
                    ModuleModelSpec spec = entry.getKey();
                    for (String name : modelData.getPartsForSpec(spec, entry.getBooleanValue())) {
                        if (name.contains(OVERRIDDEN_TAG)) {
                            overrides.put(spec.processOverrideName(name), new OverrideData(modelData, name));
                        }
                        
                        if (type == spec.slotType) {
                            
                            
                            matchedParts.add(name);
                        }
                    }
                }
            }
            for (Map.Entry<MekanismModelData, Set<String>> entry : allMatchedParts.entrySet()) {
                Set<String> matchedParts = entry.getValue();
                if (!matchedParts.isEmpty()) {
                    MekanismModelData modelData = entry.getKey();
                    Map<ModelPos, Set<String>> quadsToRender = specialQuadsToRender.computeIfAbsent(modelData, d -> new EnumMap<>(ModelPos.class));
                    Map<ModelPos, Set<String>> ledQuadsToRender = specialLEDQuadsToRender.computeIfAbsent(modelData, d -> new EnumMap<>(ModelPos.class));
                    
                    for (String name : matchedParts) {
                        ModelPos pos = ModelPos.get(name);
                        if (pos == null) {
                            Mekanism.logger.warn("MekaSuit part '{}' is invalid from modules model. Ignoring.", name);
                        } else {
                            
                            
                            
                            addQuadsToRender(pos, name, overrides, quadsToRender, ledQuadsToRender, specialQuadsToRender, specialLEDQuadsToRender);
                        }
                    }
                }
            }
        }

        
        if (type == EquipmentSlot.CHEST) {
            if (hasMekaToolLeft) {
                processMekaTool(MekanismModelCache.INSTANCE.MEKATOOL_LEFT_HAND, ignored);
            }
            if (hasMekaToolRight) {
                processMekaTool(MekanismModelCache.INSTANCE.MEKATOOL_RIGHT_HAND, ignored);
            }
        }

        Map<ModelPos, Set<String>> armorQuadsToRender = new EnumMap<>(ModelPos.class);
        Map<ModelPos, Set<String>> armorLEDQuadsToRender = new EnumMap<>(ModelPos.class);
        for (String name : MekaPlateModelCache.INSTANCE.MEKASUIT_EXO.getModel().getRootComponentNames()) {
            if (!checkEquipment(type, name)) {
                
                continue;
            } else if (name.startsWith(EXCLUSIVE_TAG)) {
                if (wornParts.contains(adjacentType)) {
                    
                    continue;
                }
            } else if (name.startsWith(SHARED_TAG) && wornParts.contains(adjacentType) && adjacentType.ordinal() > type.ordinal()) {
                
                continue;
            }
            ModelPos pos = ModelPos.get(name);
            if (pos == null) {
                Mekanism.logger.warn("MekaSuit part '{}' is invalid. Ignoring.", name);
            } else if (!ignored.contains(name)) {
                addQuadsToRender(pos, name, overrides, armorQuadsToRender, armorLEDQuadsToRender, specialQuadsToRender, specialLEDQuadsToRender);
            }
        }

        Map<ModelPos, List<BakedQuad>> opaqueMap = new EnumMap<>(ModelPos.class);
        Map<ModelPos, List<BakedQuad>> transparentMap = new EnumMap<>(ModelPos.class);
        for (ModelPos pos : ModelPos.VALUES) {
            for (MekanismModelData modelData : MekaPlateModelCache.INSTANCE.MEKASUIT_MODULES) {
                parseTransparency(modelData, pos, opaqueMap, transparentMap, specialQuadsToRender.getOrDefault(modelData, Collections.emptyMap()),
                      specialLEDQuadsToRender.getOrDefault(modelData, Collections.emptyMap()));
            }
            parseTransparency(MekaPlateModelCache.INSTANCE.MEKASUIT_EXO, pos, opaqueMap, transparentMap, armorQuadsToRender, armorLEDQuadsToRender);
        }
        return new ArmorQuads(opaqueMap, transparentMap);
    }

    private static void processMekaTool(OBJModelData mekaToolModel, Set<String> ignored) {
        for (String name : mekaToolModel.getModel().getRootComponentNames()) {
            if (name.contains(OVERRIDDEN_TAG)) {
                
                ignored.add(processOverrideName(name, "mekatool"));
            }
        }
    }

    private static boolean checkEquipment(EquipmentSlot type, String text) {
        return switch (type) {
            case HEAD -> text.contains("helmet");
            case CHEST -> text.contains("chest");
            case LEGS -> text.contains("leggings");
            case FEET -> text.contains("boots");
            default -> false;
        };
    }

    private static void addQuadsToRender(ModelPos pos, String name, Map<String, OverrideData> overrides, Map<ModelPos, Set<String>> quadsToRender,
          Map<ModelPos, Set<String>> ledQuadsToRender, Map<MekanismModelData, Map<ModelPos, Set<String>>> specialQuadsToRender,
          Map<MekanismModelData, Map<ModelPos, Set<String>>> specialLEDQuadsToRender) {
        OverrideData override = overrides.get(name);
        if (override != null) {
            
            name = override.name();
            
            
            MekanismModelData overrideData = override.modelData();
            quadsToRender = specialQuadsToRender.computeIfAbsent(overrideData, d -> new EnumMap<>(ModelPos.class));
            ledQuadsToRender = specialLEDQuadsToRender.computeIfAbsent(overrideData, d -> new EnumMap<>(ModelPos.class));
        }
        if (name.contains(LED_TAG)) {
            ledQuadsToRender.computeIfAbsent(pos, p -> new HashSet<>()).add(name);
        } else {
            quadsToRender.computeIfAbsent(pos, p -> new HashSet<>()).add(name);
        }
    }

    private static void parseTransparency(MekanismModelData modelData, ModelPos pos, Map<ModelPos, List<BakedQuad>> opaqueMap, Map<ModelPos, List<BakedQuad>> transparentMap,
          Map<ModelPos, Set<String>> regularQuads, Map<ModelPos, Set<String>> ledQuads) {
        Set<String> opaqueRegularQuads = new HashSet<>(), opaqueLEDQuads = new HashSet<>();
        Set<String> transparentRegularQuads = new HashSet<>(), transparentLEDQuads = new HashSet<>();
        parseTransparency(pos, opaqueRegularQuads, transparentRegularQuads, regularQuads);
        parseTransparency(pos, opaqueLEDQuads, transparentLEDQuads, ledQuads);
        addParsedQuads(modelData, pos, opaqueMap, opaqueRegularQuads, opaqueLEDQuads);
        addParsedQuads(modelData, pos, transparentMap, transparentRegularQuads, transparentLEDQuads);
    }

    private static void addParsedQuads(MekanismModelData modelData, ModelPos pos, Map<ModelPos, List<BakedQuad>> map, Set<String> quads, Set<String> ledQuads) {
        
        List<BakedQuad> bakedQuads = getQuads(modelData, quads, ledQuads, pos.getTransform());
        if (!bakedQuads.isEmpty()) {
            map.computeIfAbsent(pos, p -> new ArrayList<>()).addAll(bakedQuads);
        }
    }

    private static void parseTransparency(ModelPos pos, Set<String> opaqueQuads, Set<String> transparentQuads, Map<ModelPos, Set<String>> quads) {
        for (String quad : quads.getOrDefault(pos, Collections.emptySet())) {
            if (quad.contains(GLASS_TAG)) {
                transparentQuads.add(quad);
            } else {
                opaqueQuads.add(quad);
            }
        }
    }

    private static List<BakedQuad> getQuads(MekanismModelData data, Set<String> parts, Set<String> ledParts, @Nullable QuadTransformation transform) {
        RandomSource random = Minecraft.getInstance().level.getRandom();
        List<BakedQuad> quads = new ArrayList<>();
        
        
        if (!parts.isEmpty()) {
            quads.addAll(data.bake(new MekaSuitModelConfiguration(parts)).getQuads(null, null, random, ModelData.EMPTY, null));
        }
        if (!ledParts.isEmpty()) {
            List<BakedQuad> ledQuads = data.bake(new MekaSuitModelConfiguration(ledParts)).getQuads(null, null, random, ModelData.EMPTY, null);
            quads.addAll(QuadUtils.transformBakedQuads(ledQuads, QuadTransformation.fullbright));
        }
        if (transform != null) {
            quads = QuadUtils.transformBakedQuads(quads, transform);
        }
        return quads;
    }

    private static String processOverrideName(String part, String name) {
        return part.replaceFirst(OVERRIDDEN_TAG, "").replaceFirst(name + "_", "");
    }

    public static class ModuleOBJModelData extends OBJModelData {

        private record SpecData(Set<String> active, Set<String> inactive) {
        }

        private final Map<ModuleModelSpec, SpecData> specParts = new Object2ObjectOpenHashMap<>();

        public ModuleOBJModelData(ResourceLocation rl) {
            super(rl);
        }

        public Set<String> getPartsForSpec(ModuleModelSpec spec, boolean active) {
            SpecData specData = specParts.get(spec);
            if (specData == null) {
                return Collections.emptySet();
            }
            return active ? specData.active() : specData.inactive();
        }

        @Override
        protected void reload(BakingCompleted evt) {
            super.reload(evt);
            Collection<ModuleModelSpec> modules = moduleModelSpec.values();
            for (String name : getModel().getRootComponentNames()) {
                
                
                
                ModuleModelSpec matchingSpec = null;
                int bestScore = -1;
                for (ModuleModelSpec spec : modules) {
                    int score = spec.score(name);
                    if (score != -1 && (bestScore == -1 || score < bestScore)) {
                        bestScore = score;
                        matchingSpec = spec;
                    }
                }
                if (matchingSpec != null) {
                    SpecData specData = specParts.computeIfAbsent(matchingSpec, spec -> new SpecData(new HashSet<>(), new HashSet<>()));
                    if (name.contains(INACTIVE_TAG + matchingSpec.name + "_")) {
                        specData.inactive().add(name);
                    } else {
                        specData.active().add(name);
                    }
                }
            }
            
            for (Map.Entry<ModuleModelSpec, SpecData> entry : specParts.entrySet()) {
                SpecData specData = entry.getValue();
                if (specData.active().isEmpty()) {
                    entry.setValue(new SpecData(Collections.emptySet(), specData.inactive()));
                } else if (specData.inactive().isEmpty()) {
                    entry.setValue(new SpecData(specData.active(), Collections.emptySet()));
                }
            }
        }
    }

    private record MekaSuitModelConfiguration(Set<String> parts) implements IGeometryBakingContext {

        @SuppressWarnings("deprecation")
        private static final Material NO_MATERIAL = new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation());

        private MekaSuitModelConfiguration {
            parts = parts.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet(parts);
        }

        @NotNull
        @Override
        public String getModelName() {
            return "mekanism:mekasuit";
        }

        @Override
        public boolean hasMaterial(@NotNull String name) {
            return false;
        }

        @NotNull
        @Override
        public Material getMaterial(@NotNull String name) {
            return NO_MATERIAL;
        }

        @Override
        public boolean isGui3d() {
            return false;
        }

        @Override
        public boolean useBlockLight() {
            return false;
        }

        @Override
        public boolean useAmbientOcclusion() {
            return true;
        }

        @NotNull
        @Override
        @Deprecated
        public ItemTransforms getTransforms() {
            return ItemTransforms.NO_TRANSFORMS;
        }

        @NotNull
        @Override
        public Transformation getRootTransform() {
            return Transformation.identity();
        }

        @Nullable
        @Override
        public ResourceLocation getRenderTypeHint() {
            return null;
        }

        @Override
        public boolean isComponentVisible(String component, boolean fallback) {
            
            return parts.contains(component);
        }
    }
}
