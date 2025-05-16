package moffy.ticex.modifier;

import java.util.Map;
import java.util.Map.Entry;

import moffy.ticex.lib.hook.EmbossmentModifierHook;
import moffy.ticex.lib.utils.TicEXSBUtil;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;

public class ModifierKonpaku extends NoLevelsModifier implements EmbossmentModifierHook{

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, TicEXRegistry.EMBOSSMENT_HOOK);
    }

    @Override
    public boolean applyItem(EmbossmentContext context, int inputIndex, boolean secondary) {
        ItemStack input = context.getInputStack(inputIndex);
        ItemStack toolStack = context.getToolStack();

        boolean result = false;
        if(input.getItem().equals(Items.ENCHANTED_BOOK)){
            Map<Enchantment, Integer> bookEnchantments = EnchantmentHelper.getEnchantments(input);
            
            for(Entry<Enchantment, Integer> entry : bookEnchantments.entrySet()){
                if(TicEXSBUtil.applyEnchantment(toolStack, entry.getKey(), entry.getValue())){
                    return true;
                }
            }
        }
        return result;
    }
}
