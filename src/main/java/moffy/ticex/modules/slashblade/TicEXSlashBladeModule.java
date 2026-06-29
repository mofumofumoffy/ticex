package moffy.ticex.modules.slashblade;

import moffy.addonapi.AddonModule;
import moffy.ticex.TicEX;
import moffy.ticex.caps.slashblade.SBItemCapabilityProvider;
import moffy.ticex.client.render.custom.CustomModel;
import moffy.ticex.client.render.ticex.TicEXRenders;
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
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

public class TicEXSlashBladeModule implements AddonModule {

    @Override
    public void init(FMLJavaModLoadingContext context) {
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
                TicEXRegistry.SLASHBLADE_BLADE,
                defaultProperties
        );
        TicEXRegistry.SLASHBLADE_SAYA_CAST = TicEXRegistry.ITEMS_EXTENDED.registerCast(
                TicEXRegistry.SLASHBLADE_SAYA,
                defaultProperties
        );

        TicEXRegistry.KONPAKU_MODIFIER = TicEXRegistry.MODIFIERS.register("konpaku", ModifierKonpaku::new);
        TicEXRegistry.KOSHIRAE_MODIFIER = TicEXRegistry.MODIFIERS.register("koshirae", ModifierKoshirae::new);
        TicEXRegistry.PROUD_MODIFIER = TicEXRegistry.MODIFIERS.register("hidden_proud", ModifierHiddenProud::new);

        TicEX.CHANNEL.messageBuilder(StateSyncPacket.class, TicEXPacketID.SB_STATE_SYNC)
                .encoder(StateSyncPacket::encode)
                .decoder(StateSyncPacket::new)
                .consumerMainThread(StateSyncPacket::handle)
                .add();

        TicEXSBEvent.registerEventsByVersion();
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, TicEXSBEvent::onBladeMotion);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, TicEXSBEvent::onLivingDeath);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, TicEXSBEvent::onLivingExperienceDrop);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, TicEXSBEvent::onLivingFall);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, TicEXSBEvent::onLivingHurt);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, TicEXSBEvent::onPlayerFlyableFall);
        //MinecraftForge.EVENT_BUS.addListener(TicEXSBEvent::onHit);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient(FMLJavaModLoadingContext context) {
        IEventBus bus = context.getModEventBus();
        bus.addListener(TicEXSBEvent::registerItemDecorators);
        bus.addListener(TicEXSBEvent::onRegisterRenderers);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void clientSetup(FMLClientSetupEvent event) {
        TicEXRenders.CUSTOM_MODELS.put(TicEXRegistry.REFORGED_SLASHBLADE.get(), CustomModel::new);
    }
    /* public static boolean isPreviousVersion(){
        return ModList.get().getModFileById("slashblade").versionString().compareTo("1.2.0") < 0;
    } */
}
