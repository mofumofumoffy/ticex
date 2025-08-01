package moffy.ticex.lib.utils;

import java.util.function.Predicate;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class TicEXUtils {

    public static boolean isPureDamage(DamageSource source, float damage) {
        boolean isInfinityDamage = false;

        if (ModList.get().isLoaded("avaritia")) {
            isInfinityDamage = TicEXAvaritiaUtils.isInfinityDamage(source);
        }

        return (
            source.is(DamageTypes.FELL_OUT_OF_WORLD) ||
            isInfinityDamage ||
            (damage == Float.MAX_VALUE &&
                source.is(DamageTypeTags.BYPASSES_ARMOR) &&
                source.is(DamageTypeTags.BYPASSES_INVULNERABILITY))
        );
    }

    public static boolean canPlayerFly(Player player) {
        boolean canFly = false; // player.getAbilities().mayfly;

        if (ModList.get().isLoaded("avaritia")) {
            canFly = canFly || TicEXAvaritiaUtils.hasCelestial(player);
        }

        return canFly;
    }

    public static ItemStack getToolStack(IToolStackView tool, LivingEntity entity, Modifier modifier) {
        return getToolStack(tool, entity, stack -> ToolStack.from(stack).getModifierLevel(modifier) > 0);
    }

    public static ItemStack getToolStack(IToolStackView tool, LivingEntity entity, Predicate<ItemStack> predicate) {
        if (tool instanceof ToolStack) {
            return ((ToolStack) tool).createStack();
        }
        ItemStack mainHandStack = entity.getMainHandItem();
        if (mainHandStack.getItem() instanceof IModifiable && predicate.test(mainHandStack)) {
            return mainHandStack;
        }
        ItemStack offHandStack = entity.getOffhandItem();
        if (offHandStack.getItem() instanceof IModifiable && predicate.test(offHandStack)) {
            return offHandStack;
        }

        if (ModList.get().isLoaded("curios")) {
            ItemStack curioStack = TicEXCuriosUtils.getToolStackInCurios(entity, predicate);
            if (curioStack != null) {
                return curioStack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static String getEquipmentSlotName(LivingEntity entity, ItemStack stack) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (ItemStack.isSameItem(entity.getItemBySlot(slot), stack)) {
                return slot.getName();
            }
        }

        if (ModList.get().isLoaded("curios")) {
            String curioName = TicEXCuriosUtils.getEquipmentSlotNameInCurios(entity, stack);
            if (curioName != null) {
                return curioName;
            }
        }

        return null;
    }
}
