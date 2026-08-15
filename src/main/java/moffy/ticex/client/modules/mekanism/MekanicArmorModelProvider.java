package moffy.ticex.client.modules.mekanism;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMaps;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import mekanism.api.gear.IModule;
import mekanism.api.gear.IModuleHelper;
import mekanism.api.gear.ModuleData;
import mekanism.api.providers.IModuleDataProvider;
import mekanism.client.render.MekanismRenderType;
import mekanism.client.render.armor.MekaSuitArmor;
import mekanism.client.render.lib.QuickHash;
import mekanism.client.render.lib.effect.BoltRenderer;
import mekanism.common.content.gear.shared.ModuleColorModulationUnit;
import mekanism.common.item.gear.ItemMekaTool;
import mekanism.common.lib.Color;
import mekanism.common.lib.effect.BoltEffect;
import mekanism.common.registries.MekanismModules;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.MekanismUtils;
import moffy.ticex.TicEXConfig;
import moffy.ticex.client.providers.ExtraArmorModelProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.client.armor.ArmorModelManager;
import slimeknights.tconstruct.library.tools.item.IModifiable;

import java.util.*;
import java.util.function.Predicate;

public class MekanicArmorModelProvider extends ExtraArmorModelProvider {

    private EquipmentSlot type;
    private HumanoidModel<?> base;
    private LivingEntity living;
    private ItemStack stack;

    @Override
    public void providerSetup(@NotNull LivingEntity living, @NotNull ItemStack stack, @NotNull EquipmentSlot slot, @NotNull HumanoidModel<?> base, ArmorModelManager.@NotNull ArmorModel model) {
        this.type = slot;
        this.living = living;
        this.stack = stack;
        this.base = base;

        MekanicArmorQuadCache.createQuadCache(this.type);
    }

    @Override
    public void renderExtraModel(@NotNull PoseStack matrices, @NotNull VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.base != null && TicEXConfig.USE_ARMOR_MODEL != null && TicEXConfig.USE_ARMOR_MODEL.get()) {
            renderMekaSuit(
                    this.base,
                    matrices,
                    Minecraft.getInstance().renderBuffers().bufferSource(),
                    packedLightIn,
                    packedOverlayIn,
                    getColor(stack),
                    0.05F + 0.5F * Mth.sin(living.tickCount * (float) Math.PI),
                    stack.hasFoil(),
                    living
            );
        }
    }

    private void renderMekaSuit(
            HumanoidModel<? extends LivingEntity> baseModel,
            @NotNull PoseStack matrix,
            @NotNull MultiBufferSource renderer,
            int light,
            int overlayLight,
            Color color,
            float partialTicks,
            boolean hasEffect,
            LivingEntity entity
    ) {
        MekanicArmorQuadCache.ArmorQuads armorQuads = MekanicArmorQuadCache.getOrCreateCache(this.type).getUnchecked(key(entity));
        matrix.pushPose();
        render(
                baseModel,
                renderer,
                matrix,
                light,
                overlayLight,
                color,
                hasEffect,
                entity,
                armorQuads.opaqueQuads(),
                false
        );
        matrix.popPose();

        if (type == EquipmentSlot.CHEST) {
            BoltRenderer boltRenderer = MekanicArmorQuadCache.boltRenderMap.computeIfAbsent(entity.getUUID(), id -> new BoltRenderer());
            if (
                    IModuleHelper.INSTANCE.isEnabled(
                            entity.getItemBySlot(EquipmentSlot.CHEST),
                            MekanismModules.GRAVITATIONAL_MODULATING_UNIT
                    )
            ) {
                BoltEffect leftBolt = new BoltEffect(
                        BoltEffect.BoltRenderInfo.ELECTRICITY,
                        new Vec3(-0.01, 0.35, 0.37),
                        new Vec3(-0.01, 0.15, 0.37),
                        10
                )
                        .size(0.012F)
                        .lifespan(6)
                        .spawn(BoltEffect.SpawnFunction.noise(3, 1));
                BoltEffect rightBolt = new BoltEffect(
                        BoltEffect.BoltRenderInfo.ELECTRICITY,
                        new Vec3(0.025, 0.35, 0.37),
                        new Vec3(0.025, 0.15, 0.37),
                        10
                )
                        .size(0.012F)
                        .lifespan(6)
                        .spawn(BoltEffect.SpawnFunction.noise(3, 1));
                boltRenderer.update(0, leftBolt, partialTicks);
                boltRenderer.update(1, rightBolt, partialTicks);
            }

            matrix.pushPose();
            MekaSuitArmor.ModelPos.BODY.translate(baseModel, matrix, entity);
            boltRenderer.render(partialTicks, matrix, Minecraft.getInstance().renderBuffers().bufferSource());
            matrix.popPose();
        }
    }

    private void render(
            HumanoidModel<? extends LivingEntity> baseModel,
            MultiBufferSource renderer,
            PoseStack matrix,
            int light,
            int overlayLight,
            Color color,
            boolean hasEffect,
            LivingEntity entity,
            Map<MekaSuitArmor.ModelPos, List<BakedQuad>> quadMap,
            boolean transparent
    ) {
        if (!quadMap.isEmpty()) {
            RenderType renderType = transparent
                    ? RenderType.entityTranslucent(InventoryMenu.BLOCK_ATLAS)
                    : MekanismRenderType.MEKASUIT;
            VertexConsumer builder = ItemRenderer.getFoilBufferDirect(renderer, renderType, false, hasEffect);
            for (Map.Entry<MekaSuitArmor.ModelPos, List<BakedQuad>> entry : quadMap.entrySet()) {
                matrix.pushPose();
                entry.getKey().translate(baseModel, matrix, entity);
                putQuads(entry.getValue(), builder, matrix.last(), light, overlayLight, color);
                matrix.popPose();
            }
        }
    }

    private void putQuads(
            List<BakedQuad> quads,
            VertexConsumer builder,
            PoseStack.Pose pose,
            int light,
            int overlayLight,
            Color color
    ) {
        for (BakedQuad quad : quads) {
            builder.putBulkData(pose, quad, color.rf(), color.gf(), color.bf(), color.af(), light, overlayLight, false);
        }
    }

    public QuickHash key(LivingEntity player) {
        Object2BooleanMap<MekanicArmorQuadCache.ModuleModelSpec> modules = new Object2BooleanOpenHashMap<>();
        Set<EquipmentSlot> wornParts = EnumSet.noneOf(EquipmentSlot.class);
        IModuleHelper moduleHelper = IModuleHelper.INSTANCE;
        for (EquipmentSlot slotType : EnumUtils.ARMOR_SLOTS) {
            ItemStack wornItem = player.getItemBySlot(slotType);
            if (!wornItem.isEmpty() && wornItem.getItem() instanceof IModifiable) {
                wornParts.add(slotType);
                for (Map.Entry<ModuleData<?>, MekanicArmorQuadCache.ModuleModelSpec> entry : MekanicArmorQuadCache.moduleModelSpec.row(slotType).entrySet()) {
                    if (moduleHelper.isEnabled(wornItem, entry.getKey())) {
                        MekanicArmorQuadCache.ModuleModelSpec spec = entry.getValue();
                        modules.put(spec, spec.isActive(player));
                    }
                }
            }
        }
        return new QuickHash(
                modules.isEmpty() ? Object2BooleanMaps.emptyMap() : modules,
                wornParts.isEmpty() ? Collections.emptySet() : wornParts,
                MekanismUtils.getItemInHand(player, HumanoidArm.LEFT).getItem() instanceof ItemMekaTool,
                MekanismUtils.getItemInHand(player, HumanoidArm.RIGHT).getItem() instanceof ItemMekaTool
        );
    }

    private static Color getColor(ItemStack stack) {
        if (!stack.isEmpty()) {
            IModule<ModuleColorModulationUnit> colorModulation = IModuleHelper.INSTANCE.load(
                    stack,
                    MekanismModules.COLOR_MODULATION_UNIT
            );
            if (colorModulation != null) {
                return colorModulation.getCustomInstance().getColor();
            }
        }

        return Color.WHITE;
    }
}
