package moffy.ticex.lib.utils;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Axis;

import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.renderer.model.obj.Face;
import mods.flammpfeil.slashblade.client.renderer.model.obj.GroupObject;
import mods.flammpfeil.slashblade.client.renderer.model.obj.Vertex;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import mods.flammpfeil.slashblade.init.DefaultResources;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.SwordType;
import moffy.ticex.TicEX;
import moffy.ticex.TicEXConfig;
import moffy.ticex.client.modules.slashblade.SBToolBladeItemRenderer;
import moffy.ticex.client.modules.slashblade.SBToolRenderType.PartType;
import moffy.ticex.client.rendering.ItemRenderContext;
import moffy.ticex.client.rendering.shader.ShaderProvider;
import moffy.ticex.client.rendering.ticex.TicEXToolRenders;
import moffy.ticex.item.modifiable.ModifiableSlashBladeItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import java.util.*;
import java.util.function.Supplier;

public class TicEXSBUtils {

    public static Set<Enchantment> disallowedEnchantments = new HashSet<>();

    static {
        disallowedEnchantments.add(Enchantments.UNBREAKING);
        disallowedEnchantments.add(Enchantments.MENDING);
        disallowedEnchantments.add(Enchantments.SHARPNESS);
        disallowedEnchantments.add(Enchantments.BANE_OF_ARTHROPODS);
        disallowedEnchantments.add(Enchantments.SMITE);
        disallowedEnchantments.add(Enchantments.FIRE_ASPECT);
        disallowedEnchantments.add(Enchantments.KNOCKBACK);
        disallowedEnchantments.add(Enchantments.MOB_LOOTING);
    }

    public static Supplier<Matrix4f> defaultTransform = Suppliers.memoize(() -> {
        Matrix4f m = new Matrix4f();
        m.identity();
        return m;
    });

    private static final Map<ShaderProvider.Tool, RenderType> renderTypeCache = new HashMap<>();

    @OnlyIn(Dist.CLIENT)
    public static void tessellateWithShader(
            ItemStack stack,
            ItemRenderContext itemRenderContext,
            ShaderProvider.Tool shaderProvider,
            MaterialVariantId material,
            WavefrontObject wavefrontObject,
            VertexConsumer tessellator,
            MultiBufferSource bufferSource,
            PartType partType,
            String... groupNames
    ) {
        boolean needShader = shaderProvider != null && TicEXConfig.USE_SHADER.get();

        Material atlasMaterial = new Material(
            InventoryMenu.BLOCK_ATLAS,
            new ResourceLocation(TicEX.MODID, "obj_tool/slashblade_tool/" + partType.getName())
        );
        TextureAtlasSprite sprite = atlasMaterial.sprite();
        VertexConsumer vertexConsumer = null;


        if (needShader) {
            shaderProvider.beginRender(stack, itemRenderContext);
            shaderProvider.beginRenderMaterial(stack, material.getId());
            shaderProvider.preRenderMaterial(stack, material.getId());

            // fake batch
            shaderProvider.startRenderBatch(itemRenderContext, TicEXToolRenders.RenderPhase.OVERLAY_MATERIAL);

            vertexConsumer = bufferSource.getBuffer(getBladeRenderType(shaderProvider));
        }

        for (GroupObject groupObject : wavefrontObject.groupObjects) {
            for (String groupName : groupNames) {
                if (!groupName.equalsIgnoreCase(groupObject.name)) {
                    continue;
                }
                if (needShader) {
                    renderWithShader(
                            groupObject,
                            vertexConsumer,
                            sprite
                    );
                } else {
                    groupObject.render(tessellator);
                }
            }
        }

        if (needShader) {
            shaderProvider.endRenderBatch(itemRenderContext, TicEXToolRenders.RenderPhase.OVERLAY_MATERIAL);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderWithShader(
        GroupObject groupObject,
        VertexConsumer tessellator,
        TextureAtlasSprite sprite
    ) {
        if (groupObject.faces.isEmpty()) {
            return;
        }

        for (Face face : groupObject.faces) {
            addFaceForRender(face, tessellator, sprite);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void addFaceForRender(Face face, VertexConsumer tessellator, TextureAtlasSprite sprite) {
        final float textureOffset = 5.0E-4F;

        if (face.faceNormal == null) {
            face.faceNormal = face.calculateFaceNormal();
        }

        float averageU = 0.0F;
        float averageV = 0.0F;
        if (face.textureCoordinates != null && face.textureCoordinates.length > 0) {
            for (int i = 0; i < face.textureCoordinates.length; ++i) {
                averageU += face.textureCoordinates[i].u * Face.uvOperator.x() + Face.uvOperator.z();
                averageV += face.textureCoordinates[i].v * Face.uvOperator.y() + Face.uvOperator.w();
            }

            averageU /= (float) face.textureCoordinates.length;
            averageV /= (float) face.textureCoordinates.length;
        }

        Matrix4f transform;
        if (Face.matrix != null) {
            PoseStack.Pose me = Face.matrix.last();
            transform = me.pose();
        } else {
            transform = defaultTransform.get();
        }

        for (int i = 0; i < face.vertices.length; ++i) {
            putVertex(face, tessellator, i, transform, textureOffset, sprite, averageU, averageV);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void putVertex(
        Face face,
        VertexConsumer wr,
        int i,
        Matrix4f transform,
        float textureOffset,
        TextureAtlasSprite sprite,
        float averageU,
        float averageV
    ) {
        wr.vertex(transform, face.vertices[i].x, face.vertices[i].y, face.vertices[i].z);
        wr.color(
            1.0f,
            1.0f,
            1.0f,
                Face.alphaOverride.apply(
                        new Vector4f(face.vertices[i].x, face.vertices[i].y, face.vertices[i].z, 1.0F),
                        Face.col.getAlpha()
                )
        );
        if (face.textureCoordinates != null && face.textureCoordinates.length > 0) {
            float offsetU = textureOffset;
            float offsetV = textureOffset;
            float textureU = face.textureCoordinates[i].u * Face.uvOperator.x() + Face.uvOperator.z();
            float textureV = face.textureCoordinates[i].v * Face.uvOperator.y() + Face.uvOperator.w();
            if (textureU > averageU) {
                offsetU = -textureOffset;
            }

            if (textureV > averageV) {
                offsetV = -textureOffset;
            }

            wr.uv(
                sprite.getU0() + (textureU + offsetU) * (sprite.getU1() - sprite.getU0()),
                sprite.getV0() + (textureV + offsetV) * (sprite.getV1() - sprite.getV0())
            );
        } else {
            wr.uv(0.0F, 0.0F);
        }

        wr.overlayCoords(OverlayTexture.NO_OVERLAY);
        wr.uv2(Face.lightmap);
        Vector3f vector3f;
        if (Face.isSmoothShade && face.vertexNormals != null) {
            Vertex normal = face.vertexNormals[i];
            Vec3 nol = new Vec3(normal.x, normal.y, normal.z);
            vector3f = new Vector3f((float) nol.x, (float) nol.y, (float) nol.z);
        } else {
            vector3f = new Vector3f(face.faceNormal.x, face.faceNormal.y, face.faceNormal.z);
        }

        vector3f.mul(new Matrix3f(transform));
        vector3f.normalize();
        wr.normal(vector3f.x(), vector3f.y(), vector3f.z());
        wr.endVertex();
    }

    @OnlyIn(Dist.CLIENT)
    public static RenderType getBladeRenderType(ShaderProvider.Tool shaderProvider) {
        if (renderTypeCache.containsKey(shaderProvider)) {
            return renderTypeCache.get(shaderProvider);
        }

        RenderType.CompositeState state = CompositeState.builder()
                .setShaderState(new ShaderStateShard(shaderProvider::getShaderInstance))
            .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
            .setTextureState(new RenderStateShard.TextureStateShard(InventoryMenu.BLOCK_ATLAS, false, true))
            .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
            .setLightmapState(RenderType.LIGHTMAP)
            .setOverlayState(RenderType.OVERLAY)
            .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
            .createCompositeState(true);
        var renderType = RenderType.create(
                "slashblade_tool_shader_blend",
                DefaultVertexFormat.NEW_ENTITY,
                Mode.TRIANGLES,
                256,
                true,
                false,
                state
        );

        renderTypeCache.put(shaderProvider, renderType);
        return renderType;
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean renderBladeTool(ItemStack stack, float partialTicks, PoseStack matrixStackIn,
            MultiBufferSource bufferIn, int packedLightIn){
        Item itemIn = stack.getItem();
        if (itemIn instanceof ModifiableSlashBladeItem) {
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-135));
            try (MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStackIn)){
                EnumSet<SwordType> types = SwordType.from(stack);
                ResourceLocation modelLocation = stack.getCapability(ItemSlashBlade.BLADESTATE).map(state->{
                    return state.getModel().orElse(DefaultResources.resourceDefaultModel);
                }).orElse(DefaultResources.resourceDefaultModel);
                Optional<ResourceLocation> textureLoc = stack
                        .getCapability(ItemSlashBlade.BLADESTATE)
                        .map(state -> {
                            return state.getTexture().orElse(DefaultResources.resourceDefaultTexture);
                        });
                ResourceLocation textureLocation = textureLoc.orElse(DefaultResources.resourceDefaultTexture);
                WavefrontObject model = BladeModelManager.getInstance().getModel(modelLocation);
                float scale = 0.00625F;

                try (MSAutoCloser msac2 = MSAutoCloser.pushMatrix(matrixStackIn)){
                    float xOffset = 0.0F;
                    String renderTarget;
                    if (types.contains(SwordType.EDGEFRAGMENT)) {
                        xOffset = 200.0F;
                        renderTarget = "blade_fragment";
                    } else if (types.contains(SwordType.BROKEN)) {
                        xOffset = 30.0F;
                        renderTarget = "blade_damaged";
                    } else {
                        xOffset = 120.0F;
                        renderTarget = "blade";
                    }

                    matrixStackIn.scale(scale, scale, scale);
                    matrixStackIn.translate(xOffset, -1.25F, 0.0F);

                    ItemRenderContext itemRenderContext = new ItemRenderContext(
                        stack,
                        ItemDisplayContext.GROUND,
                        false,
                        matrixStackIn,
                        bufferIn,
                        packedLightIn,
                        OverlayTexture.NO_OVERLAY
                    );

                    SBToolBladeItemRenderer.renderToolSlashBlade(
                            stack,
                            itemRenderContext,
                            model,
                            textureLocation,
                            renderTarget,
                            matrixStackIn,
                            bufferIn,
                            packedLightIn
                    );
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean applyEnchantment(ItemStack toolStack, Enchantment enchantment, int level) {
        for (Enchantment disallowed : TicEXSBUtils.disallowedEnchantments) {
            if (!enchantment.getDescriptionId().equals(disallowed.getDescriptionId())) {
                if (toolStack.getEnchantmentLevel(enchantment) > 0) {
                    CompoundTag nbt = toolStack.getOrCreateTag();
                    if (!nbt.contains("Enchantments", Tag.TAG_LIST)) {
                        nbt.put("Enchantments", new ListTag());
                    }

                    ListTag listTag = nbt.getList("Enchantments", Tag.TAG_COMPOUND);
                    ListTag newListTag = new ListTag();
                    for (int i = 0; i < listTag.size(); i++) {
                        CompoundTag enchantmentTag = listTag.getCompound(i);
                        if (
                            enchantmentTag
                                .getString("id")
                                    .equals(Objects.requireNonNull(ForgeRegistries.ENCHANTMENTS.getKey(enchantment)).toString())
                        ) {
                            newListTag.add(
                                EnchantmentHelper.storeEnchantment(
                                    ResourceLocation.tryParse(enchantmentTag.getString("id")),
                                    calcEnchLevel(toolStack, enchantment, level)
                                )
                            );
                        } else {
                            newListTag.add(enchantmentTag);
                        }
                    }
                    nbt.put("Enchantments", newListTag);
                } else {
                    toolStack.enchant(enchantment, Math.min(enchantment.getMaxLevel(), level));
                }
                return true;
            }
        }
        return false;
    }

    public static int calcEnchLevel(ItemStack stack, Enchantment key, int value) {
        int currentLv = stack.getEnchantmentLevel(key);
        int levelCap = key.getMaxLevel();
        if (value == currentLv) {
            return Math.min(value + 1, levelCap);
        }
        return Math.min(Math.max(value, currentLv), levelCap);
    }
}
