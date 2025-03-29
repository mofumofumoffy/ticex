package moffy.ticex.datagen.tinkering;

import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ArmorItem;
import slimeknights.tconstruct.library.data.tinkering.AbstractToolDefinitionDataProvider;
import slimeknights.tconstruct.library.materials.RandomMaterial;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.definition.module.ToolModule;
import slimeknights.tconstruct.library.tools.definition.module.build.MultiplyStatsModule;
import slimeknights.tconstruct.library.tools.definition.module.build.ToolSlotsModule;
import slimeknights.tconstruct.library.tools.definition.module.material.DefaultMaterialsModule;
import slimeknights.tconstruct.library.tools.definition.module.material.PartStatsModule;
import slimeknights.tconstruct.library.tools.nbt.MultiplierNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.ArmorDefinitions;
import slimeknights.tconstruct.tools.TinkerToolParts;

public class TicEXToolDefinitionsDataGen extends AbstractToolDefinitionDataProvider{

    public TicEXToolDefinitionsDataGen(PackOutput packOutput, String modId) {
        super(packOutput, modId);
    }

    @Override
    public String getName() {
        return "TicEX Tool Definitions";
    }

    @Override
    protected void addToolDefinitions() {
        RandomMaterial tier2Material = RandomMaterial.random().tier(1, 2).build();
        DefaultMaterialsModule plateMaterials = DefaultMaterialsModule.builder().material(tier2Material, tier2Material).build();
        ToolModule plateSlots =
        ToolSlotsModule.builder()
                        .slots(SlotType.UPGRADE, 2)
                        .slots(SlotType.DEFENSE, 3).build();

        defineArmor(TicEXRegistry.MEKAPLATE_DEFINITION)
            .modules(slots -> PartStatsModule.armor(slots)
                .part(TinkerToolParts.plating, 1)
                .part(TinkerToolParts.maille, 1)
                .part(TicEXRegistry.CATALYST_MEKAPLATE, 1))
            .module(plateMaterials)
            .module(ArmorItem.Type.HELMET, new MultiplyStatsModule(MultiplierNBT.builder().set(ToolStats.ATTACK_DAMAGE, 0.4f).build()))
            .module(plateSlots);
    }
    
}
