package moffy.ticex.datagen.tool;

import moffy.ticex.lib.CatalystMaterialStatsType;
import moffy.ticex.lib.TicEXMaterials;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Tiers;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;
import slimeknights.tconstruct.library.data.material.AbstractMaterialStatsDataProvider;
import slimeknights.tconstruct.tools.stats.GripMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.LimbMaterialStats;
import slimeknights.tconstruct.tools.stats.PlatingMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

public class MaterialStatsProvider extends AbstractMaterialStatsDataProvider {

    public MaterialStatsProvider(PackOutput packOutput, AbstractMaterialDataProvider materials) {
        super(packOutput, materials);
    }

    @Override
    public String getName() {
        return "TiCEX Material Stats";
    }

    @Override
    protected void addMaterialStats() {
        addMeleeHarvest();
        addRanged();
        addArmor();
        addMisc();
    }

    private void addMeleeHarvest() {
        addMaterialStats(
            TicEXMaterials.INFINITY,
            new HeadMaterialStats(9999, 999.0f, TicEXRegistry.INFINITY_TIER, 999.1f),
            HandleMaterialStats.multipliers()
                .attackDamage(9.99f)
                .attackSpeed(9.99f)
                .durability(9.99f)
                .miningSpeed(9.99f)
                .build(),
            StatlessMaterialStats.BINDING
        );
        addMaterialStats(
            TicEXMaterials.CRYSTAL_MATRIX,
            new HeadMaterialStats(3200, 13f, Tiers.NETHERITE, 4.5f),
            HandleMaterialStats.multipliers()
                .attackDamage(1.5f)
                .attackSpeed(1.5f)
                .durability(1.25f)
                .miningSpeed(1.5f)
                .build(),
            StatlessMaterialStats.BINDING
        );
        addMaterialStats(
                TicEXMaterials.BLAZING,
                new HeadMaterialStats(2800, 10.4f, Tiers.DIAMOND, 3.6f),
                HandleMaterialStats.multipliers()
                        .attackDamage(1.5f)
                        .attackSpeed(1.5f)
                        .durability(1.25f)
                        .miningSpeed(1.5f)
                        .build(),
                StatlessMaterialStats.BINDING
        );
        addMaterialStats(
            TicEXMaterials.DRACONIUM,
            new HeadMaterialStats(512, 7.0f, Tiers.NETHERITE, 4.0f),
            HandleMaterialStats.multipliers()
                .attackDamage(1.1f)
                .attackSpeed(1.1f)
                .durability(1.1f)
                .miningSpeed(1.1f)
                .build(),
            StatlessMaterialStats.BINDING
        );
        addMaterialStats(
            TicEXMaterials.WYVERN,
            new HeadMaterialStats(1024, 10f, Tiers.NETHERITE, 4.5f),
            HandleMaterialStats.multipliers()
                .attackDamage(1.5f)
                .attackSpeed(1.5f)
                .durability(1.25f)
                .miningSpeed(1.5f)
                .build(),
            StatlessMaterialStats.BINDING
        );
        addMaterialStats(
            TicEXMaterials.DRACONIC,
            new HeadMaterialStats(1536, 10f, Tiers.NETHERITE, 5f),
            HandleMaterialStats.multipliers()
                .attackDamage(1.75f)
                .attackSpeed(1.75f)
                .durability(1.5f)
                .miningSpeed(1.75f)
                .build(),
            StatlessMaterialStats.BINDING
        );
        addMaterialStats(
            TicEXMaterials.CHAOTIC,
            new HeadMaterialStats(2048, 10f, Tiers.NETHERITE, 5.5f),
            HandleMaterialStats.multipliers()
                .attackDamage(2.2f)
                .attackSpeed(2.2f)
                .durability(2f)
                .miningSpeed(2.2f)
                .build(),
            StatlessMaterialStats.BINDING
        );
        addMaterialStats(
            TicEXMaterials.ETHERIC,
            new HeadMaterialStats(2434, 7.5f, Tiers.NETHERITE, 7.5f),
            HandleMaterialStats.multipliers()
                .attackDamage(1.25f)
                .attackSpeed(1.3f)
                .durability(0.15f)
                .miningSpeed(1f)
                .build(),
            StatlessMaterialStats.BINDING
        );
    }

    private void addRanged() {
        addMaterialStats(
            TicEXMaterials.INFINITY,
            new LimbMaterialStats(9999, 99.9f, 99.9f, 1.0f),
            new GripMaterialStats(9.99f, 1.0f, 4995.5f)
        );
        addMaterialStats(
            TicEXMaterials.CRYSTAL_MATRIX,
            new LimbMaterialStats(3200, -0.4f, 0.45f, -0.1f),
            new GripMaterialStats(9.99f, 1.0f, 4995.5f)
        );
        addMaterialStats(
            TicEXMaterials.ETHERIC,
            new LimbMaterialStats(2434, 0.25f, -0.05f, -0.1f),
            new GripMaterialStats(9.99f, 1.0f, 1137.5f)
        );
    }

    private void addArmor() {
        addArmorShieldStats(
            TicEXMaterials.INFINITY,
            PlatingMaterialStats.builder()
                .durabilityFactor(9999.0f)
                .armor(8.14f, 17.44f, 30f, 9.3f)
                .toughness(15f)
                .knockbackResistance(0.25f),
            StatlessMaterialStats.MAILLE
        );
        addArmorShieldStats(
            TicEXMaterials.NEUTRON,
            PlatingMaterialStats.builder()
                .durabilityFactor(1111.0f)
                .armor(3.36f, 7.2f, 9f, 3.84f)
                .toughness(15f)
                .knockbackResistance(100f),
            StatlessMaterialStats.MAILLE
        );
    }

    private void addMisc() {
        addMaterialStats(
            TicEXMaterials.RECONSTRUCTION,
            CatalystMaterialStatsType.getAllCatalystStats()
                .stream()
                .map(materialStat -> materialStat.getDefaultStats())
                .toArray(CatalystMaterialStatsType[]::new)
        );
    }
}
