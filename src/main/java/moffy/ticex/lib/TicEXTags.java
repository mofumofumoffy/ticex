package moffy.ticex.lib;

import moffy.ticex.TicEX;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierManager;

import static slimeknights.mantle.Mantle.commonResource;

public class TicEXTags {

    public static class Blocks {

        public static final TagKey<Block> INFINITY = common("storage_blocks/infinity");
        public static final TagKey<Block> NEUTRON = common("storage_blocks/neutron");
        public static final TagKey<Block> CRYSTAL_MATRIX = common("storage_blocks/crystal_matrix");
        public static final TagKey<Block> ETHERIC = common("storage_blocks/etheric");

        private static TagKey<Block> common(String name) {
            return TagKey.create(Registries.BLOCK, commonResource(name));
        }
    }

    public static class Items {

        public static final TagKey<Item> CORES = local("cores");
        public static final TagKey<Item> CATALYSTS = local("catalysts");

        public static final TagKey<Item> INFINITY_INGOT = common("ingots/infinity");
        public static final TagKey<Item> NEUTRON_INGOT = common("ingots/neutron");
        public static final TagKey<Item> CRYSTAL_MATRIX_INGOT = common("ingots/crystal_matrix");
        public static final TagKey<Item> ETHERIC_INGOT = common("ingots/etheric");

        public static final TagKey<Item> INFINITY_BLOCK = common("storage_blocks/infinity");
        public static final TagKey<Item> NEUTRON_BLOCK = common("storage_blocks/neutron");
        public static final TagKey<Item> CRYSTAL_MATRIX_BLOCK = common("storage_blocks/crystal_matrix");
        public static final TagKey<Item> ETHERIC_BLOCK = common("storage_blocks/etheric");

        public static final TagKey<Item> SERAM = local("seram");
        public static final TagKey<Item> PLATE = local("plate");
        public static final TagKey<Item> MEKASUIT_ARMOR = local("seram/mekasuit");
        public static final TagKey<Item> GEM_ARMOR = local("seram/gem");
        public static final TagKey<Item> SLASHBLADE_TOOL = local("seram/slashblade");
        public static final TagKey<Item> KINETIC_GUN_TOOL = local("seram/kinetic_gun");
        public static final TagKey<Item> IRONS_SPELLBOOK_TOOL = local("seram/irons_spellbook");
        public static final TagKey<Item> MEKA_TOOL = local("seram/meka_tool");

        private static TagKey<Item> local(String name) {
            return TagKey.create(Registries.ITEM, getResource(name));
        }

        private static TagKey<Item> common(String name) {
            return TagKey.create(Registries.ITEM, commonResource(name));
        }
    }

    public static class Fluids {

        public static final TagKey<Fluid> INFINITY = common("molten_infinity");
        public static final TagKey<Fluid> NEUTRON = common("molten_neutron");
        public static final TagKey<Fluid> CRYSTAL_MATRIX = common("molten_crystal_matrix");
        public static final TagKey<Fluid> ETHERIC = common("molten_etheric");
        public static final TagKey<Fluid> HEPATIZON = common("molten_hepatizon");
        public static final TagKey<Fluid> GOLD = common("molten_gold");

        public static final TagKey<Fluid> RECONSTRUCTION_CORE = common("molten_reconstruction_core");

        private static TagKey<Fluid> common(String name) {
            return TagKey.create(Registries.FLUID, commonResource(name));
        }
    }

    public static class Modifiers {
        public static final TagKey<Modifier> REMOVAL_BLACKLIST = local("removal_blacklist");

        private static TagKey<Modifier> local(String name) {
            return ModifierManager.getTag(getResource(name));
        }
    }

    public static ResourceLocation getResource(String name) {
        return new ResourceLocation(TicEX.MODID, name);
    }
}
