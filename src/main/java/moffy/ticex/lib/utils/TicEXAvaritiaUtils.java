package moffy.ticex.lib.utils;

import com.google.common.collect.Iterables;
import committee.nova.mods.avaritia.init.registry.ModDamageTypes;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class TicEXAvaritiaUtils {

    public static boolean isInfinityDamage(DamageSource source) {
        return source.is(ModDamageTypes.INFINITY);
    }

    public static boolean hasCelestial(Player player) {
        Iterable<ItemStack> slots = Iterables.concat(
                player.getArmorSlots(),
                player.getHandSlots()
        );

        for (ItemStack armorStack : slots) {
            if (armorStack.getItem() instanceof IModifiable) {
                ToolStack armor = ToolStack.from(armorStack);
                if (armor.getModifierLevel(TicEXRegistry.CELESTIAL_MODIFIER.get()) > 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
