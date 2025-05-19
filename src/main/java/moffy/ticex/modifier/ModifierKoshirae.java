package moffy.ticex.modifier;

import java.util.EnumSet;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.SwordType;
import moffy.ticex.item.modifiable.ModifiableSlashBladeItem;
import moffy.ticex.lib.hook.EmbossmentModifierHook;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class ModifierKoshirae extends NoLevelsModifier implements EmbossmentModifierHook{
    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, TicEXRegistry.EMBOSSMENT_HOOK);
    }

    @Override
    public boolean applyItem(EmbossmentContext context, int inputIndex, boolean secondary) {
        ItemStack input = context.getInputStack(inputIndex);
        ItemStack toolStack = context.getToolStack();

        ToolStack resultTool = ToolStack.from(toolStack);
        EnumSet<SwordType> swordTypes = SwordType.from(toolStack);
        if(swordTypes.contains(SwordType.BEWITCHED)){
            toolStack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(resultState -> {{
                input.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(inputState -> {
                    resultState.deserializeNBT(inputState.serializeNBT());
                    resultTool.getPersistentData().put(ModifiableSlashBladeItem.BLADE_STATE_LOCATION, resultState.serializeNBT());
                });
            }});
            return true;
        }
        return false;
        
    }
}
