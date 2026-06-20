package moffy.ticex.lib.utils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class TicEXUtils {
    public static final List<EquipmentSlot> ARMOR_TYPES = ImmutableList.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);

    public static ItemStack getToolStack(IToolStackView tool){
        if(tool instanceof ToolStack toolStack){
            return toolStack.createStack();
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack getToolStack(IToolStackView tool, LivingEntity entity, Modifier modifier) {
        return getToolStack(tool, entity, stack -> ToolStack.from(stack).getModifierLevel(modifier) > 0);
    }

    public static ItemStack getToolStack(IToolStackView tool, LivingEntity entity, Predicate<ItemStack> predicate) {
        if (tool instanceof ToolStack) {
            return ((ToolStack) tool).createStack();
        }
        ItemStack mainHandStack = entity.getMainHandItem();
        if (mainHandStack.getItem() instanceof IModifiable && predicate.test(mainHandStack)) {
            return mainHandStack;
        }
        ItemStack offHandStack = entity.getOffhandItem();
        if (offHandStack.getItem() instanceof IModifiable && predicate.test(offHandStack)) {
            return offHandStack;
        }

        if (ModList.get().isLoaded("curios")) {
            ItemStack curioStack = TicEXCuriosUtils.getStackInCurios(entity, predicate);
            if (curioStack != null) {
                return curioStack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static String getEquipmentSlotName(LivingEntity entity, ItemStack stack) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (ItemStack.isSameItem(entity.getItemBySlot(slot), stack)) {
                return slot.getName();
            }
        }

        if (ModList.get().isLoaded("curios")) {
            String curioName = TicEXCuriosUtils.getEquipmentSlotNameInCurios(entity, stack);
            if (curioName != null) {
                return curioName;
            }
        }

        return null;
    }

    public static ItemStack applyCatalystEmbossment(ItemStack toolItemStack, ITinkerStationContainer inv, boolean copyAttribute){
        boolean containsCatalyst = false;

        for (int i = 0; i < inv.getInputCount(); i++) {
            ItemStack input = inv.getInput(i);
            if (input.getOrCreateTag().contains("embossed")) {
                containsCatalyst = true;
                CompoundTag embossedTag = Objects.requireNonNull(input.getTag()).getCompound("embossed");
                CompoundTag tagTmp = embossedTag.copy();
                if(embossedTag.contains("id")){
                    CompoundTag stackTag = embossedTag.copy();
                    stackTag.put("tag",new CompoundTag());
                    if(copyAttribute){
                        ItemStack embossedStack = ItemStack.of(stackTag);
                        for(EquipmentSlot slot : EquipmentSlot.values()){
                            Multimap<Attribute, AttributeModifier> attributeModifierMultimap = toolItemStack.getAttributeModifiers(slot);
                            Multimap<Attribute, AttributeModifier> tmp = ArrayListMultimap.create();
                            embossedStack.getAttributeModifiers(slot).asMap().forEach((attribute, attributeModifier) -> {
                                if(!attributeModifierMultimap.containsKey(attribute)){
                                    tmp.putAll(attribute, attributeModifier);
                                }
                            });
                            tmp.forEach((attribute, attributeModifier) -> {
                                toolItemStack.addAttributeModifier(attribute, attributeModifier, slot);
                            });
                        }

                        if(ModList.get().isLoaded("curios")){
                            TicEXCuriosUtils.addCurioAttribute(toolItemStack, embossedStack);
                        }
                    }

                    tagTmp = embossedTag.getCompound("tag").copy();
                }

                CompoundTag compoundTag = toolItemStack.getOrCreateTag();
                for(String key : tagTmp.getAllKeys()){
                    Tag merged;
                    if(compoundTag.contains(key)){
                        merged = compoundTag.get(key);
                    } else {
                        merged = tagTmp.get(key).copy();
                    }
                    compoundTag.put(key, merged);
                }
            }
        }

        ToolStack toolStack = ToolStack.from(toolItemStack);
        if(containsCatalyst && toolStack.getModifierLevel(TicEXRegistry.REBIRTH_MODIFIER.get()) < 1){
            toolStack.addModifier(TicEXRegistry.REBIRTH_MODIFIER.getId(), 1);
        }

        return toolStack.createStack();
    }
}
