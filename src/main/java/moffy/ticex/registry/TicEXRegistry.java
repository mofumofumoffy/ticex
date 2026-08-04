package moffy.ticex.registry;

import moffy.ticex.TicEX;
import moffy.ticex.lib.registry.TicEXItemDeferredRegisterExtension;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.*;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.registration.deferred.FluidDeferredRegister;
import slimeknights.mantle.registration.deferred.MenuTypeDeferredRegister;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;

import java.util.function.Supplier;

public class TicEXRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TicEX.MODID);
    public static final TicEXItemDeferredRegisterExtension ITEMS_EXTENDED = new TicEXItemDeferredRegisterExtension(
            ITEMS,
            TicEX.MODID
    );
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TicEX.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(
            ForgeRegistries.BLOCK_ENTITY_TYPES,
            TicEX.MODID
    );
    public static final FluidDeferredRegister FLUIDS = new FluidDeferredRegister(TicEX.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(
            ForgeRegistries.ENTITY_TYPES,
            TicEX.MODID
    );
    public static final ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(TicEX.MODID);
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(
            ForgeRegistries.ATTRIBUTES,
            TicEX.MODID
    );
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB,
            TicEX.MODID
    );
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(
            Registries.RECIPE_SERIALIZER,
            TicEX.MODID
    );
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(
            Registries.RECIPE_TYPE,
            TicEX.MODID
    );
    public static final MenuTypeDeferredRegister MENUS = new MenuTypeDeferredRegister(
            TicEX.MODID
    );

    public static RegistryObject<CreativeModeTab> CREATIVE_TAB_ITEMS = null;

    public static Tier INFINITY_TIER;

    public static void addTabItems(ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
        for (RegistryObject<Item> itemObject : ITEMS.getEntries()) {
            Item item = itemObject.get();
            if (!(item instanceof ToolPartItem || item instanceof IModifiable || item instanceof ArrowItem)) {
                output.accept(itemObject.get());
            }
        }

        for (RegistryObject<Block> blockObject : BLOCKS.getEntries()) {
            output.accept(blockObject.get().asItem());
        }

        acceptCatalystArmor(output, TicEXItems.CATALYST_MEKASUIT);
        acceptCatalystArmor(output, TicEXItems.CATALYST_GEM);
        acceptPart(output, TicEXItems.CATALYST_SLASHBLADE);
        acceptPart(output, TicEXItems.CATALYST_MEKA_TOOL);
        acceptPart(output, TicEXItems.CATALYST_MEKA_TANA);
        acceptPart(output, TicEXItems.CATALYST_MEKA_BOW);
        //acceptPart(output, CATALYST_KINETIC_GUN);
        //acceptPart(output, CATALYST_IRONS_SPELLBOOK);

        acceptPart(output, TicEXItems.SLASHBLADE_BLADE);
        acceptPart(output, TicEXItems.SLASHBLADE_SAYA);

        acceptTool(output, TicEXItems.MEKA_EDGE);
        acceptTool(output, TicEXItems.RESONANCE_GAUNTLET);
        acceptTool(output, TicEXItems.REFORGED_SLASHBLADE);
        //acceptTool(output, BLITZ_GUN);
        //acceptTool(output, REVIVAL_SPELLBOOK_IRONS);

        //acceptArmor(output, MEKAPLATE_ARMOR);
        //acceptArmor(output, SINGULAR_GEM_ARMOR);

        acceptCast(output, TicEXItems.SLASHBLADE_BLADE_CAST);
        acceptCast(output, TicEXItems.SLASHBLADE_SAYA_CAST);
    }

    private static void acceptTool(CreativeModeTab.Output output, Supplier<? extends Item> toolObject) {
        if (toolObject != null) {
            Item item = toolObject.get();
            if (item instanceof IModifiable) {
                ToolBuildHandler.addVariants(output::accept, (IModifiable) item, "");
            }
        }
    }

    private static void acceptArmor(CreativeModeTab.Output output, EnumObject<?, ? extends IModifiable> armorObject) {
        if (armorObject != null) {
            armorObject.forEach(obj -> ToolBuildHandler.addVariants(output::accept, obj, ""));
        }
    }

    private static void acceptCatalystArmor(
            CreativeModeTab.Output output,
            EnumObject<ArmorItem.Type, ToolPartItem> catalystObject
    ) {
        if (catalystObject != null) {
            catalystObject.forEach(c -> c.addVariants(output::accept, ""));
        }
    }

    private static void acceptPart(CreativeModeTab.Output output, ItemObject<ToolPartItem> partObject) {
        if (partObject != null) {
            partObject.get().addVariants(output::accept, "");
        }
    }

    private static void acceptCast(CreativeModeTab.Output output, CastItemObject castObject) {
        if (castObject != null) {
            output.accept(castObject.get());
            output.accept(castObject.getRedSand());
            output.accept(castObject.getSand());
        }
    }
}
