package moffy.ticex.modifier;

import moffy.ticex.lib.hook.ProvidePropertyModifierHook;
import moffy.ticex.lib.hook.TicEXModifierHooks;
import moffy.ticex.modifier.propeties.IncomparableProperty;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.SlotStackModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.capability.inventory.InventoryModule;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import java.util.Map;
import java.util.function.BiFunction;

public class ModifierIncomparable extends NoLevelsModifier implements ProvidePropertyModifierHook, ToolInventoryCapability.InventoryModifierHook, SlotStackModifierHook {

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        super.registerHooks(hookBuilder);
        hookBuilder.addHook(this, TicEXModifierHooks.PROPERTY_PROVIDER, ToolInventoryCapability.HOOK, ModifierHooks.SLOT_STACK);
    }

    @Override
    public BiFunction<Player, ItemStack, Map<String, Object>> getPropertyProvider() {
        return IncomparableProperty.getProperties();
    }

    @Override
    public int getSlots(IToolStackView iToolStackView, ModifierEntry modifierEntry) {
        return 6;
    }

    @Override
    public ItemStack getStack(IToolStackView tool, ModifierEntry modifier, int slot) {
        IModDataView modData = tool.getPersistentData();
        ResourceLocation key = modifier.getId();
        if (slot < getSlots(tool, modifier) && modData.contains(key, Tag.TAG_LIST)) {
            ListTag list = tool.getPersistentData().get(key, InventoryModule.GET_COMPOUND_LIST);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag compound = list.getCompound(i);
                if (compound.getInt(InventoryModule.TAG_SLOT) == slot) {
                    return ItemStack.of(compound);
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setStack(IToolStackView tool, ModifierEntry modifier, int slot, ItemStack stack) {
        if (slot < getSlots(tool, modifier)) {
            ListTag list;
            ModDataNBT modData = tool.getPersistentData();
            // if the tag exists, fetch it
            ResourceLocation key = modifier.getId();
            if (modData.contains(key, Tag.TAG_LIST)) {
                list = modData.get(key, InventoryModule.GET_COMPOUND_LIST);
                // first, try to find an existing stack in the slot
                for (int i = 0; i < list.size(); i++) {
                    CompoundTag compound = list.getCompound(i);
                    if (compound.getInt(InventoryModule.TAG_SLOT) == slot) {
                        if (stack.isEmpty()) {
                            list.remove(i);
                        } else {
                            compound.getAllKeys().clear();
                            InventoryModule.writeStack(stack, slot, compound);
                        }
                        return;
                    }
                }
            } else if (stack.isEmpty()) {
                // nothing to do if empty
                return;
            } else {
                list = new ListTag();
                modData.put(key, list);
            }

            // list did not contain the slot, so add it
            if (!stack.isEmpty()) {
                list.add(InventoryModule.writeStack(stack, slot, new CompoundTag()));
            }
        }
    }

    @Override
    public boolean isItemValid(IToolStackView tool, ModifierEntry modifier, int slot, ItemStack stack) {
        return slot < getSlots(tool, modifier) && stack.getItem() instanceof IModifiable;
    }
}
