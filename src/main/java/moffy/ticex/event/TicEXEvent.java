package moffy.ticex.event;

import moffy.ticex.caps.EmbossmentMaterialCapability;
import moffy.ticex.modules.TicEXRegistry;
import moffy.ticex.utils.TicEXAvaritiaUtils;
import moffy.ticex.utils.TicEXUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class TicEXEvent {

    public static void onEntityHurt(LivingHurtEvent event){

        //attribute
        float damage = event.getAmount();
        if (damage <= 0F || TicEXUtils.isPureDamage(event.getSource(), damage)) {
            return;
        }
        LivingEntity entity = event.getEntity();
        AttributeInstance attributeInstance = entity.getAttribute(TicEXRegistry.DAMAGE_TAKEN.get());
        if(attributeInstance != null){
            double multiplier = attributeInstance.getValue();
            if (multiplier != 1D) {
                float newAmount = Math.max(damage * (float)multiplier, 0F);
                event.setAmount(newAmount);
                if(newAmount < 0.0001){
                    entity.setDeltaMovement(0, 0, 0);
                    entity.hurtTime = 0;
                    entity.hurtDuration = 0;
                    event.setCanceled(true);
                }
            }
        }
    }

    public static void onEntityHeal(LivingHealEvent event){
        float amount = event.getAmount();
        if (amount <= 0F) {
            return;
        }
        LivingEntity entity = event.getEntity();
        AttributeInstance attributeInstance = entity.getAttribute(TicEXRegistry.HEALING_RECEIVED.get());
        if(attributeInstance != null){
            double multiplier = attributeInstance.getValue();
            if (multiplier != 1D) {
                float newAmount = Math.max(amount * (float)multiplier, 0F);
                event.setAmount(newAmount);
                if(newAmount < 0.0001){
                    event.setCanceled(true);
                }
            }
        }
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if(!player.isCreative() && TicEXAvaritiaUtils.hasCelestial(player)){
            if (TicEXUtils.canPlayerFly(player) && !player.getAbilities().mayfly) {
                player.getAbilities().mayfly = true;
                player.onUpdateAbilities();
            } else if(!TicEXUtils.canPlayerFly(player) && player.getAbilities().mayfly){
                player.getAbilities().mayfly = false;
                player.onUpdateAbilities();
            }
        }
    }

    public static void onRegisterCaps(RegisterCapabilitiesEvent event){
        event.register(EmbossmentMaterialCapability.class);
    }
}
