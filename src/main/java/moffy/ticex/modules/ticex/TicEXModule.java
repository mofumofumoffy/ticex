package moffy.ticex.modules.ticex;

import moffy.addonapi.AddonModule;
import moffy.ticex.TicEX;
import moffy.ticex.block.transmuter.container.FluidTransmuterContainerMenu;
import moffy.ticex.block.transmuter.pattern.FluidTransmutationResolver;
import moffy.ticex.caps.TiCEXToolCapabilityProvider;
import moffy.ticex.client.modules.ticex.UnsyncedToolContainerMenu;
import moffy.ticex.client.modules.ticex.screen.FluidTransmuterScreen;
import moffy.ticex.event.TicEXEvent;
import moffy.ticex.item.cores.ItemFlickeringCore;
import moffy.ticex.item.cores.ItemReconstCore;
import moffy.ticex.lib.CatalystMaterialStatsType;
import moffy.ticex.lib.InfinityTier;
import moffy.ticex.lib.recipe.*;
import moffy.ticex.lib.utils.TicEXFluidUtils;
import moffy.ticex.modifier.ModifierEnchantmentSupplier;
import moffy.ticex.network.TicEXPacketID;
import moffy.ticex.network.curios.TicEXSyncEntityMovements;
import moffy.ticex.registry.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.tools.client.ToolContainerScreen;

import java.util.List;

public class TicEXModule implements AddonModule {

    @Override
    public void init(FMLJavaModLoadingContext context) {
        IEventBus bus = context.getModEventBus();

        ToolCapabilityProvider.register(TiCEXToolCapabilityProvider::new);

        TicEX.CHANNEL.messageBuilder(TicEXSyncEntityMovements.class, TicEXPacketID.SHOT_GAUNTLET)
                .encoder(TicEXSyncEntityMovements::encode)
                .decoder(TicEXSyncEntityMovements::new)
                .consumerMainThread(TicEXSyncEntityMovements::handle)
                .add();

        TicEXRecipeSerializers.MODIFIER_EMBOSSMENT_RECIPE_SERIALIZER = TicEXRegistry.RECIPE_SERIALIZERS.register(
                "embossment_modifier",
                () -> LoadableRecipeSerializer.of(EmbossmentModifierRecipe.LOADER)
        );
        TicEXRecipeSerializers.SINGLE_MODIFIER_EMBOSSMENT_RECIPE_SERIALIZER = TicEXRegistry.RECIPE_SERIALIZERS.register(
                "single_embossment_modifier",
                () -> LoadableRecipeSerializer.of(SingleEmbossmentModifierRecipe.LOADER)
        );
        TicEXRecipeSerializers.CASTING_EMBOSSMENT_RECIPE_SERIALIZER = TicEXRegistry.RECIPE_SERIALIZERS.register(
                "embossment_casting",
                () -> LoadableRecipeSerializer.of(EmbossmentCastingRecipe.LOADER, TinkerRecipeTypes.CASTING_TABLE)
        );
        TicEXRecipeSerializers.BUILDING_EMBOSSMENT_RECIPE_SERIALIZER = TicEXRegistry.RECIPE_SERIALIZERS.register(
                "embossment_building",
                () -> LoadableRecipeSerializer.of(EmbossmentBuildingRecipe.LOADER)
        );
        TicEXRecipeSerializers.VALIDATABLE_INCREMENTAL_RECIPE_SERIALIZER = TicEXRegistry.RECIPE_SERIALIZERS.register(
                "validatable_incremental_modifier",
                () -> LoadableRecipeSerializer.of(ValidatableIncrementalModifierRecipe.LOADER)
        );

        TicEXItems.RECONSTRUCTION_CORE = TicEXRegistry.ITEMS.register("reconstruction_core", () ->
                new ItemReconstCore(new Item.Properties(), null)
        );
        TicEXItems.FLICKERING_RECONSTRUCTION_CORE = TicEXRegistry.ITEMS.register(
                "flickering_reconstruction_core",
                () -> new ItemFlickeringCore(new Item.Properties())
        );

        TicEXFluids.MOLTEN_RECONSTRUCTION_CORE = TicEXRegistry.FLUIDS.register("molten_reconstruction_core")
                .type(TicEXFluidUtils.slime("reconstruction_core").temperature(1000).density(-1600))
                .bucket()
                .commonTag()
                .unplacable();


        TicEXAttributes.HEALING_RECEIVED = TicEXRegistry.ATTRIBUTES.register("healing_received", () ->
                new RangedAttribute("attribute." + TicEX.MODID + ".healing_received", 1f, 0f, 1f)
        );
        TicEXAttributes.DAMAGE_TAKEN = TicEXRegistry.ATTRIBUTES.register("damage_taken", () ->
                new RangedAttribute("attribute." + TicEX.MODID + ".damage_taken", 1f, Float.MIN_NORMAL, 1f).setSyncable(
                        true
                )
        );

        TicEXRegistry.CREATIVE_TAB_ITEMS = TicEXRegistry.CREATIVE_TABS.register(TicEX.MODID, () ->
                CreativeModeTab.builder()
                        .title(Component.translatable("itemGroup.tab." + TicEX.MODID))
                        .icon(() -> new ItemStack(TicEXItems.RECONSTRUCTION_CORE.get()))
                        .displayItems(TicEXRegistry::addTabItems)
                        .build()
        );

        TicEXModifiers.REBIRTH_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("rebirth");
        TicEXModifiers.ENCHANTMENT_SUPPLIER_MODIFIER = TicEXRegistry.MODIFIERS.register("enchantment_supplier", ModifierEnchantmentSupplier::new);

        TicEXMenuTypes.UNSYNCED_TOOL_CONTAINER = TicEXRegistry.MENUS.register(
                "unsynced_tool_container",
                UnsyncedToolContainerMenu::forClient
        );
        TicEXMenuTypes.FLUID_TRANSMUTER_MENU = TicEXRegistry.MENUS.register(
                "fluid_transmuter",
                FluidTransmuterContainerMenu::new
        );

        bus.addListener(TicEXEvent::onEntityAttributeModification);
        bus.addListener(TicEXEvent::onRegisterCaps);
        bus.addListener(TicEXEvent::registerModelLoaders);
        bus.addListener(TicEXEvent::registerRenderers);

        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, TicEXEvent::onEntityHeal);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, TicEXEvent::onEntityHurt);
        MinecraftForge.EVENT_BUS.addListener(TicEXEvent::supplierBouncer);
        MinecraftForge.EVENT_BUS.addListener(TicEXEvent::onDatapackSync);

        if (TierSortingRegistry.isTierSorted(InfinityTier.instance)) {
            TicEXRegistry.INFINITY_TIER = TierSortingRegistry.registerTier(
                    InfinityTier.instance,
                    TicEX.getResource("infinity"),
                    List.of(TierSortingRegistry.getSortedTiers().get(TierSortingRegistry.getSortedTiers().size() - 1)),
                    List.of()
            );
        } else {
            TicEXRegistry.INFINITY_TIER = TierSortingRegistry.registerTier(
                    InfinityTier.instance,
                    TicEX.getResource("infinity"),
                    List.of(Tiers.NETHERITE),
                    List.of()
            );
        }
    }

    @Override
    public void initClient(FMLJavaModLoadingContext context) {

    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(TicEXMenuTypes.UNSYNCED_TOOL_CONTAINER.get(), ToolContainerScreen::new);
            MenuScreens.register(TicEXMenuTypes.FLUID_TRANSMUTER_MENU.get(), FluidTransmuterScreen::new);
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
