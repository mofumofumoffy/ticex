package moffy.ticex.modules.draconicevolution;

import com.brandon3055.brandonscore.api.TechLevel;

import moffy.addonapi.AddonModule;
import moffy.ticex.caps.draconicevolution.DEItemCapabilityProvider;
import moffy.ticex.client.draconicevolution.TicEXDEShader;
import moffy.ticex.item.cores.ItemReconstCore;
import moffy.ticex.modifier.ModifierEvolved;
import moffy.ticex.modules.TicEXRegistry;
import moffy.ticex.utils.TicEXDEUtils;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.tools.data.ModifierIds;

public class TicEXDEModule extends AddonModule{

    public TicEXDEModule(){

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ToolCapabilityProvider.register(DEItemCapabilityProvider::new);

        Item.Properties defaultProps = new Item.Properties();

        TicEXRegistry.DRACONIUM_EVOLVED_CORE = TicEXRegistry.ITEMS.register("draconium_evolved_core", ()->new ItemReconstCore(defaultProps, "evolved", 1));
        TicEXRegistry.WYVERN_EVOLVED_CORE = TicEXRegistry.ITEMS.register("wyvern_evolved_core", ()->new ItemReconstCore(defaultProps, "evolved", 2));
        TicEXRegistry.DRACONIC_EVOLVED_CORE = TicEXRegistry.ITEMS.register("draconic_evolved_core", ()->new ItemReconstCore(defaultProps, "evolved", 3));
        TicEXRegistry.CHAOTIC_EVOLVED_CORE = TicEXRegistry.ITEMS.register("chaotic_evolved_core", ()->new ItemReconstCore(defaultProps, "evolved", 4));
        TicEXRegistry.INJECT_CORE = TicEXRegistry.ITEMS.register("inject_core", ()->new ItemReconstCore(defaultProps, "inject"));

        TicEXRegistry.EVOLVED_MODIFIER = TicEXRegistry.MODIFIERS.register("evolved", ModifierEvolved::new);
        
        TicEXDEShader.init(bus);

        TicEXRegistry.TOOL_SHADERS.addShader(
            ModifierIds.reinforced,
            (wrapper)->{
                TechLevel techLevel = TicEXDEUtils.getTechLevel(wrapper.getTool());
                if(techLevel != null && TicEXDEShader.instance != null){
                    TicEXDEShader.glUniformBaseColor(TicEXDEShader.instance, techLevel, 1F);
                    wrapper.renderQuadsWithConsumer(TicEXDEShader.instance.getRenderType(), wrapper.getQuad(), techLevel == TechLevel.CHAOTIC ? 0.9f : wrapper.getRed(), wrapper.getGreen(), wrapper.getBlue());
                }
            }
        );
    }
}
