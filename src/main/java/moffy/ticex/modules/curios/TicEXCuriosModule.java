package moffy.ticex.modules.curios;

import moffy.addonapi.AddonModule;
import moffy.ticex.TicEX;
import moffy.ticex.caps.curios.CuriosCapProvider;
import moffy.ticex.entity.avaritia.EndestShotProjectile;
import moffy.ticex.entity.curios.ResonanceToolProjectile;
import moffy.ticex.event.TicEXCuriosEvent;
import moffy.ticex.event.TicEXEvent;
import moffy.ticex.item.GloveItem;
import moffy.ticex.item.cores.ItemReconstCore;
import moffy.ticex.item.modifiable.ModifiableGauntlet;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;

public class TicEXCuriosModule extends AddonModule {

    public TicEXCuriosModule() {
        ToolCapabilityProvider.register(CuriosCapProvider::new);

        TicEXRegistry.RESONANCE_TOOL_PROJECTILE = TicEXRegistry.ENTITIES.register("resonance_tool", () ->
                EntityType.Builder.<ResonanceToolProjectile>of(ResonanceToolProjectile::new, MobCategory.MISC)
                        .sized(0.5f, 0.5f)
                        .setTrackingRange(10)
                        .setUpdateInterval(20)
                        .setShouldReceiveVelocityUpdates(false)
                        .build(TicEX.MODID + ":resonance_tool")
        );

        TicEXRegistry.EXHAUSTED_GLOVE = TicEXRegistry.ITEMS.register("exhausted_glove", () -> new GloveItem(new Item.Properties().stacksTo(1)));
        TicEXRegistry.RESONANCE_GAUNTLET = TicEXRegistry.ITEMS_EXTENDED.register("resonance_gauntlet", ()->new ModifiableGauntlet(new Item.Properties().stacksTo(1), TicEXRegistry.GAUNTLET_DEFINITION));

        TicEXRegistry.INCOMPARABLE_CORE = TicEXRegistry.ITEMS.register("incomparable_core", () ->
            new ItemReconstCore(new Properties(), "incomparable")
        );

        TicEXRegistry.INCOMPARABLE_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("incomparable");

        MinecraftForge.EVENT_BUS.addListener(TicEXCuriosEvent::onLivingDeath);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ()-> this::initClient);
    }

    @OnlyIn(Dist.CLIENT)
    public void initClient(){
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(TicEXCuriosEvent::onRegisterRenderers);
        bus.addListener(TicEXCuriosEvent::addLayers);
    }
}
