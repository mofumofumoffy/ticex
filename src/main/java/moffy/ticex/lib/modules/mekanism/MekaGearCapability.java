package moffy.ticex.lib.modules.mekanism;

import mekanism.common.capabilities.ItemCapabilityWrapper;
import moffy.ticex.lib.modules.mekanism.interfaces.IMekaGear;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class MekaGearCapability implements IMekaGear {
    public static final Capability<IMekaGear> MEKA_GEAR_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
}
