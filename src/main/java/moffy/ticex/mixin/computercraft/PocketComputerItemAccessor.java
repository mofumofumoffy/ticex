package moffy.ticex.mixin.computercraft;

import dan200.computercraft.shared.pocket.core.PocketServerComputer;
import dan200.computercraft.shared.pocket.items.PocketComputerItem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = PocketComputerItem.class, remap = false)
public interface PocketComputerItemAccessor {

    @Invoker("getServerComputer")
    static @Nullable PocketServerComputer invokeGetServerComputer(MinecraftServer server, ItemStack stack) {
        throw new IllegalStateException();
    }
}
