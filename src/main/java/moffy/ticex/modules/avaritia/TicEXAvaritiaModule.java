package moffy.ticex.modules.avaritia;

import moffy.addonapi.AddonModule;
import moffy.ticex.TicEX;
import moffy.ticex.client.avaritia.TicEXCosmicShader;
import moffy.ticex.event.TicEXAvaritiaEvent;
import moffy.ticex.item.cores.ItemReconstCore;
import moffy.ticex.modifier.ModifierAftershock;
import moffy.ticex.modifier.ModifierBedrockBreaker;
import moffy.ticex.modifier.ModifierCelestial;
import moffy.ticex.modifier.ModifierCondensing;
import moffy.ticex.modifier.ModifierOmnipotence;
import moffy.ticex.modules.TicEXRegistry;
import moffy.ticex.utils.TicEXFluidUtils;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import slimeknights.tconstruct.fluids.block.BurningLiquidBlock;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

public class TicEXAvaritiaModule extends AddonModule{

    public TicEXAvaritiaModule(){
        TicEXRegistry.CELESTIAL_CORE = TicEXRegistry.ITEMS.register("celestial_core", ()->new ItemReconstCore(new Item.Properties(), "celestial"));

        TicEXRegistry.OMNIPOTEMCE_MODIFIER = TicEXRegistry.MODIFIERS.register("omnipotence", ModifierOmnipotence::new);
        TicEXRegistry.COSMIC_UNBREAKABLE_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("cosmic_unbreakable");
        TicEXRegistry.COSMIC_LUCK_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("cosmic_luck");
        TicEXRegistry.BEDROCK_BREAKER_MODIFIER = TicEXRegistry.MODIFIERS.register("bedrock_breaker", ModifierBedrockBreaker::new);
        TicEXRegistry.CELESTIAL_MODIFIER = TicEXRegistry.MODIFIERS.register("celestial", ModifierCelestial::new);
        TicEXRegistry.CONDENSING_MODIFIER = TicEXRegistry.MODIFIERS.register("condensing", ModifierCondensing::new);
        TicEXRegistry.AFTERSHOCK_MODIFIER = TicEXRegistry.MODIFIERS.register("aftershock", ModifierAftershock::new);

        TicEXRegistry.MOLTEN_INFINITY = TicEXRegistry.FLUIDS.register("molten_infinity").type(TicEXFluidUtils.hot("molten_infinity").temperature(6360).lightLevel(15)).block(BurningLiquidBlock.createBurning(MapColor.EMERALD, 15, 20, 20f)).bucket().commonTag().flowing();
        TicEXRegistry.MOLTEN_NEUTRONIUM = TicEXRegistry.FLUIDS.register("molten_neutronium").type(TicEXFluidUtils.cool().temperature(1000)).block(MapColor.COLOR_BLACK, 0).bucket().commonTag().flowing();        
        TicEXRegistry.MOLTEN_CRYSTAL_MATRIX = TicEXRegistry.FLUIDS.register("molten_crystal_matrix").type(TicEXFluidUtils.cool().temperature(1000)).block(MapColor.COLOR_LIGHT_BLUE, 0).bucket().commonTag().flowing();    
    
        MinecraftForge.EVENT_BUS.addListener(TicEXAvaritiaEvent::onGetHurt);
        MinecraftForge.EVENT_BUS.addListener(TicEXAvaritiaEvent::onDeath);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ()->()->{
            TicEXCosmicShader.setup();
        
            MaterialVariantId infinityMaterial = new MaterialId(new ResourceLocation(TicEX.MODID, "infinity"));

            TicEXRegistry.TOOL_SHADERS.addShader(infinityMaterial, 
                (wrapper)->{
                    TicEXCosmicShader.instance.setupCosmic(wrapper.getDisplayContext());
                    RenderType cosmicRenderType = TicEXCosmicShader.instance.getCosmicRenderTypeTool();
                    wrapper.renderQuadsWithConsumer(cosmicRenderType);
                }
            );

            TicEXRegistry.ARMOR_SHADERS.addShader(infinityMaterial,
                (wrapper)->{
                    TicEXCosmicShader.instance.setupCosmic();
                    Material material = new Material(InventoryMenu.BLOCK_ATLAS, wrapper.getTexture());
                    wrapper.renderArmorWithConsumer(material.buffer(wrapper.getBufferSource(), TicEXCosmicShader.instance::getCosmicRenderTypeArmor));
                }
            );
        });
    }
}
