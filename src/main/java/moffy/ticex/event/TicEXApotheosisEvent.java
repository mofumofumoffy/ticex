package moffy.ticex.event;

import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.adventure.event.ItemSocketingEvent;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.socket.SocketHelper;
import moffy.ticex.registry.TicEXModifiers;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class TicEXApotheosisEvent {
    public static void onSocketGem(ItemSocketingEvent.ModifyResult event){
        ItemStack stack = event.getOutput();
        if(stack.getItem() instanceof IModifiable){
            ToolStack tool = ToolStack.from(stack);
            if(tool.getModifierLevel(TicEXModifiers.APOTH_SUPPLIER_MODIFIER.get()) < 1){
                tool.addModifier(TicEXModifiers.APOTH_SUPPLIER_MODIFIER.getId(), 1);
            }
        }
    }

    public static void supplierBouncer(LivingEquipmentChangeEvent event){
        ItemStack stack = event.getTo();
        if(stack.getItem() instanceof IModifiable){
            ToolStack tool = ToolStack.from(stack);
            if((AffixHelper.hasAffixes(stack) || !SocketHelper.getGems(stack).isEmpty()) && tool.getModifierLevel(TicEXModifiers.APOTH_SUPPLIER_MODIFIER.get()) < 1){
                tool.addModifier(TicEXModifiers.APOTH_SUPPLIER_MODIFIER.getId(), 1);
            }
        }
    }

    public static void onModifyAttribute(ItemAttributeModifierEvent event){
        ItemStack stack = event.getItemStack();
        EquipmentSlot slot = event.getSlotType();
        if(stack.getItem() instanceof IModifiable){
            SocketHelper.getGems(stack).addModifiers(LootCategory.forItem(stack), slot, event::addModifier);

            var affixes = AffixHelper.getAffixes(stack);
            for (AffixInstance inst : affixes.values()) {
                inst.addModifiers(slot, event::addModifier);
            }
        }
    }
}
