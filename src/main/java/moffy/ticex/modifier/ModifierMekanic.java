package moffy.ticex.modifier;

import moffy.ticex.lib.hook.EmbossmentModifierHook;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class ModifierMekanic extends NoLevelsModifier implements EmbossmentModifierHook{

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, TicEXRegistry.EMBOSSMENT_HOOK);
    }

    @Override
    public boolean applyItem(EmbossmentContext context, int inputIndex, boolean secondary) {
        ItemStack toolStack = context.getToolStack().copy();
        ItemStack inputStack = context.getInputStack(inputIndex);

        if(toolStack.getItem() instanceof ArmorItem armorItem && toolStack.getItem() instanceof IModifiable){
            ToolStack armor = ToolStack.from(toolStack);

            if(armor.getModifierLevel(TicEXRegistry.REBIRTH_MODIFIER.get()) == 0){
                ArmorItem.Type type = armorItem.getType();
            
                ItemStack mekaPlateStack = new ItemStack(TicEXRegistry.MEKAPLATE_ARMOR.get(type));

                if(inputStack.hasTag() && inputStack.getTag().contains("embossed")){
                    CompoundTag resultNBT = mekaPlateStack.getOrCreateTag();
                    CompoundTag embossedTag = inputStack.getTag().getCompound("embossed");
                    for(String key : embossedTag.getAllKeys()){
                        resultNBT.put(key, embossedTag.get(key));
                    }
                }

                if(toolStack.hasTag()){
                    CompoundTag resultNBT = mekaPlateStack.getOrCreateTag();
                    CompoundTag toolTag = toolStack.getTag();
                    for(String key : toolTag.getAllKeys()){
                        resultNBT.put(key, toolTag.get(key));
                    }
                }

                if(toolStack.hasCustomHoverName()){
                    mekaPlateStack.setHoverName(toolStack.getHoverName().copy());
                }

                ToolStack mekaPlate = ToolStack.from(mekaPlateStack);
                mekaPlate.addModifier(TicEXRegistry.REBIRTH_MODIFIER.get().getId(), 1);

                context.setToolStack(mekaPlate.createStack());

                return true;
            }
        }
        return false;
    }
    

    @Override
    public boolean shouldDisplay(boolean advanced) {
        return false;
    }
}
