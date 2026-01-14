package moffy.ticex.caps.mekanism;

import mekanism.api.gear.IModule;
import mekanism.api.math.FloatingLong;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.IBlastingItem;
import mekanism.common.content.gear.mekatool.ModuleBlastingUnit;
import mekanism.common.content.gear.shared.ModuleEnergyUnit;
import mekanism.common.registries.MekanismModules;
import moffy.ticex.TicEX;
import moffy.ticex.lib.modules.mekanism.MekaGearCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;
import java.util.Map;

public class MekaToolGearCapability extends MekaGearCapability implements IBlastingItem {
    @Override
    public ResourceLocation getRadialId() {
        return TicEX.getResource("tinker_tool");
    }

    @Override
    public FloatingLong getChargeRate(ItemStack stack) {
        IModule<ModuleEnergyUnit> module = getModule(stack, MekanismModules.ENERGY_UNIT);
        return module == null ? MekanismConfig.gear.mekaToolBaseChargeRate.get() : module.getCustomInstance().getChargeRate(module);
    }

    @Override
    public FloatingLong getMaxEnergy(ItemStack stack) {
        IModule<ModuleEnergyUnit> module = getModule(stack, MekanismModules.ENERGY_UNIT);
        return module == null ? MekanismConfig.gear.mekaToolBaseEnergyCapacity.get() : module.getCustomInstance().getEnergyCapacity(module);
    }

    @Override
    public Map<BlockPos, BlockState> getBlastedBlocks(Level world, Player player, ItemStack stack, BlockPos pos, BlockState state) {
        if (!player.isShiftKeyDown()) {
            IModule<ModuleBlastingUnit> blastingUnit = getModule(stack, MekanismModules.BLASTING_UNIT);
            if (blastingUnit != null && blastingUnit.isEnabled()) {
                int radius = blastingUnit.getCustomInstance().getBlastRadius();
                if (radius > 0 && IBlastingItem.canBlastBlock(world, pos, state)) {
                    return IBlastingItem.findPositions(world, pos, player, radius);
                }
            }
        }
        return Collections.emptyMap();
    }
}
