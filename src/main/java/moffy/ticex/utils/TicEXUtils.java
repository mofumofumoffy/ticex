package moffy.ticex.utils;

import java.util.function.Predicate;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class TicEXUtils {
    public static boolean isPureDamage(DamageSource source, float damage){
        boolean isInfinityDamage = false;

        if(ModList.get().isLoaded("avaritia")){
            isInfinityDamage = TicEXAvaritiaUtils.isInfinityDamage(source);
        }

        return source.is(DamageTypes.FELL_OUT_OF_WORLD) || isInfinityDamage || (damage == Float.MAX_VALUE && source.is(DamageTypeTags.BYPASSES_ARMOR) && source.is(DamageTypeTags.BYPASSES_INVULNERABILITY));
    }

    public static boolean canPlayerFly(Player player){
        boolean canFly = false;

        if(ModList.get().isLoaded("avaritia")){
            canFly = canFly || TicEXAvaritiaUtils.hasCelestial(player);
        }

        return canFly;
    }

    public static ItemStack getToolStack(LivingEntity entity, Modifier modifier){
        return getToolStack(entity, (stack)->ToolStack.from(stack).getModifierLevel(modifier) > 0);
    }

    public static ItemStack getToolStack(LivingEntity entity, Predicate<ItemStack> predicate){
        ItemStack mainHandStack = entity.getMainHandItem();
        if(mainHandStack.getItem() instanceof IModifiable && predicate.test(mainHandStack)){
            return mainHandStack;
        } else {
            ItemStack offHandStack = entity.getOffhandItem();
            if(offHandStack.getItem() instanceof IModifiable && predicate.test(offHandStack)){
                return offHandStack;
            }
        }
        return ItemStack.EMPTY;
    }

    
}
