package moffy.ticex.modules.avaritia;

import moffy.addonapi.AddonModule;
import moffy.ticex.TicEX;
import moffy.ticex.client.modules.avaritia.TicEXCosmicShaderProvider;
import moffy.ticex.client.rendering.PartPredicate;
import moffy.ticex.client.rendering.ticex.ItemArrowRenderer;
import moffy.ticex.client.rendering.ticex.TicEXRenders;
import moffy.ticex.entity.ItemArrow;
import moffy.ticex.entity.avaritia.EndestShotProjectile;
import moffy.ticex.event.TicEXAvaritiaEvent;
import moffy.ticex.item.cores.ItemReconstCore;
import moffy.ticex.item.projectile.EndestShotItem;
import moffy.ticex.lib.utils.TicEXFluidUtils;
import moffy.ticex.modifier.*;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.fluids.block.BurningLiquidBlock;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import java.util.ArrayList;
import java.util.List;

public class TicEXAvaritiaModule implements AddonModule {

    @Override
    public void init(FMLJavaModLoadingContext context) {
        TicEXRegistry.CELESTIAL_CORE = TicEXRegistry.ITEMS.register("celestial_core", () ->
                new ItemReconstCore(new Item.Properties(), "celestial")
        );
        TicEXRegistry.ENDESTSHOT_ARROW = TicEXRegistry.ITEMS.register("endestshot", () ->
                new EndestShotItem(new Item.Properties())
        );

        TicEXRegistry.OMNIPOTENCE_MODIFIER = TicEXRegistry.MODIFIERS.register("omnipotence", ModifierOmnipotence::new);
        TicEXRegistry.COSMIC_UNBREAKABLE_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("cosmic_unbreakable");
        TicEXRegistry.COSMIC_LUCK_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("cosmic_luck");
        TicEXRegistry.BEDROCK_BREAKER_MODIFIER = TicEXRegistry.MODIFIERS.register(
                "bedrock_breaker",
                ModifierBedrockBreaker::new
        );
        TicEXRegistry.CELESTIAL_MODIFIER = TicEXRegistry.MODIFIERS.register("celestial", ModifierCelestial::new);
        TicEXRegistry.CONDENSING_MODIFIER = TicEXRegistry.MODIFIERS.register("condensing", ModifierCondensing::new);
        TicEXRegistry.AFTERSHOCK_MODIFIER = TicEXRegistry.MODIFIERS.register("aftershock", ModifierAftershock::new);
        TicEXRegistry.TRANSCENDENTAL_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("transcendental");
        TicEXRegistry.DENSE_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("dense");
        TicEXRegistry.ENDESTSHOT_MODIFIER = TicEXRegistry.MODIFIERS.register("endestshot", ModifierEndestShot::new);
        TicEXRegistry.SKULLFIRE_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("skullfire");
        TicEXRegistry.BLAZING_FLAME_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("blazing_flame");
        TicEXRegistry.BLAZING_FORTUNE_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("blazing_fortune");

        TicEXRegistry.MOLTEN_INFINITY = TicEXRegistry.FLUIDS.register("molten_infinity")
                .type(TicEXFluidUtils.hot("molten_infinity").temperature(6360).lightLevel(15))
                .block(BurningLiquidBlock.createBurning(MapColor.EMERALD, 15, 20, 20f))
                .bucket()
                .commonTag()
                .flowing();
        TicEXRegistry.MOLTEN_NEUTRON = TicEXRegistry.FLUIDS.register("molten_neutron")
                .type(TicEXFluidUtils.cool().temperature(1000))
                .block(MapColor.COLOR_BLACK, 0)
                .bucket()
                .commonTag()
                .flowing();
        TicEXRegistry.MOLTEN_CRYSTAL_MATRIX = TicEXRegistry.FLUIDS.register("molten_crystal_matrix")
                .type(TicEXFluidUtils.cool().temperature(1000))
                .block(MapColor.COLOR_LIGHT_BLUE, 0)
                .bucket()
                .commonTag()
                .flowing();
        TicEXRegistry.MOLTEN_BLAZING = TicEXRegistry.FLUIDS.register("molten_blazing")
                .type(TicEXFluidUtils.hot("molten_blazing").temperature(4800).lightLevel(15))
                .block(BurningLiquidBlock.createBurning(MapColor.COLOR_ORANGE, 15, 20, 10f))
                .bucket()
                .commonTag()
                .flowing();

        TicEXRegistry.ENDESTSHOT_PROJECTILE = TicEXRegistry.ENTITIES.register("endestshot", () ->
                EntityType.Builder.<EndestShotProjectile>of(EndestShotProjectile::new, MobCategory.MISC)
                        .sized(0.5f, 0.5f)
                        .setTrackingRange(10)
                        .setUpdateInterval(20)
                        .setShouldReceiveVelocityUpdates(false)
                        .build(TicEX.MODID + ":endestshot")
        );

        MinecraftForge.EVENT_BUS.addListener(TicEXAvaritiaEvent::onGetHurt);
        MinecraftForge.EVENT_BUS.addListener(TicEXAvaritiaEvent::onDeath);
        MinecraftForge.EVENT_BUS.addListener(TicEXAvaritiaEvent::onPlayerTick);


    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient(FMLJavaModLoadingContext context) {
        List<MaterialVariantId> infinityMaterials = new ArrayList<>();
        infinityMaterials.add(new MaterialId(new ResourceLocation(TicEX.MODID, "infinity")));

        if (ModList.get().isLoaded("sakuratinker")) {
            infinityMaterials.add(new MaterialId(new ResourceLocation("sakuratinker", "infinity")));
        }

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        TicEXCosmicShaderProvider.init(bus);
        TicEXRenders.TOOL_SHADERS.addShader(new PartPredicate.Material(infinityMaterials::contains), new TicEXCosmicShaderProvider.Tool());
        TicEXRenders.ARMOR_SHADERS.addShader(new PartPredicate.Material(infinityMaterials::contains), new TicEXCosmicShaderProvider.Armor());

        bus.addListener(TicEXAvaritiaEvent::onRegisterRenderers);
    }

    @SuppressWarnings("unchecked")
    @OnlyIn(Dist.CLIENT)
    @Override
    public void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            EntityRenderers.register((EntityType<ItemArrow>)TicEXRegistry.ENDESTSHOT_PROJECTILE.get(), context -> new ItemArrowRenderer(context, 1));
        });
    }
}
