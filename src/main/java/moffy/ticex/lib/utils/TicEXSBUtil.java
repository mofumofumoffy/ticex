package moffy.ticex.lib.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Supplier;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import mods.flammpfeil.slashblade.client.renderer.model.obj.Face;
import mods.flammpfeil.slashblade.client.renderer.model.obj.GroupObject;
import mods.flammpfeil.slashblade.client.renderer.model.obj.Vertex;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import moffy.ticex.TicEX;
import moffy.ticex.TicEXConfig;
import moffy.ticex.client.ShaderInstanceMap.InstanceProvider;
import moffy.ticex.client.slashblade.SBToolRenderType.PartType;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

public class TicEXSBUtil {

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

    public static void tessellateWithShader(MaterialVariantId material, WavefrontObject wavefrontObject, VertexConsumer tessellator, MultiBufferSource bufferSource, PartType partType, String... groupNames) {
        Iterator<GroupObject> var3 = wavefrontObject.groupObjects.iterator();

        InstanceProvider instanceProvider = TicEXRegistry.SHADER_INSTANCE_MAP.getInstanceProvider(material);
        boolean needShader = instanceProvider != null && TicEXConfig.USE_SHADER.get();

        Material atlasMaterial = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(TicEX.MODID, "obj_tool/slashblade_tool/"+ partType.getName()));
        TextureAtlasSprite sprite = atlasMaterial.sprite();

        if(needShader){
            instanceProvider.getSetupMethod().run();
        }
        while(var3.hasNext()) {
            GroupObject groupObject = (GroupObject)var3.next();
            String[] var5 = groupNames;
            int var6 = groupNames.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                String groupName = var5[var7];
                if (groupName.equalsIgnoreCase(groupObject.name)) {
                    if(needShader){
                        renderWithShader(groupObject, bufferSource.getBuffer(getRenderType(instanceProvider.getInstanceSupplier())), sprite);
                    } else {
                        groupObject.render(tessellator);
                    }
                }
            }
        }
    }

    public static void renderWithShader(GroupObject groupObject, VertexConsumer tessellator, TextureAtlasSprite sprite){
        if (groupObject.faces.size() > 0) {
            Iterator<Face> var2 = groupObject.faces.iterator();
   
            while(var2.hasNext()) {
               Face face = (Face)var2.next();
               addFaceForRender(face, tessellator, sprite);
            }
         }
    }

    public static void addFaceForRender(Face face, VertexConsumer tessellator, TextureAtlasSprite sprite) {
        final float textureOffset = 5.0E-4F;
        VertexConsumer wr = tessellator;
        
        if (face.faceNormal == null) {
            face.faceNormal = face.calculateFaceNormal();
        }

        float averageU = 0.0F;
        float averageV = 0.0F;
        if (face.textureCoordinates != null && face.textureCoordinates.length > 0) {
            for(int i = 0; i < face.textureCoordinates.length; ++i) {
                averageU += face.textureCoordinates[i].u * Face.uvOperator.x() + Face.uvOperator.z();
                averageV += face.textureCoordinates[i].v * Face.uvOperator.y() + Face.uvOperator.w();
            }

            averageU /= (float)face.textureCoordinates.length;
            averageV /= (float)face.textureCoordinates.length;
        }
        
        Matrix4f transform;
        if (Face.matrix != null) {
            PoseStack.Pose me = Face.matrix.last();
            transform = me.pose();
        } else {
            transform = (Matrix4f)defaultTransform.get();
        }

        for(int i = 0; i < face.vertices.length; ++i) {
            putVertex(face, wr, i, transform, textureOffset, sprite, averageU, averageV);
        }
    }

    public static void putVertex(Face face, VertexConsumer wr, int i, Matrix4f transform, float textureOffset, TextureAtlasSprite sprite, float averageU, float averageV) {
        wr.vertex(transform, face.vertices[i].x, face.vertices[i].y, face.vertices[i].z);
        wr.color(1.0f, 1.0f, 1.0f, (Integer)Face.alphaOverride.apply(new Vector4f(face.vertices[i].x, face.vertices[i].y, face.vertices[i].z, 1.0F), Face.col.getAlpha()));
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

            wr.uv(sprite.getU0() + (textureU + offsetU) * (sprite.getU1() - sprite.getU0()), sprite.getV0() + (textureV + offsetV) * (sprite.getV1() - sprite.getV0()));
        } else {
            wr.uv(0.0F, 0.0F);
        }

        wr.overlayCoords(OverlayTexture.NO_OVERLAY);
        wr.uv2(Face.lightmap);
        Vector3f vector3f;
        if (Face.isSmoothShade && face.vertexNormals != null) {
            Vertex normal = face.vertexNormals[i];
            Vec3 nol = new Vec3((double)normal.x, (double)normal.y, (double)normal.z);
            vector3f = new Vector3f((float)nol.x, (float)nol.y, (float)nol.z);
        } else {
            vector3f = new Vector3f(face.faceNormal.x, face.faceNormal.y, face.faceNormal.z);
        }

        vector3f.mul(new Matrix3f(transform));
        vector3f.normalize();
        wr.normal(vector3f.x(), vector3f.y(), vector3f.z());
        wr.endVertex();
    }

    public static RenderType getRenderType(Supplier<ShaderInstance> instanceSupplier){
        RenderType.CompositeState state = CompositeState.builder().setShaderState(new ShaderStateShard(instanceSupplier)).setOutputState(RenderStateShard.ITEM_ENTITY_TARGET).setTextureState(new RenderStateShard.TextureStateShard(InventoryMenu.BLOCK_ATLAS, false, true)).setTransparencyState(RenderStateShard.NO_TRANSPARENCY).setLightmapState(RenderType.LIGHTMAP).setOverlayState(RenderType.OVERLAY).setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE).createCompositeState(true);
        return RenderType.create("slashblade_tool_shader_blend", DefaultVertexFormat.NEW_ENTITY, Mode.TRIANGLES, 256, true, false, state);
    }

    public static boolean applyEnchantment(ItemStack toolStack, Enchantment enchantment, int level){
        for(Enchantment disallowed : TicEXSBUtil.disallowedEnchantments){
            if(!enchantment.getDescriptionId().equals(disallowed.getDescriptionId())){
                if(toolStack.getEnchantmentLevel(enchantment) > 0){
                    CompoundTag nbt = toolStack.getOrCreateTag();
                    if (!nbt.contains("Enchantments", Tag.TAG_LIST)) {
                        nbt.put("Enchantments", new ListTag());
                    }
        
                    ListTag listTag = nbt.getList("Enchantments", Tag.TAG_COMPOUND);
                    ListTag newListTag = new ListTag();
                    for(int i = 0; i < listTag.size(); i++){
                        CompoundTag enchantmentTag = listTag.getCompound(i);
                        if(enchantmentTag.getString("id").equals(ForgeRegistries.ENCHANTMENTS.getKey(enchantment).toString())){
                            newListTag.add(EnchantmentHelper.storeEnchantment(ResourceLocation.tryParse(enchantmentTag.getString("id")), calcEnchLevel(toolStack, enchantment, level)));
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

    public static int calcEnchLevel(ItemStack stack, Enchantment key, int value){
        int currentLv = stack.getEnchantmentLevel(key);
        int levelCap = key.getMaxLevel();
        if(value == currentLv){
            return Math.min(value + 1, levelCap);
        } 
        return Math.min(Math.max(value, currentLv), levelCap);
    }
}
