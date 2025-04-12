package moffy.ticex.modifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;

public class ModifierKonpaku extends NoLevelsModifier implements EmbossmentModifierHook{

    protected Map<Enchantment, Integer> enchantmentCapacities;

    public ModifierKonpaku(){
        enchantmentCapacities = new HashMap<>();

        enchantmentCapacities = new HashMap<>();

        enchantmentCapacities.put(Enchantments.FIRE_PROTECTION, 4);
        enchantmentCapacities.put(Enchantments.FALL_PROTECTION, 4);
        enchantmentCapacities.put(Enchantments.RESPIRATION, 3);
        enchantmentCapacities.put(Enchantments.POWER_ARROWS, 5);
        enchantmentCapacities.put(Enchantments.PUNCH_ARROWS, 2);
        enchantmentCapacities.put(Enchantments.THORNS, 3);
        enchantmentCapacities.put(Enchantments.SOUL_SPEED, 3);
    }

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
                for(Entry<Enchantment, Integer> predicate : enchantmentCapacities.entrySet()){
                    if(entry.getKey().getDescriptionId().equals(predicate.getKey().getDescriptionId())){
                        if(toolStack.getEnchantmentLevel(entry.getKey()) > 0){
                            CompoundTag nbt = toolStack.getOrCreateTag();
                            if (!nbt.contains("Enchantments", Tag.TAG_LIST)) {
                                nbt.put("Enchantments", new ListTag());
                            }

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
                            
                        } else {
                            toolStack.enchant(entry.getKey(), Math.min(predicate.getValue(), entry.getValue()));
                        }
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    protected int calcEnchLevel(ItemStack stack, Enchantment key, int value){
        int currentLv = stack.getEnchantmentLevel(key);
        int levelCap = enchantmentCapacities.get(key);
        if(value == currentLv){
            return Math.min(value + 1, levelCap);
        } 
        return Math.min(Math.max(value, currentLv), levelCap);
    }
}
