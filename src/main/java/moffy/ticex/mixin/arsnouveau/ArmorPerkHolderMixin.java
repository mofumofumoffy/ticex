package moffy.ticex.mixin.arsnouveau;

import com.hollingsworth.arsnouveau.api.perk.ArmorPerkHolder;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import moffy.ticex.registry.TicEXModifiers;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

/*
 * This file is part of the TicEXArsModule.
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 * See the LICENSES/LGPL-3.0.md file for details.
 * 2026 Moffy
 */
@Mixin(value = ArmorPerkHolder.class, remap = false)
public class ArmorPerkHolderMixin {
    @ModifyExpressionValue(
            method = "getSlotsForTier",
            at = @At(value = "INVOKE", target = "Lcom/hollingsworth/arsnouveau/api/perk/ArmorPerkHolder;getTier()I")
    )
    public int modifyTier(int original){
        ItemStack stack = ((ArmorPerkHolder)((Object)this)).stack;
        if(stack.getItem() instanceof IModifiable){
            ToolStack tool = ToolStack.from(stack);
            int alterativeLevel = tool.getModifierLevel(TicEXModifiers.ALTERATIVE_MODIFIER.get());
            if(alterativeLevel > 0){
                return Math.max(original, alterativeLevel - 1);
            }
        }
        return original;
    }
}
