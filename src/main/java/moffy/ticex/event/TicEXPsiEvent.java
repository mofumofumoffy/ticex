package moffy.ticex.event;

import moffy.ticex.lib.utils.TicEXPsiUtils;
import moffy.ticex.modifier.ModifierPsionizingRadiation;
import moffy.ticex.modifier.ModifierSensor;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import vazkii.psi.api.exosuit.PsiArmorEvent;

public class TicEXPsiEvent {

    public static void onPsiArmorEvent(PsiArmorEvent event){
        Player player = event.getEntity();
        player.getArmorSlots().forEach(armorStack->{
            if(armorStack.getItem() instanceof IModifiable){
                ToolStack armor = ToolStack.from(armorStack);
                String eventType = getEvent(armorStack);
                int timesCast = armor.getPersistentData().getInt(ModifierPsionizingRadiation.TIMES_CAST_LOC);

                if(eventType != null && event.type.equals(eventType)){
                    TicEXPsiUtils.CastSpell(player, armorStack, (context)->{
                        context.loopcastIndex = timesCast;
                    });
                }
            }
        });
    }

    public static String getEvent(ItemStack armorStack){
        if(armorStack.getItem() instanceof IModifiable && armorStack.getItem() instanceof ArmorItem armorItem){
            ToolStack armor = ToolStack.from(armorStack);
            if(armor.getModifierLevel(TicEXRegistry.PSIONIZING_RADIATION_MODIFIER.get()) > 0){
                switch (armorItem.getType()) {
                    case HELMET:
                        ModDataNBT persistentData = armor.getPersistentData();
                        if(persistentData.contains(ModifierSensor.EVENT_TYPE_LOC, Tag.TAG_STRING)){
                            return persistentData.getString(ModifierSensor.EVENT_TYPE_LOC);
                        }
                        return null;
                    case CHESTPLATE:
                        return PsiArmorEvent.DAMAGE;
                    case LEGGINGS:
                        return PsiArmorEvent.TICK;
                    case BOOTS:
                        return PsiArmorEvent.JUMP;
                }
            }
        }
        return null;
    }
}
