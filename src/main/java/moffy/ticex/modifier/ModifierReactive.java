package moffy.ticex.modifier;

/*
 * This file is part of the TicEXArsModule.
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 * See the LICENSES/LGPL-3.0.md file for details.
 * 2025 Moffy
 */


import com.hollingsworth.arsnouveau.setup.registry.EnchantmentRegistry;
import moffy.ticex.TicEX;
import moffy.ticex.TicEXConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
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
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Map;

public class ModifierReactive extends Modifier implements EnchantmentModifierHook, InventoryTickModifierHook {

    public static final ResourceLocation REACTIVE_COOLDOWN = new ResourceLocation(TicEX.MODID, "reactive_cooldown");

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.ENCHANTMENTS, ModifierHooks.INVENTORY_TICK);
    }

    @Override
    public int updateEnchantmentLevel(IToolStackView iToolStackView, ModifierEntry modifierEntry, Enchantment enchantment, int i) {
        if(EnchantmentHelper.getEnchantmentId(enchantment).equals(EnchantmentHelper.getEnchantmentId(EnchantmentRegistry.REACTIVE_ENCHANTMENT.get()))){
            return iToolStackView.getModifierLevel(this);
        }
        return 0;
    }

    @Override
    public void updateEnchantments(IToolStackView iToolStackView, ModifierEntry modifierEntry, Map<Enchantment, Integer> map) {
        map.put(EnchantmentRegistry.REACTIVE_ENCHANTMENT.get(), iToolStackView.getPersistentData().getInt(REACTIVE_COOLDOWN) <= 0 ? iToolStackView.getModifierLevel(this) : 0);
    }

    @Override
    public void onInventoryTick(IToolStackView iToolStackView, ModifierEntry modifierEntry, Level level, LivingEntity livingEntity, int i, boolean b, boolean b1, ItemStack itemStack) {
        if(!level.isClientSide()){
            ModDataNBT persistentData = ToolStack.from(itemStack).getPersistentData();
            TicEX.LOGGER.info("{}", persistentData.getInt(ModifierReactive.REACTIVE_COOLDOWN));
            /*if(persistentData.getInt(REACTIVE_COOLDOWN) > 0){
                persistentData.putInt(REACTIVE_COOLDOWN, persistentData.getInt(REACTIVE_COOLDOWN) - 1);
            }*/
        }
    }
}
