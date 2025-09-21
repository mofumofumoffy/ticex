package moffy.ticex.mixin.arsnouveau;

/*
 * This file is part of the TicEXArsModule.
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 * See the LICENSES/LGPL-3.0.md file for details.
 * 2025 Moffy
 */

import com.hollingsworth.arsnouveau.api.enchanting_apparatus.ReactiveEnchantmentRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.SpellWriteRecipe;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.List;

@Mixin(value = SpellWriteRecipe.class, remap = false)
public class SpellWriteRecipeMixin {
    @Inject(
            at=@At("HEAD"),
            method = "isMatch",
            cancellable = true
    )
    public void isMatch(List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile enchantingApparatusTile, Player player, CallbackInfoReturnable<Boolean> cir){
        if(reagent.getItem() instanceof IModifiable){
            int level = ToolStack.from(reagent).getModifierLevel(TicEXRegistry.REACTIVE_MODIFIER.get());
            ItemStack parchment = ReactiveEnchantmentRecipe.getParchment(pedestalItems);
            cir.setReturnValue(!parchment.isEmpty() && !CasterUtil.getCaster(parchment).getSpell().isEmpty() && level > 0);
        }
    }
}
