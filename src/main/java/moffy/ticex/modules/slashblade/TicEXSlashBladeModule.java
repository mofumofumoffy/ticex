package moffy.ticex.modules.slashblade;

import mods.flammpfeil.slashblade.client.renderer.model.BladeModel;
import moffy.addonapi.AddonModule;
import moffy.ticex.TicEX;
import moffy.ticex.caps.slashblade.SBItemCapabilityProvider;
import moffy.ticex.client.slashblade.SBToolRenderType;
import moffy.ticex.entity.slashblade.SBToolItemEntity;
import moffy.ticex.event.TicEXSBEvent;
import moffy.ticex.item.modifiable.ModifiableSlashBladeItem;
import moffy.ticex.modifier.ModifierKonpaku;
import moffy.ticex.modifier.ModifierKoshirae;
import moffy.ticex.modules.CatalystMaterialStatsType;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

public class TicEXSlashBladeModule extends AddonModule{

    public static final ToolDefinition SLASHBLADE_DEFINITION = ToolDefinition.create(new ResourceLocation(TicEX.MODID, "slashblade_tool")); 

    public TicEXSlashBladeModule(){

        TicEXRegistry.SLASHBLADE_TOOL_ITEM_ENTITY = TicEXRegistry.ENTITIES.register("slashblade_tool", ()->EntityType.Builder.of(SBToolItemEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).setTrackingRange(10)
        .setUpdateInterval(20).setShouldReceiveVelocityUpdates(false)
        .build(TicEX.MODID+":slashblade_tool"));

        Item.Properties defaultProperties = new Item.Properties();

        ToolCapabilityProvider.register(SBItemCapabilityProvider::new);

        TicEXRegistry.CATALYST_SLASHBLADE = TicEXRegistry.ITEMS_EXTENDED.register("catalyst_slashblade", ()->new ToolPartItem(defaultProperties, CatalystMaterialStatsType.getOrMakeType("catalyst_slashblade").getId()));

        TicEXRegistry.SLASHBLADE_TOOL = TicEXRegistry.ITEMS_EXTENDED.register("reforged_slashblade", ()->new ModifiableSlashBladeItem(new Item.Properties().stacksTo(1), SLASHBLADE_DEFINITION));

        TicEXRegistry.SLASHBLADE_BLADE = TicEXRegistry.ITEMS_EXTENDED.register("slashblade_blade", ()->new ToolPartItem(defaultProperties, HeadMaterialStats.ID));
        TicEXRegistry.SLASHBLADE_SAYA = TicEXRegistry.ITEMS_EXTENDED.register("slashblade_saya", ()->new ToolPartItem(defaultProperties, HeadMaterialStats.ID));

        TicEXRegistry.KONPAKU_MODIFIER = TicEXRegistry.MODIFIERS.register("konpaku", ModifierKonpaku::new);
        TicEXRegistry.KOSHIRAE_MODIFIER = TicEXRegistry.MODIFIERS.register("koshirae", ModifierKoshirae::new);

        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, false, TicEXSBEvent::onInputChange);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ()->()->{
            IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
            bus.addListener(TicEXSBEvent::addLayers);
            bus.addListener(TicEXSBEvent::onRegisterRenderers);

            MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, false, TicEXSBEvent::onEntityUpdate);
        });
    }

    @Override
    public void clientSetup(FMLClientSetupEvent event) {
        SBToolRenderType.init();
        TicEXRegistry.CUSTOM_MODELS.put(TicEXRegistry.SLASHBLADE_TOOL.get(), (originalModel)->{
            return new BladeModel(originalModel, Minecraft.getInstance().getModelManager().getModelBakery());
        });
    }
}
