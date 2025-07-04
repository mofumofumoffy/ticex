package moffy.ticex.datagen.general.tag;

import java.util.concurrent.CompletableFuture;
import moffy.ticex.TicEX;
import moffy.ticex.lib.TicEXTags;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.recipe.CostTagAppender;

import static slimeknights.tconstruct.common.TinkerTags.Items.*;

public class ItemTagProvider extends ItemTagsProvider {

    /*
     * sample link: https://github.com/SlimeKnights/TinkersConstruct/blob/1.20.1/src/main/java/slimeknights/tconstruct/common/data/tags/ItemTagProvider.java
     */

    public ItemTagProvider(
        PackOutput pOutput,
        CompletableFuture<Provider> pLookupProvider,
        CompletableFuture<TagLookup<Block>> pBlockTags,
        @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(pOutput, pLookupProvider, pBlockTags, TicEX.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(Provider pProvider) {
        this.addCommon();
        //this.addWorld();
        this.addSmeltery();
        this.addTools();
    }

    private void addCommon() {
        addCores(
            new ResourceLocation(TicEX.MODID, "reconstruction_core"),
            new ResourceLocation(TicEX.MODID, "flickering_reconstruction_core"),
            new ResourceLocation(TicEX.MODID, "celestial_core"),
            new ResourceLocation(TicEX.MODID, "radiation_shelding_core"),
            new ResourceLocation(TicEX.MODID, "draconium_evolved_core"),
            new ResourceLocation(TicEX.MODID, "wyvern_evolved_core"),
            new ResourceLocation(TicEX.MODID, "draconic_evolved_core"),
            new ResourceLocation(TicEX.MODID, "chaotic_evolved_core"),
            new ResourceLocation(TicEX.MODID, "inject_core"),
            new ResourceLocation(TicEX.MODID, "konpaku_core"),
            new ResourceLocation(TicEX.MODID, "koshirae_core"),
            new ResourceLocation(TicEX.MODID, "lamellar_core"),
            new ResourceLocation(TicEX.MODID, "overload_core"),
            new ResourceLocation(TicEX.MODID, "override_core"),
            new ResourceLocation(TicEX.MODID, "incomparable_core"),
            new ResourceLocation(TicEX.MODID, "cardboard_core")
        );

        //ingots
        addOptional(TicEXTags.Items.INFINITY_INGOT, new ResourceLocation("avaritia", "infinity_ingot"));
        addOptional(TicEXTags.Items.NEUTRON_INGOT, new ResourceLocation("avaritia", "neutron_ingot"));
        addOptional(TicEXTags.Items.CRYSTAL_MATRIX_INGOT, new ResourceLocation("avaritia", "crystal_matrix_ingot"));

        addOptional(TicEXTags.Items.ETHERIC_INGOT, new ResourceLocation(TicEX.MODID, "etheric_ingot"));

        //blocks
        addOptional(TicEXTags.Items.INFINITY_BLOCK, new ResourceLocation("avaritia", "infinity_block"));
        addOptional(TicEXTags.Items.NEUTRON_BLOCK, new ResourceLocation("avaritia", "neutron_block"));
        addOptional(TicEXTags.Items.CRYSTAL_MATRIX_BLOCK, new ResourceLocation("avaritia", "crystal_matrix_block"));

        addOptional(TicEXTags.Items.ETHERIC_BLOCK, new ResourceLocation(TicEX.MODID, "etheric_block"));

        //catalyst_tools
        addCatalysts(
            new ResourceLocation(TicEX.MODID, "catalyst_slashblade"),
            new ResourceLocation(TicEX.MODID, "catalyst_kinetic_gun"),
            new ResourceLocation(TicEX.MODID, "catalyst_irons_spellbook"),
            new ResourceLocation(TicEX.MODID, "catalyst_meka_tool")
        );

        //catalyst_armors
        for (ArmorItem.Type type : ArmorItem.Type.values()) {
            addCatalysts(
                new ResourceLocation(TicEX.MODID, "catalyst_mekasuit").withSuffix("_" + type.getName()),
                new ResourceLocation(TicEX.MODID, "catalyst_gem").withSuffix("_" + type.getName())
            );
        }

        addOptional(TinkerTags.Items.TOOL_PARTS, new ResourceLocation(TicEX.MODID, "slashblade_blade"));
        addOptional(TinkerTags.Items.TOOL_PARTS, new ResourceLocation(TicEX.MODID, "slashblade_saya"));
    }

    private void addSmeltery() {
        this.tag(TinkerTags.Items.SEARED_TANKS).add(
                TicEXRegistry.SEARED_RF_FURNACE.get().asItem(),
                TicEXRegistry.CREATIVE_SEARED_RF_FURNACE.get().asItem()
            );
        this.tag(TinkerTags.Items.SCORCHED_TANKS).add(
                TicEXRegistry.SCORCHED_RF_FURNACE.get().asItem(),
                TicEXRegistry.CREATIVE_SCORCHED_RF_FURNACE.get().asItem()
            );
    }

    @SuppressWarnings("unchecked")
    private void addTools() {
        //tools
        addToolTags(
            new ResourceLocation(TicEX.MODID, "reforged_slashblade"),
            TicEXTags.Items.SLASHBLADE_TOOL,
            MULTIPART_TOOL,
            DURABILITY,
            HARVEST,
            MELEE_PRIMARY,
            INTERACTABLE_RIGHT,
            PARRY,
            SMALL_TOOLS,
            BONUS_SLOTS,
            ItemTags.SWORDS,
            UNSALVAGABLE
        );
        addToolTags(
            new ResourceLocation(TicEX.MODID, "blitz_gun"),
            TicEXTags.Items.KINETIC_GUN_TOOL,
            MULTIPART_TOOL,
            DURABILITY,
            HARVEST,
            MELEE_PRIMARY,
            INTERACTABLE_RIGHT,
            PARRY,
            SMALL_TOOLS,
            BONUS_SLOTS,
            UNSALVAGABLE
        );
        addToolTags(
            new ResourceLocation(TicEX.MODID, "revival_spellbook_irons"),
            TicEXTags.Items.IRONS_SPELLBOOK_TOOL,
            MULTIPART_TOOL,
            DURABILITY,
            HARVEST,
            MELEE_PRIMARY,
            INTERACTABLE_RIGHT,
            PARRY,
            SMALL_TOOLS,
            BONUS_SLOTS,
            UNSALVAGABLE
        );
        addToolTags(
                new ResourceLocation(TicEX.MODID, "meka_tool"),
                TicEXTags.Items.MEKA_TOOL,
                MULTIPART_TOOL,
                MELEE_WEAPON,
                HARVEST,
                BONUS_SLOTS,
                DURABILITY
        );

        //armors
        addArmorTags(new ResourceLocation(TConstruct.MOD_ID, "plate"), TicEXTags.Items.PLATE);
        addArmorTags(
            new ResourceLocation(TicEX.MODID, "mekaplate"),
            TicEXTags.Items.MEKASUIT_ARMOR,
            MULTIPART_TOOL,
            DURABILITY,
            BONUS_SLOTS,
            TRIM
        );
        addArmorTags(
            new ResourceLocation(TicEX.MODID, "singular_gem"),
            TicEXTags.Items.GEM_ARMOR,
            MULTIPART_TOOL,
            DURABILITY,
            BONUS_SLOTS,
            TRIM
        );

        this.tag(TicEXTags.Items.SERAM).addTags(
                TicEXTags.Items.SLASHBLADE_TOOL,
                TicEXTags.Items.KINETIC_GUN_TOOL,
                TicEXTags.Items.IRONS_SPELLBOOK_TOOL,
                TicEXTags.Items.MEKASUIT_ARMOR,
                TicEXTags.Items.GEM_ARMOR,
                TicEXTags.Items.MEKA_TOOL
            );
    }

    private void addTag(TagKey<Item> tagKey, ResourceLocation coreItem) {
        this.tag(tagKey).addOptional(coreItem);
    }

    @SafeVarargs
    private void addCores(ResourceLocation... coreItems) {
        for (ResourceLocation coreItem : coreItems) {
            if (coreItem != null) {
                addTag(TicEXTags.Items.CORES, coreItem);
            }
        }
    }

    @SafeVarargs
    private void addCatalysts(ResourceLocation... catalystItems) {
        for (ResourceLocation catalystItem : catalystItems) {
            if (catalystItem != null) {
                this.tag(TicEXTags.Items.CATALYSTS).addOptional(catalystItem);
                this.tag(TinkerTags.Items.TOOL_PARTS).addOptional(catalystItem);
            }
        }
    }

    @SafeVarargs
    private void addToolTags(ResourceLocation tool, TagKey<Item>... tags) {
        if (tool != null) {
            for (TagKey<Item> tag : tags) {
                this.tag(tag).addOptional(tool);
            }
        }
    }

    private TagKey<Item> getArmorTag(ArmorItem.Type slotType) {
        return switch (slotType) {
            case BOOTS -> BOOTS;
            case LEGGINGS -> LEGGINGS;
            case CHESTPLATE -> CHESTPLATES;
            case HELMET -> HELMETS;
        };
    }

    private TagKey<Item> getForgeArmorTag(ArmorItem.Type slotType) {
        return switch (slotType) {
            case BOOTS -> Tags.Items.ARMORS_BOOTS;
            case LEGGINGS -> Tags.Items.ARMORS_LEGGINGS;
            case CHESTPLATE -> Tags.Items.ARMORS_CHESTPLATES;
            case HELMET -> Tags.Items.ARMORS_HELMETS;
        };
    }

    @SafeVarargs
    private void addArmorTags(ResourceLocation armor, TagKey<Item>... tags) {
        if (armor != null) {
            for (ArmorItem.Type type : ArmorItem.Type.values()) {
                for (TagKey<Item> tag : tags) {
                    this.tag(tag).addOptional(armor.withSuffix("_" + type.getName()));
                }
                this.tag(getArmorTag(type)).addOptional(armor.withSuffix("_" + type.getName()));
                this.tag(getForgeArmorTag(type)).addOptional(armor.withSuffix("_" + type.getName()));
            }
        }
    }

    protected CostTagAppender moltenTools(FluidObject<?> fluid) {
        return CostTagAppender.moltenToolMelting(fluid, tag -> tag(ItemTags.create(tag)));
    }

    private void addOptional(TagKey<Item> tagkey, ResourceLocation id) {
        this.tag(tagkey).addOptional(id);
    }

    @Override
    public String getName() {
        return "TiCEX Item Tags";
    }
}
