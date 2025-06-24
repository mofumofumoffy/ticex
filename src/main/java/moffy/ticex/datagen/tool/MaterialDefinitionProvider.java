package moffy.ticex.datagen.tool;

import moffy.addonapi.AddonAPI;
import moffy.addonapi.ModsAvailableCondition;
import moffy.ticex.TicEX;
import moffy.ticex.lib.TicEXMaterials;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;

public class MaterialDefinitionProvider extends AbstractMaterialDataProvider {

    public MaterialDefinitionProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    public String getName() {
        return "TiCEX Material Definitions";
    }

    @Override
    protected void addMaterials() {
        addMaterial(
            TicEXMaterials.INFINITY,
            999,
            ORDER_COMPAT + ORDER_WEAPON,
            false,
            false,
            availableCondition("avaritia_compat")
        );
        addMaterial(
            TicEXMaterials.NEUTRON,
            5,
            ORDER_COMPAT + ORDER_SPECIAL,
            false,
            false,
            availableCondition("avaritia_compat")
        );
        addMaterial(
            TicEXMaterials.CRYSTAL_MATRIX,
            5,
            ORDER_COMPAT + ORDER_WEAPON,
            false,
            false,
            availableCondition("avaritia_compat")
        );
        addMaterial(
            TicEXMaterials.DRACONIUM,
            3,
            ORDER_COMPAT + ORDER_WEAPON,
            true,
            false,
            availableCondition("draconicevolution_compat")
        );
        addMaterial(
            TicEXMaterials.WYVERN,
            3,
            ORDER_COMPAT + ORDER_WEAPON,
            true,
            false,
            availableCondition("draconicevolution_compat")
        );
        addMaterial(
            TicEXMaterials.DRACONIC,
            4,
            ORDER_COMPAT + ORDER_WEAPON,
            true,
            false,
            availableCondition("draconicevolution_compat")
        );
        addMaterial(
            TicEXMaterials.CHAOTIC,
            4,
            ORDER_COMPAT + ORDER_WEAPON,
            true,
            false,
            availableCondition("draconicevolution_compat")
        );
        addMaterial(TicEXMaterials.ETHERIC, 999, ORDER_COMPAT + ORDER_WEAPON, false);
        addMaterial(TicEXMaterials.RECONSTRUCTION, 3, ORDER_COMPAT, false);
    }

    public ModsAvailableCondition availableCondition(String path) {
        return new ModsAvailableCondition(
            new ResourceLocation(AddonAPI.MODID, "mods_available"),
            new ResourceLocation(TicEX.MODID, path)
        );
    }
}
