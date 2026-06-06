package moffy.ticex.modifier;

import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.utils.ToolHelper;
import net.minecraft.core.Direction;
import net.minecraftforge.event.entity.player.PlayerEvent;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ModifierCluster extends Modifier implements BreakSpeedModifierHook {

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.BREAK_SPEED);
    }

    @Override
    public void onBreakSpeed(IToolStackView iToolStackView, ModifierEntry modifierEntry, PlayerEvent.BreakSpeed breakSpeed, Direction direction, boolean b, float v) {
        EnumMatterType matterType = modifierEntry.getLevel() > 1 ? EnumMatterType.RED_MATTER :EnumMatterType.DARK_MATTER;
        if(ToolHelper.canMatterMine(matterType, breakSpeed.getState().getBlock())){
            breakSpeed.setNewSpeed(Math.max(breakSpeed.getNewSpeed(), 1_200_000));
        }
    }
}
