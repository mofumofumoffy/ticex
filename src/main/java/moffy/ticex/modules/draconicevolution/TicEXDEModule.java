package moffy.ticex.modules.draconicevolution;

import com.brandon3055.brandonscore.api.TechLevel;
import moffy.addonapi.AddonModule;
import moffy.ticex.caps.draconicevolution.DEItemCapabilityProvider;
import moffy.ticex.client.modules.draconicevolution.TicEXDEShader;
import moffy.ticex.client.modules.draconicevolution.TicEXDEShaderProvider;
import moffy.ticex.client.render.custom.PartPredicate;
import moffy.ticex.client.render.ticex.TicEXRenders;
import moffy.ticex.item.cores.ItemReconstCore;
import moffy.ticex.lib.TicEXMaterials;
import moffy.ticex.modifier.ModifierDragonForce;
import moffy.ticex.modifier.ModifierEvolved;
import moffy.ticex.modifier.ModifierSoulRending;
import moffy.ticex.registry.TicEXRegistry;
import moffy.ticex.registry.TicEXItems;
import moffy.ticex.registry.TicEXModifiers;
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

        TicEXItems.DRACONIUM_CRYSTAL = TicEXRegistry.ITEMS.register("draconium_crystal", () -> new Item(defaultProps)
        );
        TicEXItems.WYVERN_CRYSTAL = TicEXRegistry.ITEMS.register("wyvern_crystal", () -> new Item(defaultProps));
        TicEXItems.DRACONIC_CRYSTAL = TicEXRegistry.ITEMS.register("draconic_crystal", () -> new Item(defaultProps));
        TicEXItems.CHAOTIC_CRYSTAL = TicEXRegistry.ITEMS.register("chaotic_crystal", () -> new Item(defaultProps));

        TicEXItems.DRACONIUM_EVOLVED_CORE = TicEXRegistry.ITEMS.register("draconium_evolved_core", () ->
                new ItemReconstCore(defaultProps, "evolved", 1)
        );
        TicEXItems.WYVERN_EVOLVED_CORE = TicEXRegistry.ITEMS.register("wyvern_evolved_core", () ->
                new ItemReconstCore(defaultProps, "evolved", 2)
        );
        TicEXItems.DRACONIC_EVOLVED_CORE = TicEXRegistry.ITEMS.register("draconic_evolved_core", () ->
                new ItemReconstCore(defaultProps, "evolved", 3)
        );
        TicEXItems.CHAOTIC_EVOLVED_CORE = TicEXRegistry.ITEMS.register("chaotic_evolved_core", () ->
                new ItemReconstCore(defaultProps, "evolved", 4)
        );
        TicEXItems.INJECT_CORE = TicEXRegistry.ITEMS.register("inject_core", () ->
                new ItemReconstCore(defaultProps, "inject")
        );

        TicEXModifiers.SOUL_RENDING_MODIFIER = TicEXRegistry.MODIFIERS.register(
                "soul_rending",
                ModifierSoulRending::new
        );
        TicEXModifiers.DRAGON_FORCE_MODIFIER = TicEXRegistry.MODIFIERS.register(
                "dragon_force",
                ModifierDragonForce::new
        );

        TicEXModifiers.INJECT_MODIFIER = TicEXRegistry.MODIFIERS.registerDynamic("inject");
        TicEXModifiers.EVOLVED_MODIFIER = TicEXRegistry.MODIFIERS.register("evolved", ModifierEvolved::new);
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
            MaterialId variantId = materials.get(i);
            TechLevel techLevel = TechLevel.VALUES[i];

            TicEXRenders.TOOL_SHADERS.addShader(variantId, new TicEXDEShaderProvider.Material(
                    shader.createMaterialsRenderType(techLevel),
                    techLevel
            ));
            TicEXRenders.ARMOR_SHADERS.addShader(variantId, new TicEXDEShaderProvider.Armor(techLevel));
            TicEXRenders.GENERIC_SHADERS.addShader(new PartPredicate.Material(variantId), new TicEXDEShaderProvider.Generic(
                    shader.createMaterialsRenderType(techLevel),
                    techLevel
            ));
        }
    }
}
