package moffy.ticex.lib;

import moffy.ticex.TicEX;
import slimeknights.tconstruct.library.materials.definition.MaterialId;

import java.util.Objects;

public class TicEXMaterials {

    public static final MaterialId INFINITY = Objects.requireNonNull(MaterialId.tryBuild(TicEX.MODID, "infinity"));
    public static final MaterialId NEUTRON = Objects.requireNonNull(MaterialId.tryBuild(TicEX.MODID, "neutron"));
    public static final MaterialId CRYSTAL_MATRIX = Objects.requireNonNull(MaterialId.tryBuild(TicEX.MODID, "crystal_matrix"));
    public static final MaterialId BLAZING = Objects.requireNonNull(MaterialId.tryBuild(TicEX.MODID, "blazing"));
    public static final MaterialId DRACONIUM = Objects.requireNonNull(MaterialId.tryBuild(TicEX.MODID, "draconium"));
    public static final MaterialId WYVERN = Objects.requireNonNull(MaterialId.tryBuild(TicEX.MODID, "wyvern"));
    public static final MaterialId DRACONIC = Objects.requireNonNull(MaterialId.tryBuild(TicEX.MODID, "draconic"));
    public static final MaterialId CHAOTIC = Objects.requireNonNull(MaterialId.tryBuild(TicEX.MODID, "chaotic"));
    public static final MaterialId ETHERIC = Objects.requireNonNull(MaterialId.tryBuild(TicEX.MODID, "etheric"));
    public static final MaterialId RECONSTRUCTION = Objects.requireNonNull(MaterialId.tryBuild(TicEX.MODID, "reconstruction"));
    public static final MaterialId[] TRIM_MATERIALS = new MaterialId[] {
            ETHERIC,
            INFINITY,

            DRACONIUM,
            WYVERN,
            DRACONIC,
            CHAOTIC
    };
}
