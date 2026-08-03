package moffy.ticex.registry;

import moffy.ticex.block.transmuter.container.FluidTransmuterContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.tconstruct.tools.menu.ToolContainerMenu;

public class TicEXMenuTypes {
    public static RegistryObject<MenuType<ToolContainerMenu>> UNSYNCED_TOOL_CONTAINER = null;
    public static RegistryObject<MenuType<FluidTransmuterContainerMenu>> FLUID_TRANSMUTER_MENU = null;
}
