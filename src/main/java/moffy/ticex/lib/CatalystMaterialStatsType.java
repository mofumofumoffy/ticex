package moffy.ticex.lib;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import moffy.ticex.TicEX;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ArmorItem;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

public record CatalystMaterialStatsType(MaterialStatType<?> getType) implements IMaterialStats {
    private static final RecordLoadable<CatalystMaterialStatsType> LOADABLE;
    private static final List<Component> DESCRIPTION;

    private static final HashMap<String, MaterialStatType<CatalystMaterialStatsType>> TYPES;

    public CatalystMaterialStatsType(MaterialStatType<?> getType) {
        this.getType = getType;
    }
    public static MaterialStatType<CatalystMaterialStatsType> getOrMakeType(String id) {
        if (TYPES.containsKey(id)) {
            return TYPES.get(id);
        } else {
            MaterialStatsId statsId = new MaterialStatsId(TicEX.MODID, id);
            MaterialStatType<CatalystMaterialStatsType> catalystStatType = new MaterialStatType<
                CatalystMaterialStatsType
            >(
                statsId,
                type -> {
                    return new CatalystMaterialStatsType(type);
                },
                LOADABLE
            );

            TYPES.put(id, catalystStatType);

            return catalystStatType;
        }
    }

    public static MaterialStatType<CatalystMaterialStatsType> getOrMakeType(String prefix, ArmorItem.Type armorType) {
        String id = prefix + "_" + armorType.getName();
        if (TYPES.containsKey(id)) {
            return TYPES.get(id);
        } else {
            MaterialStatsId statsId = new MaterialStatsId(TicEX.MODID, id);
            MaterialStatType<CatalystMaterialStatsType> catalystStatType = new MaterialStatType<
                CatalystMaterialStatsType
            >(
                statsId,
                type -> {
                    return new CatalystMaterialStatsType(type);
                },
                LOADABLE
            );

            TYPES.put(id, catalystStatType);

            return catalystStatType;
        }
    }

    public static void RegisterStats() {
        for (MaterialStatType<CatalystMaterialStatsType> catalystStatType : TYPES.values()) {
            MaterialRegistry.getInstance().registerStatType(catalystStatType);
        }
    }

    public static Collection<MaterialStatType<CatalystMaterialStatsType>> getAllCatalystStats() {
        return TYPES.values();
    }

    @Override
    public void apply(ModifierStatsBuilder builder, float scale) {}

    @Override
    public List<Component> getLocalizedDescriptions() {
        return DESCRIPTION;
    }

    @Override
    public List<Component> getLocalizedInfo() {
        return List.of(IMaterialStats.makeTooltip(TConstruct.getResource("extra.no_stats")));
    }

    @Override
    public MutableComponent getLocalizedName() {
        return IMaterialStats.super.getLocalizedName().withStyle(ChatFormatting.AQUA);
    }

    static {
        LOADABLE = RecordLoadable.create(MaterialStatType.CONTEXT_KEY.requiredField(), CatalystMaterialStatsType::new);
        DESCRIPTION = List.of(Component.empty());
        TYPES = new HashMap<>();
    }


}
