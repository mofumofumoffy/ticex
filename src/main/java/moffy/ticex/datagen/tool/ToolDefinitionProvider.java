package moffy.ticex.datagen.tool;

import static slimeknights.tconstruct.tools.TinkerToolParts.largePlate;
import static slimeknights.tconstruct.tools.TinkerToolParts.toolBinding;
import static slimeknights.tconstruct.tools.TinkerToolParts.toughHandle;

import moffy.ticex.TicEX;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ArmorItem;
import net.minecraftforge.common.ToolActions;
import slimeknights.tconstruct.library.data.tinkering.AbstractToolDefinitionDataProvider;
import slimeknights.tconstruct.library.materials.RandomMaterial;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.definition.module.ToolModule;
import slimeknights.tconstruct.library.tools.definition.module.build.MultiplyStatsModule;
import slimeknights.tconstruct.library.tools.definition.module.build.SetStatsModule;
import slimeknights.tconstruct.library.tools.definition.module.build.ToolActionsModule;
import slimeknights.tconstruct.library.tools.definition.module.build.ToolSlotsModule;
import slimeknights.tconstruct.library.tools.definition.module.build.ToolTraitsModule;
import slimeknights.tconstruct.library.tools.definition.module.material.DefaultMaterialsModule;
import slimeknights.tconstruct.library.tools.definition.module.material.PartStatsModule;
import slimeknights.tconstruct.library.tools.nbt.MultiplierNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerToolParts;

public class ToolDefinitionProvider extends AbstractToolDefinitionDataProvider {

    public ToolDefinitionProvider(PackOutput packOutput) {
        super(packOutput, TicEX.MODID);
    }

    @Override
    protected void addToolDefinitions() {
        RandomMaterial tier1Material = RandomMaterial.random().tier(1).build();
        RandomMaterial randomCatalystMaterial = RandomMaterial.random().tier(3, 6).build();
        DefaultMaterialsModule defaultTwoParts = DefaultMaterialsModule.builder()
            .material(tier1Material, tier1Material, randomCatalystMaterial)
            .build();

        RandomMaterial tier2Material = RandomMaterial.random().tier(1, 2).build();
        DefaultMaterialsModule plateMaterials = DefaultMaterialsModule.builder()
            .material(tier2Material, tier2Material, randomCatalystMaterial)
            .build();
        ToolModule plateSlots = ToolSlotsModule.builder().slots(SlotType.UPGRADE, 2).slots(SlotType.DEFENSE, 3).build();

        if (TicEXRegistry.SLASHBLADE_DEFINITION != null) {
            define(TicEXRegistry.SLASHBLADE_DEFINITION)
                .module(
                    PartStatsModule.parts()
                        .part(TicEXRegistry.SLASHBLADE_BLADE, 0.75f)
                        .part(TicEXRegistry.SLASHBLADE_SAYA, 0.5f)
                        .part(toughHandle, 0.5f)
                        .build()
                )
                .module(DefaultMaterialsModule.builder().material(tier1Material, tier1Material, tier1Material).build())
                .module(
                    new SetStatsModule(
                        StatsNBT.builder().set(ToolStats.ATTACK_DAMAGE, 3f).set(ToolStats.ATTACK_SPEED, 1.6f).build()
                    )
                )
                .module(
                    new MultiplyStatsModule(
                        MultiplierNBT.builder()
                            .set(ToolStats.MINING_SPEED, 0.5f)
                            .set(ToolStats.DURABILITY, 1.1f)
                            .build()
                    )
                )
                .smallToolStartingSlots()
                .module(ToolActionsModule.of(ToolActions.SWORD_DIG));
        }

        if (TicEXRegistry.GUN_DEFINITION != null) {
            define(TicEXRegistry.GUN_DEFINITION)
                .module(
                    PartStatsModule.parts()
                        .part(largePlate)
                        .part(toughHandle, 0.5f)
                        .part(TicEXRegistry.CATALYST_KINETIC_GUN)
                        .build()
                )
                .module(defaultTwoParts)
                .module(
                    new SetStatsModule(
                        StatsNBT.builder().set(ToolStats.ATTACK_DAMAGE, 3f).set(ToolStats.ATTACK_SPEED, 1.6f).build()
                    )
                )
                .module(new MultiplyStatsModule(MultiplierNBT.builder().set(ToolStats.DURABILITY, 1.1f).build()))
                .smallToolStartingSlots();
        }

        if (TicEXRegistry.SPELLBOOK_DEFINITION != null) {
            define(TicEXRegistry.SPELLBOOK_DEFINITION)
                .module(
                    PartStatsModule.parts()
                        .part(largePlate)
                        .part(toolBinding, 0.5f)
                        .part(TicEXRegistry.CATALYST_IRONS_SPELLBOOK)
                        .build()
                )
                .module(defaultTwoParts)
                .module(
                    new SetStatsModule(
                        StatsNBT.builder().set(ToolStats.ATTACK_DAMAGE, 3f).set(ToolStats.ATTACK_SPEED, 1.6f).build()
                    )
                )
                .module(new MultiplyStatsModule(MultiplierNBT.builder().set(ToolStats.DURABILITY, 1.1f).build()))
                .module(ToolTraitsModule.builder().trait(TicEXRegistry.OVERCASTING_MODIFIER).build())
                .smallToolStartingSlots();
        }

        if (TicEXRegistry.MEKAPLATE_DEFINITION != null) {
            defineArmor(TicEXRegistry.MEKAPLATE_DEFINITION)
                .modules(slots ->
                    PartStatsModule.armor(slots)
                        .part(TinkerToolParts.plating, 1)
                        .part(TinkerToolParts.maille, 1)
                        .part(TicEXRegistry.CATALYST_MEKASUIT, 1)
                )
                .module(plateMaterials)
                .module(
                    ArmorItem.Type.CHESTPLATE,
                    new MultiplyStatsModule(MultiplierNBT.builder().set(ToolStats.ATTACK_DAMAGE, 0.4f).build())
                )
                .module(plateSlots)
                .module(ToolTraitsModule.builder().trait(TicEXRegistry.MEKANIC_MODIFIER).build());
        }

        if (TicEXRegistry.SINGULAR_GEM_DEFINITION != null) {
            defineArmor(TicEXRegistry.SINGULAR_GEM_DEFINITION)
                .modules(slots ->
                    PartStatsModule.armor(slots)
                        .part(TinkerToolParts.plating, 1)
                        .part(TinkerToolParts.maille, 1)
                        .part(TicEXRegistry.CATALYST_GEM, 1)
                )
                .module(plateMaterials)
                .module(
                    ArmorItem.Type.CHESTPLATE,
                    new MultiplyStatsModule(MultiplierNBT.builder().set(ToolStats.ATTACK_DAMAGE, 0.4f).build())
                )
                .module(plateSlots)
                .module(ArmorItem.Type.HELMET, ToolTraitsModule.builder().trait(TicEXRegistry.ABYSSAL_MODIFIER).build())
                .module(
                    ArmorItem.Type.CHESTPLATE,
                    ToolTraitsModule.builder().trait(TicEXRegistry.INFERNAL_MODIFIER).build()
                )
                .module(
                    ArmorItem.Type.LEGGINGS,
                    ToolTraitsModule.builder().trait(TicEXRegistry.GRAVITY_MODIFIER).build()
                )
                .module(
                    ArmorItem.Type.BOOTS,
                    ToolTraitsModule.builder().trait(TicEXRegistry.HURRICANE_MODIFIER).build()
                );
        }
    }

    @Override
    public String getName() {
        return "TiCEX Tool Definitions";
    }
}
