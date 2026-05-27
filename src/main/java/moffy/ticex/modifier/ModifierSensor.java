package moffy.ticex.modifier;

import moffy.ticex.TicEX;
import moffy.ticex.lib.hook.EmbossmentModifierHook;
import moffy.ticex.lib.hook.TicEXModifierHooks;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import vazkii.psi.common.item.ItemExosuitSensor;

public class ModifierSensor extends NoLevelsModifier implements EmbossmentModifierHook {

    public static final ResourceLocation EVENT_TYPE_LOC = TicEX.getResource("psieventtype");
    public static final ResourceLocation TIMES_CAST_LOC = TicEX.getResource("timescast");

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, TicEXModifierHooks.EMBOSSMENT);
    }

    @Override
    public boolean applyItem(EmbossmentContext context, int inputIndex, boolean secondary) {
        ItemStack toolStack = context.getToolStack();
        ItemStack inputStack = context.getInputStack(inputIndex);
        if (inputStack.getItem() instanceof ItemExosuitSensor sensor) {
            ModDataNBT persistentData = ToolStack.from(toolStack).getPersistentData();
            persistentData.putString(EVENT_TYPE_LOC, sensor.getEventType(inputStack));
            return true;
        }
        return false;
    }

    @Override
    public Component getDisplayName(IToolStackView tool, ModifierEntry entry, RegistryAccess access) {
        return entry
            .getDisplayName()
            .copy()
            .append("(")
            .append(Component.translatable(tool.getPersistentData().getString(EVENT_TYPE_LOC)))
            .append(")")
            .withStyle(entry.getDisplayName().getStyle());
    }
}
