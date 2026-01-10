package moffy.ticex.modifier;

import java.util.Map;
import java.util.Map.Entry;
import moffy.ticex.lib.hook.EmbossmentModifierHook;
import moffy.ticex.lib.utils.TicEXSBUtils;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class ModifierKonpaku extends NoLevelsModifier implements EmbossmentModifierHook {

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, TicEXRegistry.EMBOSSMENT_HOOK);
    }

    @Override
    public boolean applyItem(EmbossmentContext context, int inputIndex, boolean secondary) {
        ItemStack input = context.getInputStack(inputIndex);
        ItemStack toolStack = context.getToolStack();

        boolean result = false;
        if (input.getItem().equals(Items.ENCHANTED_BOOK)) {
            ToolStack tool = ToolStack.from(toolStack);
            Map<Enchantment, Integer> bookEnchantments = EnchantmentHelper.getEnchantments(input);

            for (Entry<Enchantment, Integer> entry : bookEnchantments.entrySet()) {
                if (TicEXSBUtils.disallowedEnchantments.contains(entry.getKey())) {
                    context.setErrorMsg(Component.translatable("recipe.ticex.not_allowed_enchantment_slashblade"));
                    return false;
                }
                if (TicEXSBUtils.applyEnchantment(toolStack, entry.getKey(), entry.getValue())) {
                    result = true;
                    if(tool.getModifierLevel(TicEXRegistry.ENCHANTMENT_SUPPLIER_MODIFIER.get()) < 1){
                        tool.addModifier(TicEXRegistry.ENCHANTMENT_SUPPLIER_MODIFIER.getId(), 1);
                    }
                }
            }
        } else {
            context.setErrorMsg(Component.translatable("recipe.ticex.required_enchanted_book"));
        }
        return result;
    }
}
