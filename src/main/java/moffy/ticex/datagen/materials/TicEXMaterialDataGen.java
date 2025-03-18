package moffy.ticex.datagen.materials;

import moffy.ticex.TicEX;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;
import slimeknights.tconstruct.library.materials.definition.MaterialId;

public class TicEXMaterialDataGen extends AbstractMaterialDataProvider{

    public static final MaterialId INFINITY = createMaterial("infinity");
    public static final MaterialId NEUTRONIUM = createMaterial("neutronium");
    public static final MaterialId CRYSTAL_MATRIX = createMaterial("crystal_matrix");
    public static final MaterialId RECONSTRUCTED_CATALYST = createMaterial("reconstructed_catalyst");

    public TicEXMaterialDataGen(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    public String getName() {
        return "Ticex Material Data";
    }

    @Override
    protected void addMaterials() {
        addMaterial(RECONSTRUCTED_CATALYST, 3, ORDER_GENERAL, false);

        addCompatMetalMaterial(INFINITY, 4, ORDER_SPECIAL + ORDER_COMPAT, "ticex:infinity");
        addCompatMetalMaterial(NEUTRONIUM, 4, ORDER_GENERAL + ORDER_COMPAT, "ticex:neutronium");
        addCompatMetalMaterial(CRYSTAL_MATRIX, 4, ORDER_WEAPON + ORDER_COMPAT, "ticex:crystal_matrix");
    }
    
    private static MaterialId createMaterial(String path){
        return new MaterialId(new ResourceLocation(TicEX.MODID, path));
    }
}
