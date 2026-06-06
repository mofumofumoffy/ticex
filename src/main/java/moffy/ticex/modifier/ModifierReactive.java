package moffy.ticex.modifier;

/*
 * This file is part of the TicEXArsModule.
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 * See the LICENSES/LGPL-3.0.md file for details.
 * 2025 Moffy
 */

import com.hollingsworth.arsnouveau.setup.registry.EnchantmentRegistry;
import moffy.ticex.lib.hook.EmbossmentModifierHook;
import moffy.ticex.lib.hook.ProvidePropertyModifierHook;
import moffy.ticex.lib.hook.TicEXModifierHooks;
import moffy.ticex.modifier.propeties.ReactiveProperty;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.EnchantmentModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Map;
import java.util.function.BiFunction;

public class ModifierReactive extends Modifier implements InventoryTickModifierHook, EnchantmentModifierHook, EmbossmentModifierHook, ProvidePropertyModifierHook {

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK, ModifierHooks.ENCHANTMENTS, TicEXModifierHooks.EMBOSSMENT, TicEXModifierHooks.PROPERTY_PROVIDER);
    }

    @Override
    public void onInventoryTick(IToolStackView iToolStackView, ModifierEntry modifierEntry, Level level, LivingEntity livingEntity, int i, boolean b, boolean b1, ItemStack itemStack) {
        CompoundTag nbt = itemStack.getOrCreateTag();
        if(!nbt.contains("reactive_cooldown")){
            nbt.putInt("reactive_cooldown", 0);
        }

        int cd = nbt.getInt("reactive_cooldown");
        if(cd > 0){
            nbt.putInt("reactive_cooldown", cd - 1);
        }
    }

    @Override
    public int updateEnchantmentLevel(IToolStackView iToolStackView, ModifierEntry modifierEntry, Enchantment enchantment, int i) {
        if(EnchantmentHelper.getEnchantmentId(enchantment).equals(EnchantmentHelper.getEnchantmentId(EnchantmentRegistry.REACTIVE_ENCHANTMENT.get()))){
            return iToolStackView.getModifierLevel(this);
        }
        return i;
    }

    @Override
    public void updateEnchantments(IToolStackView iToolStackView, ModifierEntry modifierEntry, Map<Enchantment, Integer> map) {
        map.put(EnchantmentRegistry.REACTIVE_ENCHANTMENT.get(), iToolStackView.getModifierLevel(this));
    }

    @Override
    public boolean applyItem(EmbossmentContext context, int inputIndex, boolean secondary) {
        if(!secondary){
            ItemStack toolItemStack = context.getToolStack();
            ItemStack inputItemStack = context.getInputStack(inputIndex);
            CompoundTag inputTag = inputItemStack.getOrCreateTag();

            int reactiveLv = EnchantmentHelper.getTagEnchantmentLevel(EnchantmentRegistry.REACTIVE_ENCHANTMENT.get(), inputItemStack);
            if(reactiveLv > 0){
                if(inputTag.contains("ars_nouveau:reactive_caster")){
                    toolItemStack.getOrCreateTag().put("ars_nouveau:reactive_caster", inputTag.getCompound("ars_nouveau:reactive_caster").copy());
                }

                ToolStack toolStack = ToolStack.from(toolItemStack);
                if(toolStack.getModifierLevel(TicEXRegistry.REACTIVE_MODIFIER.get()) > 0){
                    toolStack.removeModifier(TicEXRegistry.REACTIVE_MODIFIER.getId(), toolStack.getModifierLevel(TicEXRegistry.REACTIVE_MODIFIER.get()));
                }
                toolStack.addModifier(TicEXRegistry.REACTIVE_MODIFIER.getId(), reactiveLv);
                return true;
            }
        }
        return false;
    }

    @Override
    public BiFunction<Player, ItemStack, Map<String, Object>> getPropertyProvider() {
        return ReactiveProperty.getProperties();
    }
}
