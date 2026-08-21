package moffy.ticex.datagen.tool;

import static slimeknights.tconstruct.library.materials.MaterialRegistry.ARMOR;
import static slimeknights.tconstruct.library.materials.MaterialRegistry.MELEE_HARVEST;

import moffy.ticex.lib.TicEXMaterials;
import moffy.ticex.registry.TicEXModifiers;
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
                TicEXModifiers.SOUL_RENDING_MODIFIER,
                TicEXModifiers.DRAGON_FORCE_MODIFIER,
                TinkerModifiers.lacerating
        );
        addTraits(
                TicEXMaterials.WYVERN,
                MELEE_HARVEST,
                new ModifierEntry(TicEXModifiers.SOUL_RENDING_MODIFIER, 1),
                new ModifierEntry(TicEXModifiers.DRAGON_FORCE_MODIFIER, 2),
                new ModifierEntry(TinkerModifiers.lacerating, 1)
        );
        addTraits(
                TicEXMaterials.DRACONIC,
                MELEE_HARVEST,
                new ModifierEntry(TicEXModifiers.SOUL_RENDING_MODIFIER, 2),
                new ModifierEntry(TicEXModifiers.DRAGON_FORCE_MODIFIER, 3),
                new ModifierEntry(TinkerModifiers.lacerating, 2)
        );
        addTraits(
                TicEXMaterials.CHAOTIC,
                MELEE_HARVEST,
                new ModifierEntry(TicEXModifiers.SOUL_RENDING_MODIFIER, 3),
                new ModifierEntry(TicEXModifiers.DRAGON_FORCE_MODIFIER, 4),
                new ModifierEntry(TinkerModifiers.lacerating, 3)
        );

        addDefaultTraits(
                TicEXMaterials.INFINITY,
                TicEXModifiers.OMNIPOTENCE_MODIFIER,
                TicEXModifiers.COSMIC_LUCK_MODIFIER,
                TicEXModifiers.COSMIC_UNBREAKABLE_MODIFIER,
                TicEXModifiers.BEDROCK_BREAKER_MODIFIER
        );
        addTraits(
                TicEXMaterials.INFINITY,
                ARMOR,
                TicEXModifiers.TRANSCENDENTAL_MODIFIER,
                TicEXModifiers.COSMIC_UNBREAKABLE_MODIFIER
        );
        addTraits(TicEXMaterials.NEUTRON, ARMOR, TicEXModifiers.CONDENSING_MODIFIER, TicEXModifiers.DENSE_MODIFIER);
        addDefaultTraits(TicEXMaterials.CRYSTAL_MATRIX, TicEXModifiers.AFTERSHOCK_MODIFIER, TinkerModifiers.insatiable, TicEXModifiers.BEDROCK_BREAKER_MODIFIER);
        addDefaultTraits(TicEXMaterials.BLAZING, TicEXModifiers.SKULLFIRE_MODIFIER, TicEXModifiers.BLAZING_FLAME_MODIFIER, TicEXModifiers.BLAZING_FORTUNE_MODIFIER);


        addDefaultTraits(TicEXMaterials.ETHERIC, TicEXModifiers.SASSY_MODIFIER, TicEXModifiers.DEFLECTION_MODIFIER);
        addDefaultTraits(TicEXMaterials.OD, TicEXModifiers.AFLOAT_MODIFIER, TicEXModifiers.DUNGEON_MASTER_MODIFIER, TicEXModifiers.UNRAVEL_MODIFIER);
        addDefaultTraits(TicEXMaterials.ASTRAL, TicEXModifiers.TELESCOPE_MODIFIER);
        addTraits(TicEXMaterials.ASTRAL, MELEE_HARVEST, TicEXModifiers.TELESCOPE_MODIFIER, TicEXModifiers.PLANETARIUM_MODIFIER);

        addDefaultTraits(TicEXMaterials.RECONSTRUCTION, TicEXModifiers.REBIRTH_MODIFIER);
        addTraits(TicEXMaterials.RECONSTRUCTION, ARMOR, TicEXModifiers.REBIRTH_MODIFIER);
    }
}
