package moffy.ticex.modifier;

import java.util.function.Predicate;

import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.ranged.BowAmmoModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ModifierEndestShot extends NoLevelsModifier implements BowAmmoModifierHook {

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.BOW_AMMO);
    }

    @Override
    public int getPriority() {
        return 65;
    }

    @Override
    public ItemStack findAmmo(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack standardAmmo, Predicate<ItemStack> ammoPredicate) {
        ItemStack itemstack = new ItemStack(TicEXRegistry.ENDESTSHOT_ARROW.get());
        itemstack.setCount(64);
        return itemstack;
    }

    @Override
    public void shrinkAmmo(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack ammo, int needed) {
        ToolDamageUtil.damageAnimated(tool, 16 * needed, shooter, shooter.getUsedItemHand());
    }
}
