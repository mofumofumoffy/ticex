package moffy.ticex.datagen.general.tag;

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
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.library.data.recipe.CostTagAppender;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.tags.ItemTags.TRIM_MATERIALS;
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
    protected void addTags(@NotNull Provider pProvider) {
        this.addCommon();
        //this.addWorld();
        this.addSmeltery();
        this.addTools();
    }

    private void addCommon() {
        addCores(
                TicEX.getResource("reconstruction_core"),
                TicEX.getResource("flickering_reconstruction_core"),
                TicEX.getResource("celestial_core"),
                TicEX.getResource("radiation_shelding_core"),
                TicEX.getResource("draconium_evolved_core"),
                TicEX.getResource("wyvern_evolved_core"),
                TicEX.getResource("draconic_evolved_core"),
                TicEX.getResource("chaotic_evolved_core"),
                TicEX.getResource("inject_core"),
                TicEX.getResource("konpaku_core"),
                TicEX.getResource("koshirae_core"),
                TicEX.getResource("lamellar_core"),
                TicEX.getResource("overload_core"),
                TicEX.getResource("override_core"),
                TicEX.getResource("incomparable_core"),
                TicEX.getResource("cardboard_core")
        );

        //ingots
        addOptional(TicEXTags.Items.INFINITY_INGOT, ResourceLocation.fromNamespaceAndPath("avaritia", "infinity_ingot"));
        addOptional(TicEXTags.Items.NEUTRON_INGOT, ResourceLocation.fromNamespaceAndPath("avaritia", "neutron_ingot"));
        addOptional(TicEXTags.Items.CRYSTAL_MATRIX_INGOT, ResourceLocation.fromNamespaceAndPath("avaritia", "crystal_matrix_ingot"));
        addOptional(TicEXTags.Items.BLAZING_INGOT, ResourceLocation.fromNamespaceAndPath("avaritia", "blaze_cube"));

        addOptional(TicEXTags.Items.ETHERIC_INGOT, TicEX.getResource("etheric_ingot"));

        //blocks
        addOptional(TicEXTags.Items.INFINITY_BLOCK, ResourceLocation.fromNamespaceAndPath("avaritia", "infinity"));
        addOptional(TicEXTags.Items.NEUTRON_BLOCK, ResourceLocation.fromNamespaceAndPath("avaritia", "neutron"));
        addOptional(TicEXTags.Items.CRYSTAL_MATRIX_BLOCK, ResourceLocation.fromNamespaceAndPath("avaritia", "crystal_matrix"));
        addOptional(TicEXTags.Items.BLAZING_BLOCK, ResourceLocation.fromNamespaceAndPath("avaritia", "blaze_cube_block"));

        addOptional(TicEXTags.Items.ETHERIC_BLOCK, TicEX.getResource("etheric_block"));

        //trim_materials
        tag(TRIM_MATERIALS)
                .addOptional(ResourceLocation.fromNamespaceAndPath("avaritia", "infinity_ingot"))
                .addOptional(TicEXRegistry.DRACONIUM_CRYSTAL.getId())
                .addOptional(TicEXRegistry.WYVERN_CRYSTAL.getId())
                .addOptional(TicEXRegistry.DRACONIC_CRYSTAL.getId())
                .addOptional(TicEXRegistry.CHAOTIC_CRYSTAL.getId())
                .addOptional(TicEXRegistry.ETHERIC_INGOT.getId());

        //catalyst_tools
        addCatalysts(
                TicEX.getResource("catalyst_slashblade"),
                TicEX.getResource("catalyst_kinetic_gun"),
                TicEX.getResource("catalyst_irons_spellbook"),
                TicEX.getResource("catalyst_meka_tool")
        );

        //catalyst_armors
        for (ArmorItem.Type type : ArmorItem.Type.values()) {
            addCatalysts(
                    TicEX.getResource("catalyst_mekasuit").withSuffix("_" + type.getName()),
                    TicEX.getResource("catalyst_gem").withSuffix("_" + type.getName())
            );
        }

        addOptional(TinkerTags.Items.TOOL_PARTS, TicEX.getResource("slashblade_blade"));
        addOptional(TinkerTags.Items.TOOL_PARTS, TicEX.getResource("slashblade_saya"));

        // other mods

        if (ModList.get().isLoaded("slashblade")) {
            this.tag(TicEXTags.Items.SLASHBLADE)
                    .addOptional(ResourceLocation.fromNamespaceAndPath("slashblade", "slashblade"))
                    .addOptional(ResourceLocation.fromNamespaceAndPath("slashblade_addon", "slashblade_tofu_diamond"))
                    .addOptional(ResourceLocation.fromNamespaceAndPath("slashblade_addon", "slashblade_tofu_metal"))
                    .addOptional(ResourceLocation.fromNamespaceAndPath("yakumoblade", "slashblade"))
                    .addOptional(ResourceLocation.fromNamespaceAndPath("fantasy_ending", "fantasy_ending_blade"))
                    .addOptional(ResourceLocation.fromNamespaceAndPath("energyblade", "forge_energy_blade"));
        }
    }

    @SuppressWarnings("unchecked")
    private void addSmeltery() {
        this.tag(TinkerTags.Items.SEARED_TANKS).add(
                TicEXRegistry.SEARED_RF_FURNACE.get().asItem(),
                TicEXRegistry.CREATIVE_SEARED_RF_FURNACE.get().asItem()
        );
        this.tag(TinkerTags.Items.SCORCHED_TANKS).add(
                TicEXRegistry.SCORCHED_RF_FURNACE.get().asItem(),
                TicEXRegistry.CREATIVE_SCORCHED_RF_FURNACE.get().asItem()
        );

        this.tag(TinkerTags.Items.CASTS)
                .addOptionalTags(TinkerTags.Items.GOLD_CASTS, TinkerTags.Items.SAND_CASTS, TinkerTags.Items.RED_SAND_CASTS, TinkerTags.Items.TABLE_EMPTY_CASTS, TinkerTags.Items.BASIN_EMPTY_CASTS);

        // other mods

        if (ModList.get().isLoaded("slashblade")) {
            addCast(TicEXRegistry.SLASHBLADE_SAYA_CAST);
            addCast(TicEXRegistry.SLASHBLADE_BLADE_CAST);
        }
    }

    public void addCast(CastItemObject cast) {
        this.tag(TinkerTags.Items.GOLD_CASTS).addOptional(cast.getName().withSuffix("_cast"));
        this.tag(TinkerTags.Items.SAND_CASTS).addOptional(cast.getName().withSuffix("_sand_cast"));
        this.tag(TinkerTags.Items.RED_SAND_CASTS).addOptional(cast.getName().withSuffix("_red_sand_cast"));
        this.tag(SINGLE_USE_CASTS).addTag(cast.getSingleUseTag());
        this.tag(cast.getSingleUseTag()).addOptional(cast.getName().withSuffix("_sand_cast")).addOptional(cast.getName().withSuffix("_red_sand_cast"));
        this.tag(MULTI_USE_CASTS).addTag(cast.getMultiUseTag());
        this.tag(cast.getMultiUseTag()).addOptional(cast.getName().withSuffix("_cast"));

    }

    @SuppressWarnings("unchecked")
    private void addTools() {
        //tools
        addToolTags(
                TicEX.getResource("reforged_slashblade"),
                TicEXTags.Items.REFORGED_SLASHBLADE_TOOL,
                MULTIPART_TOOL,
                DURABILITY,
                HARVEST,
                MELEE_PRIMARY,
                INTERACTABLE_RIGHT,
                PARRY,
                SMALL_TOOLS,
                BONUS_SLOTS,
                LONGBOWS,
                ItemTags.SWORDS,
                UNSALVAGABLE
        );
        addToolTags(
                TicEX.getResource("blitz_gun"),
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
                TicEX.getResource("revival_spellbook_irons"),
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
                TicEX.getResource("meka_tool"),
                TicEXTags.Items.MEKA_TOOL,
                MULTIPART_TOOL,
                MELEE_WEAPON,
                HARVEST,
                BONUS_SLOTS,
                DURABILITY
        );

        //armors
        addArmorTags(ResourceLocation.fromNamespaceAndPath(TConstruct.MOD_ID, "plate"), TicEXTags.Items.PLATE);
        addArmorTags(
                TicEX.getResource("mekaplate"),
                TicEXTags.Items.MEKASUIT_ARMOR,
                MULTIPART_TOOL,
                DURABILITY,
                BONUS_SLOTS,
                TRIM
        );
        addArmorTags(
                TicEX.getResource("singular_gem"),
                TicEXTags.Items.GEM_ARMOR,
                MULTIPART_TOOL,
                DURABILITY,
                BONUS_SLOTS,
                TRIM
        );

        this.tag(TicEXTags.Items.SERAM).addTags(
                TicEXTags.Items.REFORGED_SLASHBLADE_TOOL,
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

    private void addCores(ResourceLocation... coreItems) {
        for (ResourceLocation coreItem : coreItems) {
            if (coreItem != null) {
                addTag(TicEXTags.Items.CORES, coreItem);
            }
        }
    }

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
    public @NotNull String getName() {
        return "TiCEX Item Tags";
    }
}
