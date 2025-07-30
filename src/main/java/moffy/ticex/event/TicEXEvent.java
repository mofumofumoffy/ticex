package moffy.ticex.event;

import moffy.ticex.caps.EmbossmentMaterialCapability;
import moffy.ticex.client.modules.ticex.PartOverrideModel;
import moffy.ticex.client.rendering.ticex.LayerResonanceTools;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.tools.item.IModifiable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class TicEXEvent {

    public static UUID EXTRA_DAMAGE_UUID = UUID.fromString("39f1e204-7c3b-4d51-9a3c-65e1db213f08");

    @SuppressWarnings("unchecked")
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        if (TicEXRegistry.DAMAGE_TAKEN != null && TicEXRegistry.HEALING_RECEIVED != null) {
            ForgeRegistries.ENTITY_TYPES.forEach(action -> {
                if (action.getBaseClass().isAssignableFrom(LivingEntity.class)) {
                    event.add((EntityType<? extends LivingEntity>) action, TicEXRegistry.DAMAGE_TAKEN.get());
                    event.add((EntityType<? extends LivingEntity>) action, TicEXRegistry.HEALING_RECEIVED.get());
                }
            });
        }
    }

    public static void onEntityHurt(LivingHurtEvent event) {
        float amount = event.getAmount();
        if (amount <= 0F) {
            return;
        }
        float newAmount = amount;

        Entity source = event.getSource().getEntity();
        LivingEntity target = event.getEntity();

        //damage bonus
        if (source instanceof LivingEntity livingSource) {
            ItemStack weapon = livingSource.getMainHandItem();
            float bonus = EnchantmentHelper.getDamageBonus(weapon, target.getMobType());
            newAmount += bonus;
        }

        //armor protection
        List<ItemStack> modifiableArmors = new ArrayList<>();
        Iterator<ItemStack> armors = target.getArmorSlots().iterator();
        while (armors.hasNext()) {
            ItemStack armor = armors.next();
            if (armor.getItem() instanceof IModifiable) {
                modifiableArmors.add(armor);
            }
        }
        int protection = EnchantmentHelper.getDamageProtection(modifiableArmors, event.getSource());
        if (protection > 0) {
            newAmount *= (1 - Math.min(20, protection) / 25.0f);
        }

        //attribute reduce
        AttributeInstance attributeInstance = target.getAttribute(TicEXRegistry.DAMAGE_TAKEN.get());
        if (attributeInstance != null) {
            double multiplier = attributeInstance.getValue();
            if (multiplier != 1D) {
                newAmount = Math.max(newAmount * (float) multiplier, 0F);
            }
        }

        event.setAmount(newAmount);
        if (newAmount < 0.01) {
            target.setDeltaMovement(0, 0, 0);
            target.hurtTime = 0;
            target.hurtDuration = 0;
            event.setCanceled(true);
        }
    }

    public static void onEntityHeal(LivingHealEvent event) {
        float amount = event.getAmount();
        if (amount <= 0F) {
            return;
        }
        LivingEntity entity = event.getEntity();
        AttributeInstance attributeInstance = entity.getAttribute(TicEXRegistry.HEALING_RECEIVED.get());
        if (attributeInstance != null) {
            double multiplier = attributeInstance.getValue();
            if (multiplier != 1D) {
                float newAmount = Math.max(amount * (float) multiplier, 0F);
                event.setAmount(newAmount);
                if (newAmount < 0.0001) {
                    event.setCanceled(true);
                }
            }
        }
    }

    public static void modifyAttribute(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        EquipmentSlot slot = event.getSlotType();
        if (stack.getItem() instanceof IModifiable) {
            if (slot == EquipmentSlot.MAINHAND) {
                AttributeModifier modifier = new AttributeModifier(
                    EXTRA_DAMAGE_UUID,
                    "Enchantment Bonus for Modifiable Item",
                    0,
                    Operation.ADDITION
                );

                if (!event.getModifiers().containsValue(modifier)) {
                    event.addModifier(Attributes.ATTACK_DAMAGE, modifier);
                } else {
                    event.removeModifier(Attributes.ATTACK_DAMAGE, modifier);
                }
            }
        }
    }

    public static void onRegisterCaps(RegisterCapabilitiesEvent event) {
        event.register(EmbossmentMaterialCapability.class);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        addPlayerLayer(event, "default");
        addPlayerLayer(event, "slim");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @OnlyIn(Dist.CLIENT)
    public static void addPlayerLayer(EntityRenderersEvent.AddLayers event, String skin) {
        EntityRenderer<? extends Player> renderer = event.getSkin(skin);
        if (renderer instanceof LivingEntityRenderer livingRenderer) {
            livingRenderer.addLayer(new LayerResonanceTools<>(livingRenderer));
        }
    }

    public static void registerModelLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register("overrides_obj", PartOverrideModel.LOADER);
    }
}
