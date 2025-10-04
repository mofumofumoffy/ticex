package moffy.ticex.modules.mekanism;

import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registries.MekanismModules;
import moffy.addonapi.AddonModule;
import moffy.ticex.TicEX;
import moffy.ticex.caps.mekanism.MekItemCapabilityProvider;
import moffy.ticex.caps.mekanism.RadiationShieldingCapabilityProvider;
import moffy.ticex.client.modules.mekanism.MekaPlateModelCache;
import moffy.ticex.client.modules.mekanism.MekaPlateMultilayerModel;
import moffy.ticex.event.TicEXMekanismEvent;
import moffy.ticex.item.cores.ItemReconstCore;
import moffy.ticex.item.modifiable.ModifiableMekaSuitArmor;
import moffy.ticex.item.modifiable.ModifiableMekaTool;
import moffy.ticex.lib.CatalystMaterialStatsType;
import moffy.ticex.lib.utils.TicEXMekanismWeaponsUtils;
import moffy.ticex.modifier.ModifierMekanic;
import moffy.ticex.modules.general.TicEXRegistry;
import moffy.ticex.network.TicEXPacketID;
import moffy.ticex.network.mekanism.ConfigSyncToClientPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;

import java.util.ArrayList;
import java.util.List;

public class TicEXMekanismModule implements AddonModule {

    public static final MaterialStatsId CATALYST_MEKAPLATE = new MaterialStatsId(TicEX.MODID, "catalyst_mekaplate");

    public static BlockDeferredRegister BLOCKS;
    public static TileEntityTypeDeferredRegister TILE_ENTITY_TYPES;

    @Override
    public void init(FMLJavaModLoadingContext context) {
        IEventBus bus = context.getModEventBus();
        Item.Properties PROPS = new Item.Properties();

        ToolCapabilityProvider.register(MekItemCapabilityProvider::new);
        ToolCapabilityProvider.register(RadiationShieldingCapabilityProvider::new);

        TicEXRegistry.RADIATION_SHELDING_CORE = TicEXRegistry.ITEMS.register("radiation_shielding_core", () ->
                new ItemReconstCore(PROPS, "radiation_shielding")
        );

        TicEXRegistry.MEKAPLATE_ARMOR = TicEXRegistry.ITEMS_EXTENDED.registerEnum(
                "mekaplate",
                ArmorItem.Type.values(),
                type ->
                        new ModifiableMekaSuitArmor(TicEXRegistry.MEKAPLATE_DEFINITION, type, new Item.Properties().stacksTo(1))
        );

        TicEXRegistry.CATALYST_MEKASUIT = TicEXRegistry.ITEMS_EXTENDED.registerEnum(
                "catalyst_mekasuit",
                ArmorItem.Type.values(),
                type -> new ToolPartItem(PROPS, CatalystMaterialStatsType.getOrMakeType("catalyst_mekasuit", type).getId())
        );

        TicEXRegistry.MEKA_EDGE = TicEXRegistry.ITEMS_EXTENDED.register("meka_tool",
                () -> new ModifiableMekaTool(new Item.Properties().stacksTo(1))
        );

        TicEXRegistry.CATALYST_MEKA_TOOL = TicEXRegistry.ITEMS_EXTENDED.register("catalyst_meka_tool",
                () -> new ToolPartItem(new Item.Properties(), CatalystMaterialStatsType.getOrMakeType("catalyst_meka_tool").getId())
        );

        TicEXRegistry.MEKANIC_MODIFIER = TicEXRegistry.MODIFIERS.register("mekanic", ModifierMekanic::new);
        TicEXRegistry.RADIATION_SHIELDING_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("radiation_shielding");

        BLOCKS = new BlockDeferredRegister(TicEX.MODID);
        TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(TicEX.MODID);

        MinecraftForge.EVENT_BUS.register(new TicEXMekanismEvent());
        bus.addListener(TicEXMekanismEvent::onRegisterCaps);
        MinecraftForge.EVENT_BUS.addListener(TicEXMekanismEvent::getBreakSpeed);
        MinecraftForge.EVENT_BUS.addListener(TicEXMekanismEvent::onEntityAttack);
        MinecraftForge.EVENT_BUS.addListener(TicEXMekanismEvent::onLivingHurt);
        MinecraftForge.EVENT_BUS.addListener(TicEXMekanismEvent::onTick);
        MinecraftForge.EVENT_BUS.addListener(TicEXMekanismEvent::onModifyAttribute);
        MinecraftForge.EVENT_BUS.addListener(TicEXMekanismEvent::onLivingJump);

        TicEX.CHANNEL.messageBuilder(ConfigSyncToClientPacket.class, TicEXPacketID.MEK_CONFIG_SYNC)
                .encoder(ConfigSyncToClientPacket::encode)
                .decoder(ConfigSyncToClientPacket::new)
                .consumerMainThread(ConfigSyncToClientPacket::handle)
                .add();

        TicEXMekanismModule.BLOCKS.register(bus);
        TicEXMekanismModule.TILE_ENTITY_TYPES.register(bus);

        if(ModList.get().isLoaded("mekaweapons")){
            TicEXMekanismWeaponsUtils.register();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient(FMLJavaModLoadingContext context) {
        IEventBus bus = context.getModEventBus();
        MinecraftForge.EVENT_BUS.addListener(TicEXMekanismEvent::handleItemToolTip);
        bus.addListener(TicEXMekanismEvent::onLoadAdditionalModel);
        bus.addListener(TicEXMekanismEvent::onModelBake);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void clientSetup(FMLClientSetupEvent event) {
        MekaPlateModelCache.INSTANCE.registerMekaSuitModuleModel(
            new ResourceLocation(TicEX.MODID, "models/entity/modifiable_mekasuit_modules.obj")
        );

        MekaPlateMultilayerModel.registerModule(
            "jetpack",
            MekanismModules.JETPACK_UNIT,
            EquipmentSlot.CHEST,
            entity -> true
        );
        MekaPlateMultilayerModel.registerModule(
            "modulator",
            MekanismModules.GRAVITATIONAL_MODULATING_UNIT,
            EquipmentSlot.CHEST,
            entity -> true
        );
        MekaPlateMultilayerModel.registerModule(
            "elytra",
            MekanismModules.ELYTRA_UNIT,
            EquipmentSlot.CHEST,
            LivingEntity::isFallFlying
        );
    }
}
