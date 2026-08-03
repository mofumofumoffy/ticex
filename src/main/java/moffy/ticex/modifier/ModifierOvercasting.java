package moffy.ticex.modifier;

import java.util.Map;
import java.util.function.BiFunction;
import moffy.ticex.lib.hook.ProvidePropertyModifierHook;
import moffy.ticex.registry.TicEXModifierHooks;
import moffy.ticex.modifier.propeties.OvercastingProperty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;

public class ModifierOvercasting extends NoLevelsModifier implements ProvidePropertyModifierHook {

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, TicEXModifierHooks.PROPERTY_PROVIDER);
    }

    @Override
    public BiFunction<Player, ItemStack, Map<String, Object>> getPropertyProvider() {
        return OvercastingProperty.getProperties();
    }
}
