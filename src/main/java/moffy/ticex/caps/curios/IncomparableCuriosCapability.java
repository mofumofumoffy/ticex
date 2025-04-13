package moffy.ticex.caps.curios;

import java.util.Map;

import com.google.common.collect.Multimap;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class IncomparableCuriosCapability implements ICurio{
    public static final Capability<IncomparableCuriosCapability> INCOMPARABLE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    protected final ItemStack stack;
    protected final IToolStackView tool;

    public IncomparableCuriosCapability(ItemStack stack, IToolStackView tool){
        this.stack = stack;
        this.tool = tool;
    }

    @Override
    public ItemStack getStack() {
        return this.stack;
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack) {
        ICurio.super.onEquip(slotContext, prevStack);
        if (slotContext.entity() instanceof Player player) {
            Multimap<Attribute, AttributeModifier> modifiers = stack.getAttributeModifiers(resolveSlot(slotContext));
            for (Map.Entry<Attribute, AttributeModifier> entry : modifiers.entries()) {
                player.getAttribute(entry.getKey()).addTransientModifier(entry.getValue());
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack) {
        ICurio.super.onUnequip(slotContext, newStack);
        if (slotContext.entity() instanceof Player player) {
            Multimap<Attribute, AttributeModifier> modifiers = stack.getAttributeModifiers(resolveSlot(slotContext));
            for (Map.Entry<Attribute, AttributeModifier> entry : modifiers.entries()) {
                player.getAttribute(entry.getKey()).removeModifier(entry.getValue());
            }
        }
    }

    public EquipmentSlot resolveSlot(SlotContext context){
        String identifier = context.identifier();
        if(identifier.equals("incomparable_head")){
            return EquipmentSlot.HEAD;
        } else if(identifier.equals("incomparable_chest")){
            return EquipmentSlot.CHEST;
        } else if(identifier.equals("incomparable_legs")){
            return EquipmentSlot.LEGS;
        } else if(identifier.equals("incomparable_feet")){
            return EquipmentSlot.FEET;
        } else if(identifier.equals("incomparable_offhand")){
            return EquipmentSlot.OFFHAND;
        }

        return EquipmentSlot.MAINHAND;
    }
}
