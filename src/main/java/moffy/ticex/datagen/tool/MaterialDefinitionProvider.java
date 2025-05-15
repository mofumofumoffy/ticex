package moffy.ticex.datagen.tool;

import moffy.addonapi.AddonAPI;
import moffy.addonapi.ModsAvailableCondition;
import moffy.ticex.TicEX;
import moffy.ticex.lib.TicEXMaterials;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;

public class MaterialDefinitionProvider extends AbstractMaterialDataProvider  {
    public MaterialDefinitionProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    public String getName() {
        return "TiCEX Material Definitions";
    }

    @Override
    protected void addMaterials() {
        addMaterial(TicEXMaterials.INFINITY, 6, ORDER_COMPAT + ORDER_WEAPON, false, false, availableCondition("avaritia_compat"));
    }
    
    public ModsAvailableCondition availableCondition(String path){
        return new ModsAvailableCondition(new ResourceLocation(AddonAPI.MODID, "mods_available"), new ResourceLocation(TicEX.MODID, path));
    }
}
