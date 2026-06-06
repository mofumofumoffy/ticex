package moffy.ticex.modules.general;

import moffy.ticex.TicEX;
import moffy.ticex.block.furnace.entity.RFFurnaceBlockEntity;
import moffy.ticex.block.transmuter.container.FluidTransmuterContainerMenu;
import moffy.ticex.block.transmuter.entity.FluidTransmuterBlockEntity;
import moffy.ticex.entity.avaritia.EndestShotProjectile;
import moffy.ticex.entity.curios.ResonanceToolProjectile;
import moffy.ticex.entity.mekanism.MekanicProjectile;
import moffy.ticex.entity.slashblade.SBToolItemEntity;
import moffy.ticex.lib.hook.*;
import moffy.ticex.lib.recipe.*;
import moffy.ticex.lib.registry.TicEXItemDeferredRegisterExtension;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.fluid.UnplaceableFluid;
import slimeknights.mantle.recipe.helper.TypeAwareRecipeSerializer;
import slimeknights.mantle.registration.deferred.FluidDeferredRegister;
import slimeknights.mantle.registration.deferred.MenuTypeDeferredRegister;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.FlowingFluidObject;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.util.DynamicModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.smeltery.block.component.SearedBlock;
import slimeknights.tconstruct.tools.menu.ToolContainerMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public class TicEXRegistry {

    public static final BlockBehaviour.Properties SEARED;

    static {
        IntFunction<BlockBehaviour.Properties> solidProps = factor ->
                builder(MapColor.COLOR_GRAY, SoundType.METAL)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops()
                        .strength(3.0F * factor, 9.0F * factor)
                        .isValidSpawn(
                                (s, r, p, e) -> !s.hasProperty(SearedBlock.IN_STRUCTURE) || !s.getValue(SearedBlock.IN_STRUCTURE)
                        );
        SEARED = solidProps.apply(1);
    }

    public static final BlockBehaviour.Properties SCORCHED;

    static {
        IntFunction<BlockBehaviour.Properties> solidProps = factor ->
                builder(MapColor.TERRACOTTA_BROWN, SoundType.BASALT)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops()
                        .strength(2.5F * factor, 8.0F * factor)
                        .isValidSpawn(
                                (s, r, p, e) -> !s.hasProperty(SearedBlock.IN_STRUCTURE) || !s.getValue(SearedBlock.IN_STRUCTURE)
                        );
        SCORCHED = solidProps.apply(1);
    }

    public static final TagKey<Item> KEY_MODIFIER_UNSTABLE = TagKey.create(
            Registries.ITEM,
            TicEX.getResource("shader/unstable_modifier")
    );

    public static final ModifiableArmorMaterial MEKAPLATE_DEFINITION = ModifiableArmorMaterial.create(
            TicEX.getResource("mekaplate"),
            SoundEvents.ARMOR_EQUIP_NETHERITE
    );
    public static final ModifiableArmorMaterial SINGULAR_GEM_DEFINITION = ModifiableArmorMaterial.create(
            TicEX.getResource("singular_gem"),
            SoundEvents.ARMOR_EQUIP_NETHERITE
    );
    public static final ToolDefinition SLASHBLADE_DEFINITION = ToolDefinition.create(
            TicEX.getResource("reforged_slashblade")
    );
    public static final ToolDefinition GUN_DEFINITION = ToolDefinition.create(
            TicEX.getResource("blitz_gun")
    );
    public static final ToolDefinition SPELLBOOK_DEFINITION = ToolDefinition.create(
            TicEX.getResource("revival_spellbook")
    );
    public static final ToolDefinition MEKA_TOOL_DEFINITION = ToolDefinition.create(
            TicEX.getResource("meka_edge")
    );
    public static final ToolDefinition GAUNTLET_DEFINITION = ToolDefinition.create(
            TicEX.getResource("resonance_gauntlet")
    );

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TicEX.MODID);
    public static final TicEXItemDeferredRegisterExtension ITEMS_EXTENDED = new TicEXItemDeferredRegisterExtension(
            ITEMS,
            TicEX.MODID
    );
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TicEX.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(
            ForgeRegistries.BLOCK_ENTITY_TYPES,
            TicEX.MODID
    );
    public static final FluidDeferredRegister FLUIDS = new FluidDeferredRegister(TicEX.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(
            ForgeRegistries.ENTITY_TYPES,
            TicEX.MODID
    );
    public static final ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(TicEX.MODID);
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(
            ForgeRegistries.ATTRIBUTES,
            TicEX.MODID
    );
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB,
            TicEX.MODID
    );
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(
            Registries.RECIPE_SERIALIZER,
            TicEX.MODID
    );
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(
            Registries.RECIPE_TYPE,
            TicEX.MODID
    );
    public static final MenuTypeDeferredRegister MENUS = new MenuTypeDeferredRegister(
            TicEX.MODID
    );

    public static RegistryObject<CreativeModeTab> CREATIVE_TAB_ITEMS = null;

    public static RegistryObject<TypeAwareRecipeSerializer<EmbossmentCastingRecipe>> CASTING_EMBOSSMENT_RECIPE_SERIALIZER = null;
    public static RegistryObject<RecipeSerializer<EmbossmentBuildingRecipe>> BUILDING_EMBOSSMENT_RECIPE_SERIALIZER = null;
    public static RegistryObject<RecipeSerializer<EmbossmentModifierRecipe>> MODIFIER_EMBOSSMENT_RECIPE_SERIALIZER = null;
    public static RegistryObject<RecipeSerializer<SingleEmbossmentModifierRecipe>> SINGLE_MODIFIER_EMBOSSMENT_RECIPE_SERIALIZER = null;
    public static RegistryObject<RecipeSerializer<ValidatableIncrementalModifierRecipe>> VALIDATABLE_INCREMENTAL_RECIPE_SERIALIZER = null;


    public static RegistryObject<MenuType<ToolContainerMenu>> UNSYNCED_TOOL_CONTAINER = null;
    public static RegistryObject<MenuType<FluidTransmuterContainerMenu>> FLUID_TRANSMUTER_MENU = null;

    public static RegistryObject<Item> EXHAUSTED_GLOVE = null;
    public static RegistryObject<Item> ETHERIC_INGOT = null;
    public static RegistryObject<Item> OD_INGOT = null;
    public static RegistryObject<Item> DRACONIUM_CRYSTAL = null;
    public static RegistryObject<Item> WYVERN_CRYSTAL = null;
    public static RegistryObject<Item> DRACONIC_CRYSTAL = null;
    public static RegistryObject<Item> CHAOTIC_CRYSTAL = null;
    public static RegistryObject<Item> RECONSTRUCTION_CORE = null;
    public static RegistryObject<Item> FLICKERING_RECONSTRUCTION_CORE = null;
    public static RegistryObject<Item> CELESTIAL_CORE = null;
    public static RegistryObject<Item> RADIATION_SHELDING_CORE = null;
    public static RegistryObject<Item> DRACONIUM_EVOLVED_CORE = null;
    public static RegistryObject<Item> WYVERN_EVOLVED_CORE = null;
    public static RegistryObject<Item> DRACONIC_EVOLVED_CORE = null;
    public static RegistryObject<Item> CHAOTIC_EVOLVED_CORE = null;
    public static RegistryObject<Item> INJECT_CORE = null;
    public static RegistryObject<Item> KONPAKU_CORE = null;
    public static RegistryObject<Item> OVERLOAD_CORE = null;
    public static RegistryObject<Item> OVERRIDE_CORE = null;
    public static RegistryObject<Item> CARDBOARD_CORE = null;
    public static RegistryObject<Item> PSIONIZING_RADIATION_CORE = null;
    public static RegistryObject<Item> NECTAR_CORE = null;
    public static RegistryObject<Item> REACTIVE_CORE = null;
    public static RegistryObject<Item> ALTERATIVE_CORE = null;

    public static RegistryObject<Item> MEKANIC_ARROW = null;

    public static ItemObject<ToolPartItem> SLASHBLADE_BLADE = null;
    public static ItemObject<ToolPartItem> SLASHBLADE_SAYA = null;

    public static CastItemObject SLASHBLADE_BLADE_CAST;
    public static CastItemObject SLASHBLADE_SAYA_CAST;

    public static EnumObject<ArmorItem.Type, ToolPartItem> CATALYST_MEKASUIT = null;
    public static EnumObject<ArmorItem.Type, ToolPartItem> CATALYST_GEM = null;
    public static ItemObject<ToolPartItem> CATALYST_SLASHBLADE = null;
    public static ItemObject<ToolPartItem> CATALYST_KINETIC_GUN = null;
    public static ItemObject<ToolPartItem> CATALYST_IRONS_SPELLBOOK = null;
    public static ItemObject<ToolPartItem> CATALYST_MEKA_TOOL = null;
    public static ItemObject<ToolPartItem> CATALYST_MEKA_TANA = null;
    public static ItemObject<ToolPartItem> CATALYST_MEKA_BOW = null;

    public static ItemObject<? extends Item> REFORGED_SLASHBLADE = null;
    public static ItemObject<? extends Item> BLITZ_GUN = null;
    public static ItemObject<? extends Item> REVIVAL_SPELLBOOK_IRONS = null;
    public static ItemObject<? extends ModifiableItem> MEKA_EDGE = null;
    public static ItemObject<? extends Item> RESONANCE_GAUNTLET = null;

    public static EnumObject<ArmorItem.Type, ? extends IModifiable> MEKAPLATE_ARMOR = null;
    public static EnumObject<ArmorItem.Type, ? extends IModifiable> SINGULAR_GEM_ARMOR = null;

    public static RegistryObject<Block> ETHERIC_BLOCK = null;
    public static RegistryObject<Block> OD_BLOCK = null;
    public static RegistryObject<Block> SEARED_RF_FURNACE = null;
    public static RegistryObject<Block> CREATIVE_SEARED_RF_FURNACE = null;
    public static RegistryObject<Block> SCORCHED_RF_FURNACE = null;
    public static RegistryObject<Block> CREATIVE_SCORCHED_RF_FURNACE = null;
    public static RegistryObject<Block> FLUID_TRANSMUTER = null;

    public static RegistryObject<BlockEntityType<RFFurnaceBlockEntity>> RF_FURNACE_ENTITY = null;
    public static RegistryObject<BlockEntityType<FluidTransmuterBlockEntity>> FLUID_TRANSMUTER_ENTITY = null;

    public static FluidObject<UnplaceableFluid> MOLTEN_RECONSTRUCTION_CORE = null;
    public static List<FluidObject<UnplaceableFluid>> RF_FURNACE_FUELS = new ArrayList<>();
    public static FlowingFluidObject<ForgeFlowingFluid> MOLTEN_INFINITY = null;
    public static FlowingFluidObject<ForgeFlowingFluid> MOLTEN_NEUTRON = null;
    public static FlowingFluidObject<ForgeFlowingFluid> MOLTEN_CRYSTAL_MATRIX = null;
    public static FlowingFluidObject<ForgeFlowingFluid> MOLTEN_BLAZING = null;
    public static FlowingFluidObject<ForgeFlowingFluid> MOLTEN_ETHERIC = null;
    public static FlowingFluidObject<ForgeFlowingFluid> MOLTEN_OD = null;

    public static RegistryObject<EntityType<SBToolItemEntity>> SLASHBLADE_TOOL_ITEM_ENTITY = null;
    public static RegistryObject<EntityType<EndestShotProjectile>> ENDESTSHOT_PROJECTILE = null;
    public static RegistryObject<EntityType<ResonanceToolProjectile>> RESONANCE_TOOL_PROJECTILE = null;
    public static RegistryObject<EntityType<MekanicProjectile>> MEKANIC_PROJECTILE = null;

    public static RegistryObject<Attribute> HEALING_RECEIVED = null;
    public static RegistryObject<Attribute> DAMAGE_TAKEN = null;

    public static DynamicModifier REBIRTH_MODIFIER = null;
    public static StaticModifier<Modifier> DEFLECTION_MODIFIER = null;
    public static StaticModifier<Modifier> EMBOSSMENT_MODIFIER = null;
    public static StaticModifier<Modifier> ENCHANTMENT_SUPPLIER_MODIFIER = null;
    public static StaticModifier<Modifier> OMNIPOTENCE_MODIFIER = null;
    public static DynamicModifier COSMIC_UNBREAKABLE_MODIFIER = null;
    public static DynamicModifier COSMIC_LUCK_MODIFIER = null;
    public static StaticModifier<Modifier> BEDROCK_BREAKER_MODIFIER = null;
    public static DynamicModifier TRANSCENDENTAL_MODIFIER = null;
    public static StaticModifier<Modifier> CELESTIAL_MODIFIER = null;
    public static DynamicModifier ETERNITY_MODIFIER = null;
    public static StaticModifier<Modifier> CONDENSING_MODIFIER = null;
    public static DynamicModifier DENSE_MODIFIER = null;
    public static StaticModifier<Modifier> AFTERSHOCK_MODIFIER = null;
    public static StaticModifier<Modifier> ENDESTSHOT_MODIFIER = null;
    public static DynamicModifier SKULLFIRE_MODIFIER = null;
    public static DynamicModifier BLAZING_FLAME_MODIFIER = null;
    public static DynamicModifier BLAZING_FORTUNE_MODIFIER = null;
    public static StaticModifier<Modifier> MEKANIC_MODIFIER = null;
    public static DynamicModifier RADIATION_SHIELDING_MODIFIER = null;
    public static StaticModifier<Modifier> SASSY_MODIFIER = null;
    public static StaticModifier<Modifier> DRAGON_FORCE_MODIFIER = null;
    public static StaticModifier<Modifier> EVOLVED_MODIFIER = null;
    public static DynamicModifier INJECT_MODIFIER = null;
    public static StaticModifier<Modifier> SOUL_RENDING_MODIFIER = null;
    public static StaticModifier<Modifier> KONPAKU_MODIFIER = null;
    public static StaticModifier<Modifier> KOSHIRAE_MODIFIER = null;
    public static StaticModifier<Modifier> PROUD_MODIFIER = null;
    public static StaticModifier<Modifier> APOTH_SUPPLIER_MODIFIER = null;
    public static StaticModifier<Modifier> OVERLOAD_MODIFIER = null;
    public static StaticModifier<Modifier> OVERRIDE_MODIFIER = null;
    public static StaticModifier<Modifier> INCOMPARABLE_MODIFIER = null;
    public static DynamicModifier CARDBOARD_MODIFIER = null;
    public static StaticModifier<Modifier> OVERCASTING_MODIFIER = null;
    public static StaticModifier<Modifier> CLUSTER_MODIFIER = null;
    public static StaticModifier<Modifier> ABYSSAL_MODIFIER = null;
    public static StaticModifier<Modifier> INFERNAL_MODIFIER = null;
    public static StaticModifier<Modifier> GRAVITY_MODIFIER = null;
    public static StaticModifier<Modifier> HURRICANE_MODIFIER = null;
    public static DynamicModifier MODEM_MODIFIER = null;
    public static StaticModifier<Modifier> PSIONIZING_RADIATION_MODIFIER = null;
    public static StaticModifier<Modifier> SOCKET_MODIFIER = null;
    public static StaticModifier<Modifier> SENSOR_MODIFIER = null;
    public static DynamicModifier AHRIM_MODIFIER = null;
    public static DynamicModifier DHAROK_MODIFIER = null;
    public static DynamicModifier GUTHAN_MODIFIER = null;
    public static DynamicModifier TORAG_MODIFIER = null;
    public static DynamicModifier VERAC_MODIFIER = null;
    public static DynamicModifier KARIL_MODIFIER = null;
    public static DynamicModifier NECTAR_MODIFIER = null;
    public static StaticModifier<Modifier> REACTIVE_MODIFIER = null;
    public static StaticModifier<Modifier> ALTERATIVE_MODIFIER = null;
    public static StaticModifier<Modifier> AFLOAT_MODIFIER = null;
    public static StaticModifier<Modifier> DUNGEON_MASTER_MODIFIER = null;
    public static StaticModifier<Modifier> UNRAVEL_MODIFIER = null;

    public static Tier INFINITY_TIER;

    public static void addTabItems(ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
        for (RegistryObject<Item> itemObject : ITEMS.getEntries()) {
            Item item = itemObject.get();
            if (!(item instanceof ToolPartItem || item instanceof IModifiable || item instanceof ArrowItem)) {
                output.accept(itemObject.get());
            }
        }

        for (RegistryObject<Block> blockObject : BLOCKS.getEntries()) {
            output.accept(blockObject.get().asItem());
        }

        acceptCatalystArmor(output, CATALYST_MEKASUIT);
        acceptCatalystArmor(output, CATALYST_GEM);
        acceptPart(output, CATALYST_SLASHBLADE);
        acceptPart(output, CATALYST_MEKA_TOOL);
        acceptPart(output, CATALYST_MEKA_TANA);
        acceptPart(output, CATALYST_MEKA_BOW);
        //acceptPart(output, CATALYST_KINETIC_GUN);
        //acceptPart(output, CATALYST_IRONS_SPELLBOOK);

        acceptPart(output, SLASHBLADE_BLADE);
        acceptPart(output, SLASHBLADE_SAYA);

        acceptTool(output, MEKA_EDGE);
        acceptTool(output, RESONANCE_GAUNTLET);
        acceptTool(output, REFORGED_SLASHBLADE);
        //acceptTool(output, BLITZ_GUN);
        //acceptTool(output, REVIVAL_SPELLBOOK_IRONS);

        //acceptArmor(output, MEKAPLATE_ARMOR);
        //acceptArmor(output, SINGULAR_GEM_ARMOR);

        acceptCast(output, SLASHBLADE_BLADE_CAST);
        acceptCast(output, SLASHBLADE_SAYA_CAST);
    }

    private static void acceptTool(CreativeModeTab.Output output, Supplier<? extends Item> toolObject) {
        if (toolObject != null) {
            Item item = toolObject.get();
            if (item instanceof IModifiable) {
                ToolBuildHandler.addVariants(output::accept, (IModifiable) item, "");
            }
        }
    }

    private static void acceptArmor(CreativeModeTab.Output output, EnumObject<?, ? extends IModifiable> armorObject) {
        if (armorObject != null) {
            armorObject.forEach(obj -> ToolBuildHandler.addVariants(output::accept, obj, ""));
        }
    }

    private static void acceptCatalystArmor(
            CreativeModeTab.Output output,
            EnumObject<ArmorItem.Type, ToolPartItem> catalystObject
    ) {
        if (catalystObject != null) {
            catalystObject.forEach(c -> c.addVariants(output::accept, ""));
        }
    }

    private static void acceptPart(CreativeModeTab.Output output, ItemObject<ToolPartItem> partObject) {
        if (partObject != null) {
            partObject.get().addVariants(output::accept, "");
        }
    }

    private static void acceptCast(CreativeModeTab.Output output, CastItemObject castObject) {
        if (castObject != null) {
            output.accept(castObject.get());
            output.accept(castObject.getRedSand());
            output.accept(castObject.getSand());
        }
    }

    private static BlockBehaviour.Properties builder(MapColor color, SoundType soundType) {
        return BlockBehaviour.Properties.of().sound(soundType).mapColor(color);
    }
}
