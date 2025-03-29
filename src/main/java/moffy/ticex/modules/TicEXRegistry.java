package moffy.ticex.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import moffy.ticex.TicEX;
import moffy.ticex.block.entity.RFFurnaceBlockEntity;
import moffy.ticex.client.TicEXShaderMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.item.Item;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.fluid.UnplaceableFluid;
import slimeknights.mantle.registration.deferred.FluidDeferredRegister;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.FlowingFluidObject;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.common.registration.ItemDeferredRegisterExtension;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.util.DynamicModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.armor.MultilayerArmorItem;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.smeltery.block.component.SearedBlock;

public class TicEXRegistry {

    public static final BlockBehaviour.Properties SEARED;
    static {
        IntFunction<BlockBehaviour.Properties> solidProps = factor ->
        builder(MapColor.COLOR_GRAY, SoundType.METAL)
            .instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F * factor, 9.0F * factor)
            .isValidSpawn((s, r, p, e) -> !s.hasProperty(SearedBlock.IN_STRUCTURE) || !s.getValue(SearedBlock.IN_STRUCTURE));
        SEARED = solidProps.apply(1);
    }

    public static final BlockBehaviour.Properties SCORCHED;
    static {
        IntFunction<BlockBehaviour.Properties> solidProps = factor -> builder(MapColor.TERRACOTTA_BROWN, SoundType.BASALT)
        .instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.5F * factor, 8.0F * factor)
        .isValidSpawn((s, r, p, e) -> !s.hasProperty(SearedBlock.IN_STRUCTURE) || !s.getValue(SearedBlock.IN_STRUCTURE));
        SCORCHED = solidProps.apply(1);
    }

    public static final ModifiableArmorMaterial MEKAPLATE_DEFINITION = ModifiableArmorMaterial.create(new ResourceLocation(TicEX.MODID, "mekaplate"), SoundEvents.ARMOR_EQUIP_NETHERITE);

    public static final TicEXShaderMap.Tool TOOL_SHADERS = new TicEXShaderMap.Tool();
    public static final TicEXShaderMap.Armor ARMOR_SHADERS = new TicEXShaderMap.Armor();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TicEX.MODID);
    public static final ItemDeferredRegisterExtension ITEMS_EXTENDED = new ItemDeferredRegisterExtension(TicEX.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TicEX.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TicEX.MODID);
    public static final FluidDeferredRegister FLUIDS = new FluidDeferredRegister(TicEX.MODID);
    public static final ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(TicEX.MODID);
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, TicEX.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TicEX.MODID);

    public static RegistryObject<CreativeModeTab> CREATIVE_TAB_ITEMS = null;
    public static RegistryObject<CreativeModeTab> CREATIVE_TAB_TOOLS = null;

    public static RegistryObject<Item> ETHERIC_INGOT = null;
    public static RegistryObject<Item> RECONSTRUCTION_CORE = null;
    public static RegistryObject<Item> CELESTIAL_CORE = null;
    public static RegistryObject<Item> RADIATION_SHELDING_CORE = null;
    public static RegistryObject<Item> DRACONIUM_EVOLVED_CORE = null;
    public static RegistryObject<Item> WYVERN_EVOLVED_CORE = null;
    public static RegistryObject<Item> DRACONIC_EVOLVED_CORE = null;
    public static RegistryObject<Item> CHAOTIC_EVOLVED_CORE = null;
    public static RegistryObject<Item> INJECT_CORE = null;

    public static EnumObject<ArmorItem.Type, ToolPartItem> CATALYST_MEKAPLATE = null; 

    public static EnumObject<ArmorItem.Type, MultilayerArmorItem> MEKAPLATE_ARMOR = null;

    public static RegistryObject<Block> ETHERIC_BLOCK = null;
    public static RegistryObject<Block> SEARED_RF_FURNACE = null;
    public static RegistryObject<Block> CREATIVE_SEARED_RF_FURNACE = null;
    public static RegistryObject<Block> SCORCHED_RF_FURNACE = null;
    public static RegistryObject<Block> CREATIVE_SCORCHED_RF_FURNACE = null;
    
    public static RegistryObject<BlockEntityType<RFFurnaceBlockEntity>> RF_FURNACE_ENTITY = null;
    public static RegistryObject<BlockEntityType<RFFurnaceBlockEntity>> CREATICE_RF_FURNACE_ENTITY = null;

    public static FluidObject<UnplaceableFluid> MOLTEN_RECONSTRUCTION_CORE = null;
    public static List<FluidObject<UnplaceableFluid>> RF_FURNACE_FUELS = new ArrayList<>(); 
    public static FlowingFluidObject<ForgeFlowingFluid> MOLTEN_INFINITY = null;
    public static FlowingFluidObject<ForgeFlowingFluid> MOLTEN_NEUTRONIUM = null;
    public static FlowingFluidObject<ForgeFlowingFluid> MOLTEN_CRYSTAL_MATRIX = null;
    public static FlowingFluidObject<ForgeFlowingFluid> MOLTEN_ETHERIC = null;

    public static RegistryObject<Attribute> HEALING_RECEIVED = null;
    public static RegistryObject<Attribute> DAMAGE_TAKEN = null;

    public static DynamicModifier REBIRTH_MODIFIER = null;
    public static StaticModifier<Modifier> OMNIPOTEMCE_MODIFIER = null;
    public static DynamicModifier COSMIC_UNBREAKABLE_MODIFIER = null;
    public static DynamicModifier COSMIC_LUCK_MODIFIER = null;
    public static StaticModifier<Modifier> BEDROCK_BREAKER_MODIFIER = null;
    public static DynamicModifier TRANSCENDENTAL_MODIFIER = null;
    public static StaticModifier<Modifier> CELESTIAL_MODIFIER = null;
    public static StaticModifier<Modifier> CONDENSING_MODIFIER = null;
    public static DynamicModifier DENSE_MODIFIER = null;
    public static StaticModifier<Modifier> AFTERSHOCK_MODIFIER = null;
    public static StaticModifier<Modifier> DEFLECTION_MODIFIER = null;
    public static DynamicModifier RADIATION_SHIELDING_MODIFIER = null;
    public static StaticModifier<Modifier> SASSY_MODIFIER = null;
    public static StaticModifier<Modifier> EVOLVED_MODIFIER = null;

    public static void addTabItems(ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
        for(RegistryObject<Item> itemObject : ITEMS.getEntries()){
            output.accept(itemObject.get());
        }

        for(RegistryObject<Block> blockObject : BLOCKS.getEntries()){
            output.accept(blockObject.get().asItem());
        }

        acceptCatalyst(output, CATALYST_MEKAPLATE);

        acceptArmor(output, MEKAPLATE_ARMOR);
    }

    private static void acceptTool(CreativeModeTab.Output output, Supplier<? extends IModifiable> toolObject){
        if(toolObject != null){
            ToolBuildHandler.addVariants(output::accept, toolObject.get(), "");
        }
    }

    private static void acceptArmor(CreativeModeTab.Output output, EnumObject<?,? extends IModifiable> armorObject){
        if(armorObject != null){
            armorObject.forEach(obj -> ToolBuildHandler.addVariants(output::accept, obj, ""));
        }
    }

    private static void acceptCatalyst(CreativeModeTab.Output output, EnumObject<ArmorItem.Type, ToolPartItem> catalystObject){
        if(catalystObject != null){
            catalystObject.forEach(c -> c.addVariants(output::accept, ""));
        }
    }

    private static BlockBehaviour.Properties builder(MapColor color, SoundType soundType) {
        return BlockBehaviour.Properties.of().sound(soundType).mapColor(color);
    }
}
