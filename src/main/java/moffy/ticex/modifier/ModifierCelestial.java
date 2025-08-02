package moffy.ticex.modifier;

import moffy.ticex.lib.utils.TicEXAvaritiaUtils;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.modules.technical.ArmorLevelModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ModifierCelestial extends NoLevelsModifier implements EquipmentChangeModifierHook {

    public static final TinkerDataKey<Integer> CELESTIAL_KEY = TConstruct.createKey("celestial");

    @Override
    protected void registerHooks(@NotNull Builder hookBuilder) {
        super.registerHooks(hookBuilder);
        hookBuilder.addModule(new ArmorLevelModule(CELESTIAL_KEY, false, null));
        hookBuilder.addHook(this, ModifierHooks.EQUIPMENT_CHANGE);
    }

    @Override
    public void onEquip(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @NotNull EquipmentChangeContext context) {
        if (context.getEntity() instanceof Player player) {
            Abilities abilities = player.getAbilities();
            abilities.mayfly = true;
            player.onUpdateAbilities();
        }
    }

    @Override
    public void onUnequip(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @NotNull EquipmentChangeContext context) {
        if (context.getEntity() instanceof Player player) {
            if (!TicEXAvaritiaUtils.hasCelestial(player) && !player.isCreative()) {
                Abilities abilities = player.getAbilities();
                abilities.mayfly = false;
                abilities.flying = false;
                player.onUpdateAbilities();
            }
        }
    }
}
