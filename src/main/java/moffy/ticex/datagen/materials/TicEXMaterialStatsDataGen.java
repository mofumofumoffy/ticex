package moffy.ticex.datagen.materials;

import moffy.ticex.modules.avaritia.InfinityTier;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;
import slimeknights.tconstruct.library.data.material.AbstractMaterialStatsDataProvider;
import slimeknights.tconstruct.tools.stats.GripMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.LimbMaterialStats;
import slimeknights.tconstruct.tools.stats.PlatingMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

public class TicEXMaterialStatsDataGen extends AbstractMaterialStatsDataProvider{

    public TicEXMaterialStatsDataGen(PackOutput packOutput, AbstractMaterialDataProvider materials) {
        super(packOutput, materials);
    }

    @Override
    public String getName() {
        return "TicEX Material Stats";
    }

    @Override
    protected void addMaterialStats() {
        addMaterialStats(
            TicEXMaterialDataGen.INFINITY, 
            new HeadMaterialStats(9999, 999, InfinityTier.instance, 99.1f), 
            new HandleMaterialStats(9.99f, 9.99f, 9.99f, 9.99f),
            new LimbMaterialStats(9999, 99.9f, 99.9f, 1),
            new GripMaterialStats(9999, 1, 99)
        );

        addMaterialStats(
            TicEXMaterialDataGen.CRYSTAL_MATRIX,
            new HandleMaterialStats(1.25f, 1.5f, 1.5f, 1.5f),
            new HeadMaterialStats(3200, 13, Tiers.NETHERITE, 4.5f)
        );

        addMaterialStats(
            TicEXMaterialDataGen.RECONSTRUCTED_CATALYST,
            new CatalystMaterialStats(1.01f)
        );

        addArmorShieldStats(
            TicEXMaterialDataGen.INFINITY, 
            PlatingMaterialStats.builder()
                .armor(8.14f, 17.44f, 30f, 9.3f)
                .shieldDurability(9999)
                .durabilityFactor(9.99f)
                .knockbackResistance(0.25f)
                .toughness(10.46f),
            StatlessMaterialStats.MAILLE
        );

        addArmorStats(
            TicEXMaterialDataGen.NEUTRONIUM,
            PlatingMaterialStats.builder()
                .armor(3.36f, 7.2f, 9.6f, 3.84f)
                .toughness(15)
                .knockbackResistance(10), 
            StatlessMaterialStats.MAILLE
        );
    }
    
}
