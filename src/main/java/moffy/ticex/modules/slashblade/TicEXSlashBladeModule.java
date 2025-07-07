package moffy.ticex.modules.slashblade;

import moffy.addonapi.AddonModule;
import moffy.ticex.TicEX;
import moffy.ticex.caps.slashblade.SBItemCapabilityProvider;
import moffy.ticex.entity.slashblade.SBToolItemEntity;
import moffy.ticex.event.TicEXSBEvent;
import moffy.ticex.item.cores.ItemReconstCore;
import moffy.ticex.item.modifiable.ModifiableSlashBladeItem;
import moffy.ticex.lib.CatalystMaterialStatsType;
import moffy.ticex.modifier.ModifierHiddenProud;
import moffy.ticex.modifier.ModifierKonpaku;
import moffy.ticex.modifier.ModifierKoshirae;
import moffy.ticex.modules.general.TicEXRegistry;
import moffy.ticex.network.TicEXPacketID;
import moffy.ticex.network.slashblade.StateSyncPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

public class TicEXSlashBladeModule extends AddonModule {

    public TicEXSlashBladeModule() {
        TicEXRegistry.SLASHBLADE_TOOL_ITEM_ENTITY = TicEXRegistry.ENTITIES.register("reforged_slashblade", () ->
            EntityType.Builder.of(SBToolItemEntity::new, MobCategory.MISC)
                .sized(0.5F, 0.5F)
                .setTrackingRange(10)
                .setUpdateInterval(20)
                .setShouldReceiveVelocityUpdates(false)
                .build(TicEX.MODID + ":reforged_slashblade")
        );

        Item.Properties defaultProperties = new Item.Properties();

        ToolCapabilityProvider.register(SBItemCapabilityProvider::new);

        TicEXRegistry.KONPAKU_CORE = TicEXRegistry.ITEMS.register("konpaku_core", () ->
            new ItemReconstCore(defaultProperties, "konpaku")
        );
        TicEXRegistry.KOSHIRAE_CORE = TicEXRegistry.ITEMS.register("koshirae_core", () ->
            new ItemReconstCore(defaultProperties, "koshirae")
        );

        TicEXRegistry.CATALYST_SLASHBLADE = TicEXRegistry.ITEMS_EXTENDED.register("catalyst_slashblade", () ->
            new ToolPartItem(defaultProperties, CatalystMaterialStatsType.getOrMakeType("catalyst_slashblade").getId())
        );

        TicEXRegistry.REFORGED_SLASHBLADE = TicEXRegistry.ITEMS_EXTENDED.register("reforged_slashblade", () ->
            new ModifiableSlashBladeItem(new Item.Properties().stacksTo(1), TicEXRegistry.SLASHBLADE_DEFINITION)
        );

        TicEXRegistry.SLASHBLADE_BLADE = TicEXRegistry.ITEMS_EXTENDED.register("slashblade_blade", () ->
            new ToolPartItem(defaultProperties, HeadMaterialStats.ID)
        );
        TicEXRegistry.SLASHBLADE_SAYA = TicEXRegistry.ITEMS_EXTENDED.register("slashblade_saya", () ->
            new ToolPartItem(defaultProperties, HeadMaterialStats.ID)
        );

        TicEXRegistry.SLASHBLADE_BLADE_CAST = TicEXRegistry.ITEMS_EXTENDED.registerCast(
            "slashblade_blade",
            defaultProperties
        );
        TicEXRegistry.SLASHBLADE_SAYA_CAST = TicEXRegistry.ITEMS_EXTENDED.registerCast(
            "slashblade_saya",
            defaultProperties
        );

        TicEXRegistry.KONPAKU_MODIFIER = TicEXRegistry.MODIFIERS.register("konpaku", ModifierKonpaku::new);
        TicEXRegistry.KOSHIRAE_MODIFIER = TicEXRegistry.MODIFIERS.register("koshirae", ModifierKoshirae::new);
        TicEXRegistry.PROUD_MODIFIER = TicEXRegistry.MODIFIERS.register("hidden_proud", ModifierHiddenProud::new);

        TicEX.CHANNEL.registerMessage(
            TicEXPacketID.SB_STATE_SYNC,
            StateSyncPacket.class,
            StateSyncPacket::encode,
            StateSyncPacket::decode,
            StateSyncPacket::handle
        );

        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, TicEXSBEvent::onBladeMotion);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, TicEXSBEvent::onInputCommand);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, TicEXSBEvent::onLivingDeath);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, TicEXSBEvent::onLivingExperienceDrop);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, TicEXSBEvent::onLivingFall);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, TicEXSBEvent::onLivingHurt);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, TicEXSBEvent::onPlayerFlyableFall);

        DistExecutor.unsafeRunWhenOn(
            Dist.CLIENT,
            () ->
                () -> {
                    initClient();
                }
        );
    }

    @OnlyIn(Dist.CLIENT)
    private void initClient() {
        moffy.ticex.client.slashblade.SBToolRenderType.init();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(TicEXSBEvent::onRegisterRenderers);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void clientSetup(FMLClientSetupEvent event) {
        TicEXRegistry.CUSTOM_MODELS.put(TicEXRegistry.REFORGED_SLASHBLADE.get(), originalModel -> {
            return new moffy.ticex.client.CustomModel(originalModel);
        });
    }
    /* public static boolean isPreviousVersion(){
        return ModList.get().getModFileById("slashblade").versionString().compareTo("1.2.0") < 0;
    } */
}
