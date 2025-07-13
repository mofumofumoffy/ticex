package moffy.ticex.modules.draconicevolution;

import com.brandon3055.brandonscore.api.TechLevel;
import moffy.addonapi.AddonModule;
import moffy.ticex.caps.draconicevolution.DEItemCapabilityProvider;
import moffy.ticex.client.draconicevolution.TicEXDEShader;
import moffy.ticex.item.cores.ItemReconstCore;
import moffy.ticex.lib.TicEXMaterials;
import moffy.ticex.lib.utils.TicEXDEUtils;
import moffy.ticex.modifier.ModifierEvolved;
import moffy.ticex.modifier.ModifierSoulRending;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.tools.data.ModifierIds;

public class TicEXDEModule extends AddonModule {

    public TicEXDEModule() {
        ToolCapabilityProvider.register(DEItemCapabilityProvider::new);

        Item.Properties defaultProps = new Item.Properties();

        TicEXRegistry.DRACONIUM_CRYSTAL = TicEXRegistry.ITEMS.register("draconium_crystal", () -> new Item(defaultProps)
        );
        TicEXRegistry.WYVERN_CRYSTAL = TicEXRegistry.ITEMS.register("wyvern_crystal", () -> new Item(defaultProps));
        TicEXRegistry.DRACONIC_CRYSTAL = TicEXRegistry.ITEMS.register("draconic_crystal", () -> new Item(defaultProps));
        TicEXRegistry.CHAOTIC_CRYSTAL = TicEXRegistry.ITEMS.register("chaotic_crystal", () -> new Item(defaultProps));

        TicEXRegistry.DRACONIUM_EVOLVED_CORE = TicEXRegistry.ITEMS.register("draconium_evolved_core", () ->
            new ItemReconstCore(defaultProps, "evolved", 1)
        );
        TicEXRegistry.WYVERN_EVOLVED_CORE = TicEXRegistry.ITEMS.register("wyvern_evolved_core", () ->
            new ItemReconstCore(defaultProps, "evolved", 2)
        );
        TicEXRegistry.DRACONIC_EVOLVED_CORE = TicEXRegistry.ITEMS.register("draconic_evolved_core", () ->
            new ItemReconstCore(defaultProps, "evolved", 3)
        );
        TicEXRegistry.CHAOTIC_EVOLVED_CORE = TicEXRegistry.ITEMS.register("chaotic_evolved_core", () ->
            new ItemReconstCore(defaultProps, "evolved", 4)
        );
        TicEXRegistry.INJECT_CORE = TicEXRegistry.ITEMS.register("inject_core", () ->
            new ItemReconstCore(defaultProps, "inject")
        );

        TicEXRegistry.SOUL_RENDING_MODIFIER = TicEXRegistry.MODIFIERS.register(
            "soul_rending",
            ModifierSoulRending::new
        );
        TicEXRegistry.INJECT_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("inject");
        TicEXRegistry.EVOLVED_MODIFIER = TicEXRegistry.MODIFIERS.register("evolved", ModifierEvolved::new);

        DistExecutor.unsafeRunWhenOn(
            Dist.CLIENT,
            () -> this::initClient
        );
    }

    @OnlyIn(Dist.CLIENT)
    void initClient() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        TicEXDEShader.init(bus);

        TicEXRegistry.TOOL_SHADERS.addShader(ModifierIds.reinforced, wrapper -> {
            TicEXDEShader shader = TicEXDEShader.instance;
            TechLevel techLevel = TicEXDEUtils.getTechLevel(wrapper.getTool());
            if (techLevel != null && shader != null) {
                TicEXDEShader.glUniformBaseColor(
                        shader,
                    techLevel,
                    1F
                );
                wrapper.renderQuadsWithConsumer(
                    shader.getModifierRenderType(),
                    wrapper.getQuad(),
                    techLevel == TechLevel.CHAOTIC ? 0.9f : wrapper.getRed(),
                    wrapper.getGreen(),
                    wrapper.getBlue()
                );
            } else {
                wrapper.renderQuadsWithConsumer();
            }
        });

        TicEXRegistry.SHADER_INSTANCE_MAP.addShader(
            ModifierIds.reinforced,
            TicEXDEShader.instance::getShaderInstance
        );

        MaterialId[] materials = new MaterialId[] {
                TicEXMaterials.DRACONIUM,
                TicEXMaterials.WYVERN,
                TicEXMaterials.DRACONIC,
                TicEXMaterials.CHAOTIC
        };

        for (int i = 0; i < materials.length; i++) {
            MaterialId materialId = materials[i];
            TechLevel techLevel = TechLevel.VALUES[i];


            TicEXRegistry.TOOL_SHADERS.addShader(materialId, wrapper -> {
                TicEXDEShader shader = TicEXDEShader.instance;
                TicEXDEShader.glUniformBaseColor(shader, techLevel, 1.0F);

                wrapper.renderQuadsWithConsumer(
                        shader.getMaterialsRenderType(),
                        wrapper.getQuad(),
                        techLevel == TechLevel.CHAOTIC ? 0.9f : wrapper.getRed(),
                        wrapper.getGreen(),
                        wrapper.getBlue()
                );
            });

            TicEXRegistry.SHADER_INSTANCE_MAP.addShader(
                    materialId,
                    TicEXDEShader.instance::getShaderInstance,
                    () -> TicEXDEShader.instance.setup(techLevel)
            );
        }
    }
}
