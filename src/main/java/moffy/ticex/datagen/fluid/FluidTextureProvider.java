package moffy.ticex.datagen.fluid;

import moffy.ticex.TicEX;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.fluid.UnplaceableFluid;
import slimeknights.mantle.fluid.texture.AbstractFluidTextureProvider;
import slimeknights.mantle.fluid.texture.FluidTexture;
import slimeknights.mantle.registration.object.FluidObject;

public class FluidTextureProvider extends AbstractFluidTextureProvider {

    private static final int MOLTEN_LENGTH = "molten_".length();

    public FluidTextureProvider(PackOutput packOutput) {
        super(packOutput, TicEX.MODID);
    }

    @Override
    public String getName() {
        return "TiCEX fluid textures";
    }

    @Override
    public void addTextures() {
        molten(TicEXRegistry.MOLTEN_INFINITY);
        molten(TicEXRegistry.MOLTEN_NEUTRON);
        molten(TicEXRegistry.MOLTEN_CRYSTAL_MATRIX);
        molten(TicEXRegistry.MOLTEN_ETHERIC);
        molten(TicEXRegistry.MOLTEN_RECONSTRUCTION_CORE);

        int i = 0;
        for(FluidObject<UnplaceableFluid> fuel : TicEXRegistry.RF_FURNACE_FUELS){
            fuels(fuel).color(i++ * 0x0C0C0C);
        }
    }

    private FluidTexture.Builder fuels(FluidObject<?> fluid) {
        return this.texture(fluid.getType()).textures(new ResourceLocation(TicEX.MODID, "fluid/rf_furnace_fuels/"), false, false);
    }

    private FluidTexture.Builder named(FluidObject<?> fluid, String name) {
        return texture(fluid).textures(new ResourceLocation(TicEX.MODID, "fluid/"+name+"/"), false, false);
    }
    
    private FluidTexture.Builder molten(FluidObject<?> fluid) {
        return named(fluid, "molten/" + withoutMolten(fluid));
    }

    public static String withoutMolten(FluidObject<?> fluid) {
        return fluid.getId().getPath().substring(MOLTEN_LENGTH);
    }
}
