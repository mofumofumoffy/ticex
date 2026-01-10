package moffy.ticex.modifier;

import java.util.List;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ValidateModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.RequirementsModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ModifierSocket
    extends Modifier
    implements ValidateModifierHook, ModifierRemovalHook, RequirementsModifierHook {

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.VALIDATE, ModifierHooks.REMOVE, ModifierHooks.REQUIREMENTS);
    }

    @Override
    public Component validate(IToolStackView tool, ModifierEntry entry) {
        if (tool.getModifierLevel(TicEXRegistry.PSIONIZING_RADIATION_MODIFIER.get()) < 1) {
            return Component.translatable("recipe.ticex.modifier.socket_requirement");
        } else {
            tool.getPersistentData().putInt(ModifierPsionizingRadiation.SOCKETS_LOC, 1 + entry.getLevel());
            return null;
        }
    }

    @Override
    public Component onRemoved(IToolStackView tool, Modifier modifier) {
        if (modifier == this) {
            tool.getPersistentData().putInt(ModifierPsionizingRadiation.SOCKETS_LOC, 1);
        }
        return null;
    }

    @Override
    public List<ModifierEntry> displayModifiers(ModifierEntry entry) {
        return List.of(new ModifierEntry(TicEXRegistry.PSIONIZING_RADIATION_MODIFIER.get(), 1));
    }
}
