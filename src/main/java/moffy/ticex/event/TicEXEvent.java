package moffy.ticex.event;

import java.util.UUID;

import moffy.ticex.caps.EmbossmentMaterialCapability;
import moffy.ticex.lib.utils.TicEXApotheosisUtils;
import moffy.ticex.lib.utils.TicEXAvaritiaUtils;
import moffy.ticex.lib.utils.TicEXUtils;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public class TicEXEvent {

    private static UUID modifierUUID = UUID.fromString("841a954a-1deb-4c01-925f-973d9e265bf5");

    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event){
        event.put((EntityType<? extends LivingEntity>)TicEXRegistry.FAKE_LIVING_ENTITY.get(), AttributeSupplier.builder().add(Attributes.MAX_HEALTH, Float.MAX_VALUE).build());
    }

    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        if(TicEXRegistry.DAMAGE_TAKEN != null && TicEXRegistry.HEALING_RECEIVED != null){
            ForgeRegistries.ENTITY_TYPES.forEach(action->{
                if(action.getBaseClass().isAssignableFrom(LivingEntity.class)){
                    event.add((EntityType<? extends LivingEntity>)action, TicEXRegistry.DAMAGE_TAKEN.get());
                    event.add((EntityType<? extends LivingEntity>)action, TicEXRegistry.HEALING_RECEIVED.get());
                }
            });
        }
    }

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
        AttributeInstance attr = null;
        AttributeModifier modifier = null;
        if(ModList.get().isLoaded("attributeslib")){
            attr = player.getAttributes().getInstance(TicEXApotheosisUtils.ApothicAttributes.CREATIVE_FLIGHT.get());
            modifier = new AttributeModifier(modifierUUID, "celestial", 1, AttributeModifier.Operation.ADDITION);
        }
        if(!(player.isCreative() || player.isSpectator()) && TicEXAvaritiaUtils.hasCelestial(player)){
            if (TicEXUtils.canPlayerFly(player) && !player.getAbilities().mayfly) {
                player.getAbilities().mayfly = true;
                if(ModList.get().isLoaded("attributeslib")){
                    if(!attr.hasModifier(modifier)){
                        attr.addPermanentModifier(modifier);
                    }
                }
                player.onUpdateAbilities();
            } else if(!TicEXUtils.canPlayerFly(player) && player.getAbilities().mayfly){
                player.getAbilities().mayfly = false;
                if(ModList.get().isLoaded("attributeslib")){
                    attr.removeModifier(modifierUUID);
                }
                player.onUpdateAbilities();
            }
        }else if(player.isCreative() || player.isSpectator()){
            player.getAbilities().mayfly = true;
            if(ModList.get().isLoaded("attributeslib")){
                if(!attr.hasModifier(modifier)){
                    attr.addPermanentModifier(modifier);
                }
            }
            player.onUpdateAbilities();
        }else{
            player.getAbilities().mayfly = false;
            if(ModList.get().isLoaded("attributeslib")){
                attr.removeModifier(modifierUUID);
            }
            player.onUpdateAbilities();
        }
    }

    public static void onRegisterCaps(RegisterCapabilitiesEvent event){
        event.register(EmbossmentMaterialCapability.class);
    }
}
