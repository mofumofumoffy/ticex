package moffy.ticex.client.render.slashblade;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
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
import moffy.ticex.client.modules.slashblade.SBToolRenderType;
import moffy.ticex.client.render.custom.DecoratedRenderType;
import moffy.ticex.client.render.provider.context.ItemRenderContext;
import moffy.ticex.client.render.provider.context.RenderContext;
import moffy.ticex.client.render.provider.context.tool.RenderGenericContext;
import moffy.ticex.client.render.provider.renderer.IGenericRenderer;
import moffy.ticex.client.render.shader.ShaderProvider;
import moffy.ticex.item.modifiable.ModifiableSlashBladeItem;
import moffy.ticex.lib.utils.TicEXSBUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import java.util.*;

public class TicEXSBRenderers {

    private static final Map<RenderType, DecoratedRenderType> renderTypeCache = new HashMap<>();

    @OnlyIn(Dist.CLIENT)
    public static void tessellateWithShader(
            ItemRenderContext itemRenderContext,
            RenderType renderType,
            ShaderProvider.Generic shaderProvider,
            MaterialVariantId material,
            WavefrontObject wavefrontObject,
            SBToolRenderType.PartType partType,
            String... groupNames
    ) {
        boolean needShader = shaderProvider != null && TicEXConfig.USE_SHADER.get();

        Material atlasMaterial = new Material(
                InventoryMenu.BLOCK_ATLAS,
                new ResourceLocation(TicEX.MODID, "obj_tool/slashblade_tool/" + partType.getName())
        );
        TextureAtlasSprite sprite = atlasMaterial.sprite();

        MultiBufferSource bufferSource = itemRenderContext.bufferSource();
        if (needShader) {

            shaderProvider.prepareRenderMaterial(material);
            RenderContext renderContext = new RenderContext(
                    bufferSource,
                    1.0f, 1.0f, 1.0f, 1.0f,
                    itemRenderContext.poseStack(),
                    itemRenderContext.combinedLight(),
                    itemRenderContext.combinedOverlay()
            );

            RenderGenericContext genericContext = new RenderGenericContext(
                    renderContext, atlasMaterial.atlasLocation(), renderType,
                    rt -> bufferSource.getBuffer(getDecoratedRenderType(rt)),
                    itemRenderContext.displayContext() == ItemDisplayContext.GUI
            );
            shaderProvider.renderOverlay(genericContext, new IGenericRenderer() {
                @Override
                public void render(VertexConsumer vertexConsumer, float red, float green, float blue, float alpha, PoseStack poseStack, int combinedLight, int combinedOverlay) {
                    renderShader(vertexConsumer, wavefrontObject, groupNames, sprite);
                }
            });
        } else {
            renderGroupObjectsNaked(
                    bufferSource.getBuffer(renderType),
                    wavefrontObject,
                    groupNames
            );
        }
    }

    public static void renderShader(VertexConsumer vertexConsumer,
                                    WavefrontObject waveFrontObject, String[] groupNames, TextureAtlasSprite sprite) {

        renderGroupObjects(vertexConsumer, waveFrontObject, groupNames, sprite);
    }

    @OnlyIn(Dist.CLIENT)
    public static RenderType getDecoratedRenderType(RenderType renderType) {
        if (renderTypeCache.containsKey(renderType)) {
            return renderTypeCache.get(renderType);
        }

        DecoratedRenderType decorated = DecoratedRenderType.decorate(renderType, null, VertexFormat.Mode.TRIANGLES);

        renderTypeCache.put(renderType, decorated);
        return decorated;
    }

    public static void renderGroupObjects(VertexConsumer consumer, WavefrontObject wavefrontObject, String[] groupNames, TextureAtlasSprite sprite) {
        for (GroupObject groupObject : wavefrontObject.groupObjects) {
            for (String groupName : groupNames) {
                if (!groupName.equalsIgnoreCase(groupObject.name)) {
                    continue;
                }
                ArrayList<Face> facesToRender = groupObject.faces;
                if (facesToRender.isEmpty()) {
                    return;
                }

                for (Face face : facesToRender) {
                    renderObjFace(face, consumer, sprite);
                }
            }
        }
    }

    public static void renderGroupObjectsNaked(VertexConsumer consumer, WavefrontObject wavefrontObject, String[] groupNames) {
        for (GroupObject groupObject : wavefrontObject.groupObjects) {
            for (String groupName : groupNames) {
                if (!groupName.equalsIgnoreCase(groupObject.name)) {
                    continue;
                }
                groupObject.render(consumer);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderObjFace(Face face, VertexConsumer tessellator, TextureAtlasSprite sprite) {
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
            transform = TicEXSBUtils.defaultTransform.get();
        }

        for (int i = 0; i < face.vertices.length; ++i) {
            renderFaceVertex(face, tessellator, i, transform, textureOffset, sprite, averageU, averageV);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderFaceVertex(
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
    public static boolean renderBladeTool(ItemStack stack, float partialTicks, PoseStack matrixStackIn,
                                          MultiBufferSource bufferIn, int packedLightIn) {
        Item itemIn = stack.getItem();
        if (itemIn instanceof ModifiableSlashBladeItem) {
            matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-135));
            try (MSAutoCloser msac = MSAutoCloser.pushMatrix(matrixStackIn)) {
                EnumSet<SwordType> types = SwordType.from(stack);
                ResourceLocation modelLocation = stack.getCapability(ItemSlashBlade.BLADESTATE).map(state -> {
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

                try (MSAutoCloser msac2 = MSAutoCloser.pushMatrix(matrixStackIn)) {
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

}
