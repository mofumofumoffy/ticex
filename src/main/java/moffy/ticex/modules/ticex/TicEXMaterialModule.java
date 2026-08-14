package moffy.ticex.modules.ticex;

import moffy.addonapi.AddonModule;
import moffy.ticex.lib.utils.TicEXFluidUtils;
import moffy.ticex.modifier.*;
import moffy.ticex.registry.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class TicEXMaterialModule implements AddonModule {
    @Override
    public void init(FMLJavaModLoadingContext context) {
        TicEXItems.ETHERIC_INGOT = TicEXRegistry.ITEMS.register("etheric_ingot", () ->
                new Item(new Item.Properties())
        );

        TicEXBlocks.ETHERIC_BLOCK = TicEXRegistry.BLOCKS.register("etheric_block", () ->
                new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).noOcclusion())
        );

        TicEXRegistry.ITEMS.register("etheric_block", () ->
                new BlockItem(TicEXBlocks.ETHERIC_BLOCK.get(), new Item.Properties())
        );

        TicEXItems.ETHERIC_NUGGET = TicEXRegistry.ITEMS.register("etheric_nugget", () -> new Item(new Item.Properties()));

        TicEXFluids.MOLTEN_ETHERIC = TicEXRegistry.FLUIDS.register("molten_etheric")
                .type(TicEXFluidUtils.hot("molten_etheric").temperature(1000).density(1600))
                .block(MapColor.COLOR_LIGHT_GREEN, 0)
                .bucket()
                .commonTag()
                .flowing();

        TicEXItems.OD_INGOT = TicEXRegistry.ITEMS.register("od_ingot", () ->
                new Item(new Item.Properties())
        );

        TicEXBlocks.OD_BLOCK = TicEXRegistry.BLOCKS.register("od_block", () ->
                new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).noOcclusion())
        );

        TicEXRegistry.ITEMS.register("od_block", () ->
                new BlockItem(TicEXBlocks.OD_BLOCK.get(), new Item.Properties())
        );

        TicEXItems.OD_NUGGET = TicEXRegistry.ITEMS.register("od_nugget", () -> new Item(new Item.Properties()));

        TicEXFluids.MOLTEN_OD = TicEXRegistry.FLUIDS.register("molten_od")
                .type(TicEXFluidUtils.hot("molten_od").temperature(1000).density(1600))
                .block(MapColor.COLOR_LIGHT_GREEN, 0)
                .bucket()
                .commonTag()
                .flowing();

        TicEXModifiers.DEFLECTION_MODIFIER = TicEXRegistry.MODIFIERS.register("deflection", ModifierDeflection::new);
        TicEXModifiers.SASSY_MODIFIER = TicEXRegistry.MODIFIERS.register("sassy", ModifierSassy::new);
        TicEXModifiers.AFLOAT_MODIFIER = TicEXRegistry.MODIFIERS.register("afloat", ModifierAfloat::new);
        TicEXModifiers.DUNGEON_MASTER_MODIFIER = TicEXRegistry.MODIFIERS.register("dungeon_master", ModifierDungeonMaster::new);
        TicEXModifiers.UNRAVEL_MODIFIER = TicEXRegistry.MODIFIERS.register("unravel", ModifierUnravel::new);
        TicEXModifiers.TELESCOPE_MODIFIER = TicEXRegistry.MODIFIERS.register("telescope", ModifierTelescope::new);
        TicEXModifiers.PLANETARIUM_MODIFIER = TicEXRegistry.MODIFIERS.register("planetarium", ModifierPlanetarium::new);
        TicEXModifiers.EMBOSSMENT_MODIFIER = TicEXRegistry.MODIFIERS.register("embossment", ModifierEmbossment::new);
    }
}
