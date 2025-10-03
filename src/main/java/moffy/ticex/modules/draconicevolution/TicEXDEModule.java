package moffy.ticex.modules.draconicevolution;

import com.brandon3055.brandonscore.api.TechLevel;
import moffy.addonapi.AddonModule;
import moffy.ticex.caps.draconicevolution.DEItemCapabilityProvider;
import moffy.ticex.client.modules.draconicevolution.TicEXDEShader;
import moffy.ticex.client.modules.draconicevolution.TicEXDEShaderProvider;
import moffy.ticex.client.rendering.PartPredicate;
import moffy.ticex.client.rendering.ticex.TicEXRenders;
import moffy.ticex.item.cores.ItemReconstCore;
import moffy.ticex.lib.TicEXMaterials;
import moffy.ticex.modifier.ModifierEvolved;
import moffy.ticex.modifier.ModifierSoulRending;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.tools.data.ModifierIds;

import java.util.List;
import java.util.Objects;

public class TicEXDEModule implements AddonModule {

    @Override
    public void init(FMLJavaModLoadingContext context) {
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
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient(FMLJavaModLoadingContext context) {
        IEventBus bus = context.getModEventBus();

        TicEXDEShaderProvider.init(bus);
        List<MaterialId> materials = List.of(
                TicEXMaterials.DRACONIUM,
                TicEXMaterials.WYVERN,
                TicEXMaterials.DRACONIC,
                TicEXMaterials.CHAOTIC
        );

        TicEXDEShader shader = Objects.requireNonNull(TicEXDEShaderProvider.getShader());
        TicEXRenders.TOOL_SHADERS.addShader(new PartPredicate.Modifier(ModifierIds.reinforced), new TicEXDEShaderProvider.Modifier());

        for (int i = 0; i < materials.size(); i++) {
            TechLevel techLevel = TechLevel.VALUES[i];
            TicEXRenders.TOOL_SHADERS.addShader(materials.get(i).getId(), new TicEXDEShaderProvider.Material(
                    shader.createMaterialsRenderType(),
                    techLevel
            ));
            TicEXRenders.ARMOR_SHADERS.addShader(materials.get(i).getId(), new TicEXDEShaderProvider.Armor(techLevel));
        }
    }
}
