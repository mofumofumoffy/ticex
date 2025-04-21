package moffy.ticex.modifier;

import moffy.ticex.lib.hook.EmbossmentModifierHook;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class ModifierSakuraCmp extends NoLevelsModifier implements EmbossmentModifierHook{

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, TicEXRegistry.EMBOSSMENT_HOOK);
    }

    @Override
    public boolean applyItem(ItemStack toolStack, ItemStack input, boolean simulate) {
        
        boolean succeed = false;

        //ticex to sakuratinker
        if(!(toolStack.getItem() instanceof ArmorItem)){
            ToolStack tool = ToolStack.from(toolStack);
            for(MaterialVariant materialVariant : tool.getMaterials()){
                if(materialVariant.matches(new MaterialId("sakuratinker","infinity")) && TicEXRegistry.COSMIC_LUCK_MODIFIER != null){ 
                    //infinity
                    tool.addModifier(TicEXRegistry.COSMIC_LUCK_MODIFIER.getId(), 1); 
                    succeed = true;
                } 
                else if(materialVariant.matches(new MaterialId("sakuratinker","crystal_matrix"))){ 
                    //crystal matrix
                    tool.addModifier(TinkerModifiers.insatiable.getId(), tool.getModifierLevel(new ModifierId("sakuratinker", "crystalline")));
                    succeed = true;
                }
            }
        }

        return succeed;
    }
    
    @Override
    public boolean shouldDisplay(boolean advanced) {
        return false;
    }
}
