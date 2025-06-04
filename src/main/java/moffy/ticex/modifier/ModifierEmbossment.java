package moffy.ticex.modifier;

import moffy.ticex.caps.EmbossmentMaterialCapability;
import moffy.ticex.lib.hook.EmbossmentModifierHook;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;

public class ModifierEmbossment extends NoLevelsModifier implements EmbossmentModifierHook{

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, TicEXRegistry.EMBOSSMENT_HOOK);
    }

    @Override
    public boolean applyItem(EmbossmentContext context, int inputIndex, boolean secondary) {
        ItemStack toolStack = context.getToolStack();
        ItemStack inputStack = context.getInputStack(inputIndex);

        if(inputStack.getItem() instanceof ToolPartItem part){
            toolStack.getCapability(EmbossmentMaterialCapability.EMBOSSMENT_MATERIAL_CAPABILITY).ifPresent(embossment->{
                embossment.accept(toolStack, inputStack, part);
            });
            return true;
        }

        return false;
    }

}
