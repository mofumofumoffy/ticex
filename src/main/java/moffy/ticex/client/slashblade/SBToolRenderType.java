package moffy.ticex.client.slashblade;

import java.util.Optional;

import mods.flammpfeil.slashblade.client.renderer.util.BladeRenderState;
import moffy.ticex.TicEX;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

public class SBToolRenderType {

    public static SBToolRenderType instance = null;

    public RenderType getSlashBladeBlend(MaterialVariantId material, PartType partType, Runnable whenIsDefault){
        return BladeRenderState.getSlashBladeBlend(partType.tryTexture(material, whenIsDefault));
    }

    public RenderType getSlashBladeLuminousBlend(MaterialVariantId material, PartType partType, Runnable whenIsDefault){
        return BladeRenderState.getSlashBladeBlendLuminous(partType.tryTexture(material,whenIsDefault));
    }

    public static void init(){
        instance = new SBToolRenderType();
    }

    public static enum PartType{
        BLADE(0, "blade"),
        HANDLE(2, "handle"),
        SAYA(1, "saya");

        private static final ResourceLocation BLADE_TEXTURE_LOC = new ResourceLocation(TicEX.MODID, "textures/item/tool/slashblade_tool/");
        private static final ResourceLocation DEFAULT_BLADE_TEXTURE_LOC = new ResourceLocation(TicEX.MODID, "textures/obj_tool/slashblade_tool/");

        private final int index;
        private final String name;

        private PartType(int index, String name){
            this.index = index;
            this.name = name;
        }

        public static PartType byIndex(int layerIndex){
            switch (layerIndex) {
                case 0:
                    return BLADE;
                case 1:
                    return HANDLE;
                case 2:
                    return SAYA;
                default:
                    return null;
            }
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

        public boolean textureExsists(ResourceLocation location){
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            Optional<Resource> resource = resourceManager.getResource(location);
            return resource.isPresent();
        }

        public ResourceLocation tryTexture(MaterialVariantId material, Runnable whenIsDefault){
            String suffix = "_"+material.getId().getNamespace()+"_"+material.getId().getPath();
            if(material.hasVariant()){
                suffix += "_"+material.getVariant();
            }
            if(textureExsists(BLADE_TEXTURE_LOC.withSuffix(this.name+suffix+".png"))){
                return BLADE_TEXTURE_LOC.withSuffix(this.name+suffix+".png");
            }

            if(whenIsDefault != null){
                whenIsDefault.run();
            }
            return DEFAULT_BLADE_TEXTURE_LOC.withSuffix(this.name+".png");
        }
    }
}
