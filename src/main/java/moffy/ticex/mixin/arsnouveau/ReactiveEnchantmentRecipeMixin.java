package moffy.ticex.mixin.arsnouveau;

/*
 * This file is part of the TicEXArsModule.
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 * See the LICENSES/LGPL-3.0.md file for details.
 * 2025 Moffy
 */

import com.hollingsworth.arsnouveau.api.enchanting_apparatus.ReactiveEnchantmentRecipe;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(value = ReactiveEnchantmentRecipe.class, remap = false)
public class ReactiveEnchantmentRecipeMixin {
    @WrapOperation(method = "getResult", at = @At(value = "INVOKE", target = "Lcom/hollingsworth/arsnouveau/api/enchanting_apparatus/EnchantmentRecipe;getResult(Ljava/util/List;Lnet/minecraft/world/item/ItemStack;Lcom/hollingsworth/arsnouveau/common/block/tile/EnchantingApparatusTile;)Lnet/minecraft/world/item/ItemStack;"))
    public ItemStack modifyCore(ReactiveEnchantmentRecipe instance, List<ItemStack> pedestalItems, ItemStack reagent, EnchantingApparatusTile tile, Operation<ItemStack> original){
        if(reagent.getItem().equals(TicEXRegistry.RECONSTRUCTION_CORE.get())){
            ItemStack reactiveCoreStack = new ItemStack(TicEXRegistry.REACTIVE_CORE.get(), reagent.getCount(), reagent.getOrCreateTag().copy());
            original.call(instance, pedestalItems, reagent, tile).getAllEnchantments().forEach(reactiveCoreStack::enchant);
            return reactiveCoreStack;
        }
        return original.call(instance, pedestalItems, reagent, tile);
    }
}
