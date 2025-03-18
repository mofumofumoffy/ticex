package moffy.ticex.datagen.materials;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.Component;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public record CatalystMaterialStats (float durability) implements IMaterialStats{

    public static final MaterialStatsId ID = new MaterialStatsId(TConstruct.getResource("catalyst"));
    public static final MaterialStatType<CatalystMaterialStats> TYPE;

    private static final String DURABILITY_PREFIX;
    private static final List<Component> DESCRIPTION;

    public CatalystMaterialStats(float durability){
        this.durability = durability;
    }

    @Override
    public void apply(ModifierStatsBuilder builder, float scale) {
        ToolStats.DURABILITY.percent(builder, (double)(this.durability * scale));
    }

    @Override
    public List<Component> getLocalizedDescriptions() {
        return DESCRIPTION;
    }

    @Override
    public List<Component> getLocalizedInfo() {
        List<Component> list = new ArrayList<>();
        list.add(IToolStat.formatColoredPercentBoost(DURABILITY_PREFIX, this.durability));
        return list;
    }

    @Override
    public MaterialStatType<?> getType() {
        return TYPE;
    }
    

    static {
      TYPE = new MaterialStatType<CatalystMaterialStats>(ID, new CatalystMaterialStats(0), RecordLoadable.create(FloatLoadable.ANY.defaultField("durability", 0.0F, true, CatalystMaterialStats::durability), CatalystMaterialStats::new));
      DURABILITY_PREFIX = IMaterialStats.makeTooltipKey(TConstruct.getResource("durability"));
      DESCRIPTION = List.of(IMaterialStats.makeTooltip(TConstruct.getResource("handle.durability.description")), IMaterialStats.makeTooltip(TConstruct.getResource("handle.attack_damage.description")), IMaterialStats.makeTooltip(TConstruct.getResource("handle.attack_speed.description")), IMaterialStats.makeTooltip(TConstruct.getResource("handle.mining_speed.description")));
   }
}
