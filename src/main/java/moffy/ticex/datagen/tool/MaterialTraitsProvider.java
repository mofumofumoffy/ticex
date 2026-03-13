package moffy.ticex.datagen.tool;

import static slimeknights.tconstruct.library.materials.MaterialRegistry.ARMOR;
import static slimeknights.tconstruct.library.materials.MaterialRegistry.MELEE_HARVEST;

import moffy.ticex.lib.TicEXMaterials;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.PackOutput;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;
import slimeknights.tconstruct.library.data.material.AbstractMaterialTraitDataProvider;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class MaterialTraitsProvider extends AbstractMaterialTraitDataProvider {

    public MaterialTraitsProvider(PackOutput packOutput, AbstractMaterialDataProvider materials) {
        super(packOutput, materials);
    }

    @Override
    public String getName() {
        return "TiCEX Material Traits";
    }

    @Override
    protected void addMaterialTraits() {
        addDefaultTraits(TicEXMaterials.DRACONIUM, TinkerModifiers.lacerating);
        addDefaultTraits(TicEXMaterials.WYVERN, TinkerModifiers.lacerating);
        addDefaultTraits(TicEXMaterials.DRACONIC, TinkerModifiers.lacerating);
        addDefaultTraits(TicEXMaterials.CHAOTIC, TinkerModifiers.lacerating);
        addTraits(
                TicEXMaterials.DRACONIUM,
                MELEE_HARVEST,
                TicEXRegistry.SOUL_RENDING_MODIFIER,
                TinkerModifiers.lacerating
        );
        addTraits(
                TicEXMaterials.WYVERN,
                MELEE_HARVEST,
                TicEXRegistry.SOUL_RENDING_MODIFIER,
                TinkerModifiers.lacerating
        );
        addTraits(
                TicEXMaterials.DRACONIC,
                MELEE_HARVEST,
                new ModifierEntry(TicEXRegistry.SOUL_RENDING_MODIFIER, 2),
                new ModifierEntry(TinkerModifiers.lacerating, 2)
        );
        addTraits(
                TicEXMaterials.CHAOTIC,
                MELEE_HARVEST,
                new ModifierEntry(TicEXRegistry.SOUL_RENDING_MODIFIER, 3),
                new ModifierEntry(TinkerModifiers.lacerating, 3)
        );

        addDefaultTraits(
                TicEXMaterials.INFINITY,
                TicEXRegistry.OMNIPOTENCE_MODIFIER,
                TicEXRegistry.COSMIC_LUCK_MODIFIER,
                TicEXRegistry.COSMIC_UNBREAKABLE_MODIFIER,
                TicEXRegistry.BEDROCK_BREAKER_MODIFIER
        );
        addDefaultTraits(TicEXMaterials.CRYSTAL_MATRIX, TicEXRegistry.AFTERSHOCK_MODIFIER, TinkerModifiers.insatiable, TicEXRegistry.BEDROCK_BREAKER_MODIFIER);
        addTraits(
                TicEXMaterials.INFINITY,
                ARMOR,
                TicEXRegistry.TRANSCENDENTAL_MODIFIER,
                TicEXRegistry.COSMIC_UNBREAKABLE_MODIFIER
        );
        addTraits(TicEXMaterials.NEUTRON, ARMOR, TicEXRegistry.CONDENSING_MODIFIER, TicEXRegistry.DENSE_MODIFIER);
        addDefaultTraits(TicEXMaterials.BLAZING, TicEXRegistry.SKULLFIRE_MODIFIER, TicEXRegistry.BLAZING_FLAME_MODIFIER, TicEXRegistry.BLAZING_FORTUNE_MODIFIER);


        addDefaultTraits(TicEXMaterials.ETHERIC, TicEXRegistry.SASSY_MODIFIER, TicEXRegistry.DEFLECTION_MODIFIER);
        addDefaultTraits(TicEXMaterials.OD, TicEXRegistry.AFLOAT_MODIFIER, TicEXRegistry.DUNGEON_MASTER_MODIFIER, TicEXRegistry.UNRAVEL_MODIFIER);
        addDefaultTraits(TicEXMaterials.RECONSTRUCTION, TicEXRegistry.REBIRTH_MODIFIER);
        addTraits(TicEXMaterials.RECONSTRUCTION, ARMOR, TicEXRegistry.REBIRTH_MODIFIER);
    }
}
