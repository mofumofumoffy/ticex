package moffy.ticex.lib;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public record AttackContextHolder(ItemStack stack, ToolAttackContext context, boolean isTool) {

    public AttackContextHolder(ItemStack stack, ToolAttackContext context){
        this(stack, context, stack.getItem() instanceof IModifiable);
    }

    @Nullable
    public ToolStack getTool(){
        if(isTool){
            return ToolStack.from(stack);
        }
        return null;
    }
}
