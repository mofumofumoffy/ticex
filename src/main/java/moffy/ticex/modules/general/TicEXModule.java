package moffy.ticex.modules.general;

import moffy.addonapi.AddonModule;
import moffy.ticex.TicEX;
import moffy.ticex.block.RFFurnaceBlock;
import moffy.ticex.block.entity.RFFurnaceBlockEntity;
import moffy.ticex.block.transmuter.FluidTransmuterBlock;
import moffy.ticex.block.transmuter.container.FluidTransmuterContainerMenu;
import moffy.ticex.block.transmuter.entity.FluidTransmuterBlockEntity;
import moffy.ticex.block.transmuter.pattern.FluidTransmutationResolver;
import moffy.ticex.caps.TiCEXToolCapabilityProvider;
import moffy.ticex.client.modules.ticex.UnsyncedToolContainerMenu;
import moffy.ticex.client.modules.ticex.screen.FluidTransmuterScreen;
import moffy.ticex.event.TicEXEvent;
import moffy.ticex.item.cores.ItemFlickeringCore;
import moffy.ticex.item.cores.ItemReconstCore;
import moffy.ticex.jei.ticex.TicEXJEIIntegration;
import moffy.ticex.lib.CatalystMaterialStatsType;
import moffy.ticex.lib.InfinityTier;
import moffy.ticex.lib.hook.EmbossmentModifierHook;
import moffy.ticex.lib.hook.ProvidePropertyModifierHook;
import moffy.ticex.lib.recipe.*;
import moffy.ticex.lib.utils.TicEXFluidUtils;
import moffy.ticex.modifier.ModifierDeflection;
import moffy.ticex.modifier.ModifierEmbossment;
import moffy.ticex.modifier.ModifierEnchantmentSupplier;
import moffy.ticex.modifier.ModifierSassy;
import moffy.ticex.network.TicEXPacketID;
import moffy.ticex.network.curios.TicEXSyncEntityMovements;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.tools.client.ToolContainerScreen;

import java.util.List;

public class TicEXModule extends AddonModule {

    public TicEXModule() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ToolCapabilityProvider.register(TiCEXToolCapabilityProvider::new);

        TicEX.CHANNEL.messageBuilder(TicEXSyncEntityMovements.class, TicEXPacketID.SHOT_GAUNTLET)
                .encoder(TicEXSyncEntityMovements::encode)
                .decoder(TicEXSyncEntityMovements::new)
                .consumerMainThread(TicEXSyncEntityMovements::handle)
                .add();

        TicEXRegistry.MODIFIER_EMBOSSMENT_RECIPE_SERIALIZER = TicEXRegistry.RECIPE_SERIALIZERS.register(
            "embossment_modifier",
                () -> LoadableRecipeSerializer.of(EmbossmentModifierRecipe.LOADER)
        );
        TicEXRegistry.SINGLE_MODIFIER_EMBOSSMENT_RECIPE_SERIALIZER = TicEXRegistry.RECIPE_SERIALIZERS.register(
            "single_embossment_modifier",
            () -> LoadableRecipeSerializer.of(SingleEmbossmentModifierRecipe.LOADER)
        );
        TicEXRegistry.CASTING_EMBOSSMENT_RECIPE_SERIALIZER = TicEXRegistry.RECIPE_SERIALIZERS.register(
            "embossment_casting",
            () -> LoadableRecipeSerializer.of(EmbossmentCastingRecipe.LOADER, TinkerRecipeTypes.CASTING_TABLE)
        );
        TicEXRegistry.BUILDING_EMBOSSMENT_RECIPE_SERIALIZER = TicEXRegistry.RECIPE_SERIALIZERS.register(
            "embossment_building",
            () -> LoadableRecipeSerializer.of(EmbossmentBuildingRecipe.LOADER)
        );
        TicEXRegistry.VALIDATABLE_INCREMENTAL_RECIPE_SERIALIZER = TicEXRegistry.RECIPE_SERIALIZERS.register(
                "validatable_incremental_modifier",
                () -> LoadableRecipeSerializer.of(ValidatableIncrementalModifierRecipe.LOADER)
        );

        TicEXRegistry.EMBOSSMENT_HOOK = ModifierHooks.LOADER.register(
            new ModuleHook<>(
                new ResourceLocation(TicEX.MODID, "embossment"),
                EmbossmentModifierHook.class,
                EmbossmentModifierHook.AllMerger::new,
                new EmbossmentModifierHook.DefaultClass()
            )
        );
        TicEXRegistry.PROPERTY_PROVIDER_HOOK = ModifierHooks.LOADER.register(
            new ModuleHook<>(
                new ResourceLocation(TicEX.MODID, "provide_property"),
                ProvidePropertyModifierHook.class,
                ProvidePropertyModifierHook.AllMerger::new,
                new ProvidePropertyModifierHook.DefaultClass()
            )
        );

        TicEXRegistry.RECONSTRUCTION_CORE = TicEXRegistry.ITEMS.register("reconstruction_core", () ->
            new ItemReconstCore(new Item.Properties(), null)
        );
        TicEXRegistry.FLICKERING_RECONSTRUCTION_CORE = TicEXRegistry.ITEMS.register(
            "flickering_reconstruction_core",
            () -> new ItemFlickeringCore(new Item.Properties())
        );
        TicEXRegistry.ETHERIC_INGOT = TicEXRegistry.ITEMS.register("etheric_ingot", () ->
            new Item(new Item.Properties())
        );

        TicEXRegistry.ETHERIC_BLOCK = TicEXRegistry.BLOCKS.register("etheric_block", () ->
                new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).noOcclusion())
        );
        TicEXRegistry.SEARED_RF_FURNACE = TicEXRegistry.BLOCKS.register("seared_rf_furnace", () ->
            new RFFurnaceBlock(TicEXRegistry.SEARED, false)
        );
        TicEXRegistry.SCORCHED_RF_FURNACE = TicEXRegistry.BLOCKS.register("scorched_rf_furnace", () ->
            new RFFurnaceBlock(TicEXRegistry.SCORCHED, false)
        );
        TicEXRegistry.CREATIVE_SEARED_RF_FURNACE = TicEXRegistry.BLOCKS.register("creative_seared_rf_furnace", () ->
            new RFFurnaceBlock(TicEXRegistry.SEARED, true)
        );
        TicEXRegistry.CREATIVE_SCORCHED_RF_FURNACE = TicEXRegistry.BLOCKS.register("creative_scorched_rf_furnace", () ->
            new RFFurnaceBlock(TicEXRegistry.SCORCHED, true)
        );
        TicEXRegistry.FLUID_TRANSMUTER = TicEXRegistry.BLOCKS.register("fluid_transmuter", () ->
                new FluidTransmuterBlock(BlockBehaviour.Properties.of().noOcclusion())
        );

        TicEXRegistry.ITEMS.register("etheric_block", () ->
            new BlockItem(TicEXRegistry.ETHERIC_BLOCK.get(), new Item.Properties())
        );
        TicEXRegistry.ITEMS.register("seared_rf_furnace", () ->
            new BlockItem(TicEXRegistry.SEARED_RF_FURNACE.get(), new Item.Properties())
        );
        TicEXRegistry.ITEMS.register("scorched_rf_furnace", () ->
            new BlockItem(TicEXRegistry.SCORCHED_RF_FURNACE.get(), new Item.Properties())
        );
        TicEXRegistry.ITEMS.register("creative_seared_rf_furnace", () ->
            new BlockItem(TicEXRegistry.CREATIVE_SEARED_RF_FURNACE.get(), new Item.Properties())
        );
        TicEXRegistry.ITEMS.register("creative_scorched_rf_furnace", () ->
            new BlockItem(TicEXRegistry.CREATIVE_SCORCHED_RF_FURNACE.get(), new Item.Properties())
        );
        TicEXRegistry.ITEMS.register("fluid_transmuter", () ->
                new BlockItem(TicEXRegistry.FLUID_TRANSMUTER.get(), new Item.Properties())
        );

        TicEXRegistry.RF_FURNACE_ENTITY = TicEXRegistry.BLOCK_ENTITIES.register("rf_furnace_entity", () ->
            BlockEntityType.Builder.of(
                (BlockPos pPos, BlockState pState) ->
                    new RFFurnaceBlockEntity(TicEXRegistry.RF_FURNACE_ENTITY.get(), pPos, pState, false),
                TicEXRegistry.SEARED_RF_FURNACE.get(),
                TicEXRegistry.SCORCHED_RF_FURNACE.get(),
                TicEXRegistry.CREATIVE_SEARED_RF_FURNACE.get(),
                TicEXRegistry.CREATIVE_SCORCHED_RF_FURNACE.get()
            ).build(null)
        );

        TicEXRegistry.FLUID_TRANSMUTER_ENTITY = TicEXRegistry.BLOCK_ENTITIES.register("fluid_transmuter", () ->
                BlockEntityType.Builder.of(
                        (BlockPos pPos, BlockState pState) ->
                                new FluidTransmuterBlockEntity(TicEXRegistry.FLUID_TRANSMUTER_ENTITY.get(), pPos, pState),
                        TicEXRegistry.FLUID_TRANSMUTER.get()
                ).build(null)
        );

        TicEXRegistry.MOLTEN_ETHERIC = TicEXRegistry.FLUIDS.register("molten_etheric")
            .type(TicEXFluidUtils.hot("molten_etheric").temperature(1000).density(1600))
            .block(MapColor.COLOR_LIGHT_GREEN, 0)
            .bucket()
            .commonTag()
            .flowing();
        TicEXRegistry.MOLTEN_RECONSTRUCTION_CORE = TicEXRegistry.FLUIDS.register("molten_reconstruction_core")
            .type(TicEXFluidUtils.slime("reconstruction_core").temperature(1000).density(-1600))
            .bucket()
            .unplacable();
        for (int i = 0; i < 20; i++) {
            TicEXRegistry.RF_FURNACE_FUELS.add(
                TicEXRegistry.FLUIDS.register("rf_furnace_fuel_" + i)
                    .type(TicEXFluidUtils.hot("rf_furnace_fuel_" + i).temperature(1000).density(-1600))
                    .unplacable()
            );
        }

        TicEXRegistry.HEALING_RECEIVED = TicEXRegistry.ATTRIBUTES.register("healing_received", () ->
            new RangedAttribute("attribute." + TicEX.MODID + ".healing_received", 1f, 0f, 1f)
        );
        TicEXRegistry.DAMAGE_TAKEN = TicEXRegistry.ATTRIBUTES.register("damage_taken", () ->
            new RangedAttribute("attribute." + TicEX.MODID + ".damage_taken", 1f, Float.MIN_NORMAL, 1f).setSyncable(
                true
            )
        );

        TicEXRegistry.CREATIVE_TAB_ITEMS = TicEXRegistry.CREATIVE_TABS.register(TicEX.MODID, () ->
            CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.tab." + TicEX.MODID))
                .icon(() -> new ItemStack(TicEXRegistry.RECONSTRUCTION_CORE.get()))
                .displayItems(TicEXRegistry::addTabItems)
                .build()
        );

        TicEXRegistry.REBIRTH_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("rebirth");
        TicEXRegistry.EMBOSSMENT_MODIFIER = TicEXRegistry.MODIFIERS.register("embossment", ModifierEmbossment::new);
        TicEXRegistry.ENCHANTMENT_SUPPLIER_MODIFIER = TicEXRegistry.MODIFIERS.register("enchantment_supplier", ModifierEnchantmentSupplier::new);
        TicEXRegistry.DEFLECTION_MODIFIER = TicEXRegistry.MODIFIERS.register("deflection", ModifierDeflection::new);
        TicEXRegistry.SASSY_MODIFIER = TicEXRegistry.MODIFIERS.register("sassy", ModifierSassy::new);

        TicEXRegistry.UNSYNCED_TOOL_CONTAINER = TicEXRegistry.MENUS.register(
                "unsynced_tool_container",
                UnsyncedToolContainerMenu::forClient
        );
        TicEXRegistry.FLUID_TRANSMUTER_MENU = TicEXRegistry.MENUS.register(
                "fluid_transmuter",
                FluidTransmuterContainerMenu::new
        );

        TicEXRegistry.JEI_INTEGRATIONS.register(
                new ResourceLocation(TicEX.MODID, "ticex_compat"),
                Lazy.of(TicEXJEIIntegration::new)
        );

        bus.addListener(TicEXEvent::onEntityAttributeModification);
        bus.addListener(TicEXEvent::onRegisterCaps);
        bus.addListener(TicEXEvent::registerModelLoaders);

        MinecraftForge.EVENT_BUS.addListener(TicEXEvent::modifyAttribute);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, TicEXEvent::onEntityHeal);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, TicEXEvent::onEntityHurt);
        MinecraftForge.EVENT_BUS.addListener(TicEXEvent::supplierBouncer);
        MinecraftForge.EVENT_BUS.addListener(TicEXEvent::onRecipesUpdated);

        if (TierSortingRegistry.isTierSorted(InfinityTier.instance)) {
            TicEXRegistry.INFINITY_TIER = TierSortingRegistry.registerTier(
                InfinityTier.instance,
                new ResourceLocation(TicEX.MODID, "infinity"),
                List.of(TierSortingRegistry.getSortedTiers().get(TierSortingRegistry.getSortedTiers().size() - 1)),
                List.of()
            );
        } else {
            TicEXRegistry.INFINITY_TIER = TierSortingRegistry.registerTier(
                InfinityTier.instance,
                new ResourceLocation(TicEX.MODID, "infinity"),
                List.of(Tiers.NETHERITE),
                List.of()
            );
        }
    }

    @Override
    public void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(TicEXRegistry.UNSYNCED_TOOL_CONTAINER.get(), ToolContainerScreen::new);
            MenuScreens.register(TicEXRegistry.FLUID_TRANSMUTER_MENU.get(), FluidTransmuterScreen::new);
        });
    }

    @Override
    public void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CatalystMaterialStatsType.RegisterStats();
            FluidTransmutationResolver.INSTANCE.initConfig();
        });
    }
}
