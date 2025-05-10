package moffy.ticex.lib.utils;

import committee.nova.mods.avaritia.init.registry.ModDamageTypes;
import moffy.ticex.modifier.ModifierCelestial;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.modifiers.modules.technical.ArmorLevelModule;

public class TicEXAvaritiaUtils {
    public static boolean isInfinityDamage(DamageSource source){
        return source.is(ModDamageTypes.INFINITY);
    }

    public static boolean hasCelestial(Player player){
        return ArmorLevelModule.getLevel(player, ModifierCelestial.CELESTIAL_KEY) > 0;
    }
}
