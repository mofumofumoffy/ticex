package moffy.ticex.datagen.material;

import moffy.ticex.TicEX;
import moffy.ticex.lib.TicEXMaterials;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.client.data.material.AbstractMaterialSpriteProvider;
import slimeknights.tconstruct.library.client.data.spritetransformer.AnimatedGreyToSpriteTransformer;
import slimeknights.tconstruct.library.client.data.spritetransformer.GreyToSpriteTransformer;

public class TicEXMaterialSpriteProvider extends AbstractMaterialSpriteProvider {
    @Override
    public @NotNull String getName() {
        return "TicEX Materials";
    }

    @Override
    protected void addAllMaterials() {
        buildMaterial(TicEXMaterials.DRACONIUM).meleeHarvest()
                .fallbacks("metal").transformer(
                        GreyToSpriteTransformer.builder()
                                .addARGB(0, 0xFF000000)
                                .addARGB(63, 0xff00001e)
                                .addARGB(102, 0xff000034)
                                .addARGB(140, 0xff000052)
                                .addARGB(178, 0xff000069)
                                .addARGB(216, 0xff00008a)
                                .addARGB(255, 0xff0000aa)
                                .build()
                );
        buildMaterial(TicEXMaterials.WYVERN).meleeHarvest()
                .fallbacks("metal").transformer(
                        GreyToSpriteTransformer.builder()
                                .addARGB(0, 0xFF000000)
                                .addARGB(63, 0xff1b001b)
                                .addARGB(102, 0xff300030)
                                .addARGB(140, 0xff4f004f)
                                .addARGB(178, 0xff690069)
                                .addARGB(216, 0xff8a008a)
                                .addARGB(255, 0xffaa00aa)
                                .build()
                );
        buildMaterial(TicEXMaterials.DRACONIC).meleeHarvest()
                .fallbacks("metal").transformer(
                        GreyToSpriteTransformer.builder()
                                .addARGB(0, 0xFF000000)
                                .addARGB(63, 0xff3f3f16)
                                .addARGB(102, 0xff686826)
                                .addARGB(140, 0xffa2a238)
                                .addARGB(178, 0xffc5c541)
                                .addARGB(216, 0xffefef4d)
                                .addARGB(255, 0xffffff55)
                                .build()
                );
        buildMaterial(TicEXMaterials.CHAOTIC).meleeHarvest()
                .fallbacks("metal").transformer(
                        GreyToSpriteTransformer.builder()
                                .addARGB(0, 0xFF919191)
                                .addARGB(64, 0xff6e6e6e)
                                .addARGB(102, 0xff4f4f4f)
                                .addARGB(140, 0xff333333)
                                .addARGB(178, 0xff212121)
                                .addARGB(216, 0xff1c1c1c)
                                .addARGB(255, 0xff0f0f0f)
                                .build()
        );
        buildMaterial(TicEXMaterials.CRYSTAL_MATRIX).meleeHarvest().ranged()
                .fallbacks("metal").transformer(
                        GreyToSpriteTransformer.builder()
                                .addARGB(0, 0xFF000000)
                                .addTexture(63, new ResourceLocation(TicEX.MODID, "material/crystal_matrix_outline_dark"))
                                .addTexture(102, new ResourceLocation(TicEX.MODID, "material/crystal_matrix_outline"))
                                .addARGB(140, 0xFF2D96B9)
                                .addARGB(178, 0xFF35A6D6)
                                .addARGB(216, 0xFF47BDEF)
                                .addARGB(255, 0xFFAADCFF)
                                .build()
        );
        buildMaterial(TicEXMaterials.ETHERIC).meleeHarvest().ranged()
                .fallbacks("metal").transformer(
                        GreyToSpriteTransformer.builder()
                                .addARGB(0, 0xFF000000)
                                .addARGB(63, 0xff195c27)
                                .addARGB(102, 0xff227d35)
                                .addARGB(140, 0xff2eb04a)
                                .addARGB(178, 0xff34c954)
                                .addARGB(216, 0xff3ae85f)
                                .addARGB(255, 0xff3efa66)
                                .build()
        );
        buildMaterial(TicEXMaterials.INFINITY).meleeHarvest().ranged().armor()
                .fallbacks("metal").transformer(
                        AnimatedGreyToSpriteTransformer.builder()
                                .addARGB(0, 0xFF000000)
                                .addTexture(63, new ResourceLocation(TicEX.MODID, "material/infinity_dark"))
                                .addTexture(178, new ResourceLocation(TicEX.MODID, "material/infinity_medium"))
                                .addTexture(255, new ResourceLocation(TicEX.MODID, "material/infinity_light"))
                                .animated(new ResourceLocation(TicEX.MODID, "material/infinity_medium"), 9)
        );
        buildMaterial(TicEXMaterials.NEUTRON).armor()
                .fallbacks("metal").transformer(
                        GreyToSpriteTransformer.builder()
                                .addARGB(0, 0xFF000000)
                                .addTexture(63, new ResourceLocation(TicEX.MODID, "material/neutron_outline_dark"))
                                .addTexture(102, new ResourceLocation(TicEX.MODID, "material/neutron_outline"))
                                .addARGB(140, 0xFF3D3D3D)
                                .addARGB(178, 0xFF303030)
                                .addARGB(216, 0xFF2B2B2B)
                                .addARGB(255, 0xFF141414)
                                .build()
        );
    }
}
