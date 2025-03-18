package moffy.ticex.datagen.materials;

import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.data.PackOutput;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;
import slimeknights.tconstruct.library.data.material.AbstractMaterialTraitDataProvider;

public class TicEXMaterialTraitsDataGen extends AbstractMaterialTraitDataProvider{

    public TicEXMaterialTraitsDataGen(PackOutput packOutput, AbstractMaterialDataProvider materials) {
        super(packOutput, materials);
    }

    @Override
    public String getName() {
        return "TicEX Material Traits";
    }

    @Override
    protected void addMaterialTraits() {
        addDefaultTraits(TicEXMaterialDataGen.INFINITY, TicEXRegistry.OMNIPOTEMCE_MODIFIER);
    }
    
}
