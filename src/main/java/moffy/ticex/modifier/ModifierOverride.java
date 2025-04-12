package moffy.ticex.modifier;

import java.util.Map;
import java.util.Map.Entry;

import moffy.ticex.TicEXConfig;
import moffy.ticex.lib.hook.EmbossmentModifierHook;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;

public class ModifierOverride extends NoLevelsModifier implements EmbossmentModifierHook {
    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, TicEXRegistry.EMBOSSMENT_HOOK);
    }

    @Override
    public boolean applyItem(ItemStack toolStack, ItemStack stack, boolean simulate) {
        boolean result = false;
        if(stack.getItem().equals(Items.ENCHANTED_BOOK)){
            Map<Enchantment, Integer> bookEnchantments = EnchantmentHelper.getEnchantments(stack);
            
            for(Entry<Enchantment, Integer> entry : bookEnchantments.entrySet()){
                if(toolStack.getEnchantmentLevel(entry.getKey()) >= entry.getKey().getMaxLevel() && entry.getValue() >= entry.getValue()){
                    CompoundTag nbt = toolStack.getOrCreateTag();

                    ListTag listTag = nbt.getList("Enchantments", Tag.TAG_COMPOUND);
                    ListTag newListTag = new ListTag();
                    for(int i = 0; i < listTag.size(); i++){
                        CompoundTag enchantmentTag = listTag.getCompound(i);
                        if(enchantmentTag.getString("id").equals(ForgeRegistries.ENCHANTMENTS.getKey(entry.getKey()).toString())){
                            newListTag.add(EnchantmentHelper.storeEnchantment(ResourceLocation.tryParse(enchantmentTag.getString("id")), calcEnchLevel(toolStack, entry.getKey(), entry.getValue())));
                        } else {
                            newListTag.add(enchantmentTag);
                        }
                    }
                    nbt.put("Enchantments", newListTag);
                    result = true;
                }
            }
        }
        return result;
    }

    protected int calcEnchLevel(ItemStack stack, Enchantment key, int value){
        return Math.min(TicEXConfig.OVERRIDE_LIMIT.get(), stack.getEnchantmentLevel(key) + 1);
    }
}
