package moffy.ticex.modules.general;

import moffy.addonapi.AddonModule;
import moffy.ticex.lib.utils.TicEXFluidUtils;
import moffy.ticex.modifier.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class TicEXMaterialModule implements AddonModule {
    @Override
    public void init(FMLJavaModLoadingContext context) {
        TicEXRegistry.ETHERIC_INGOT = TicEXRegistry.ITEMS.register("etheric_ingot", () ->
                new Item(new Item.Properties())
        );

        TicEXRegistry.ETHERIC_BLOCK = TicEXRegistry.BLOCKS.register("etheric_block", () ->
                new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).noOcclusion())
        );

        TicEXRegistry.ITEMS.register("etheric_block", () ->
                new BlockItem(TicEXRegistry.ETHERIC_BLOCK.get(), new Item.Properties())
        );

        TicEXRegistry.MOLTEN_ETHERIC = TicEXRegistry.FLUIDS.register("molten_etheric")
                .type(TicEXFluidUtils.hot("molten_etheric").temperature(1000).density(1600))
                .block(MapColor.COLOR_LIGHT_GREEN, 0)
                .bucket()
                .commonTag()
                .flowing();

        TicEXRegistry.OD_INGOT = TicEXRegistry.ITEMS.register("od_ingot", () ->
                new Item(new Item.Properties())
        );

        TicEXRegistry.OD_BLOCK = TicEXRegistry.BLOCKS.register("od_block", () ->
                new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).noOcclusion())
        );

        TicEXRegistry.ITEMS.register("od_block", () ->
                new BlockItem(TicEXRegistry.OD_BLOCK.get(), new Item.Properties())
        );

        TicEXRegistry.MOLTEN_OD = TicEXRegistry.FLUIDS.register("molten_od")
                .type(TicEXFluidUtils.hot("molten_od").temperature(1000).density(1600))
                .block(MapColor.COLOR_LIGHT_GREEN, 0)
                .bucket()
                .commonTag()
                .flowing();

        TicEXRegistry.DEFLECTION_MODIFIER = TicEXRegistry.MODIFIERS.register("deflection", ModifierDeflection::new);
        TicEXRegistry.SASSY_MODIFIER = TicEXRegistry.MODIFIERS.register("sassy", ModifierSassy::new);
        TicEXRegistry.AFLOAT_MODIFIER = TicEXRegistry.MODIFIERS.register("afloat", ModifierAfloat::new);
        TicEXRegistry.DUNGEON_MASTER_MODIFIER = TicEXRegistry.MODIFIERS.register("dungeon_master", ModifierDungeonMaster::new);
        TicEXRegistry.UNRAVEL_MODIFIER = TicEXRegistry.MODIFIERS.register("unravel", ModifierUnravel::new);
        TicEXRegistry.EMBOSSMENT_MODIFIER = TicEXRegistry.MODIFIERS.register("embossment", ModifierEmbossment::new);
    }
}
