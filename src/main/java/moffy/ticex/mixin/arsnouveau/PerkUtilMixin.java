package moffy.ticex.mixin.arsnouveau;

/*
 * This file is part of the TicEXArsModule.
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 * See the LICENSES/LGPL-3.0.md file for details.
 * 2025 Moffy
 */

import com.hollingsworth.arsnouveau.api.perk.IPerkProvider;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import slimeknights.tconstruct.library.tools.item.IModifiable;

@Mixin(value = PerkUtil.class, remap = false)
public class PerkUtilMixin {
    @WrapOperation(
            method = "getPerkHolder",
            at = @At(value = "INVOKE", target = "Lcom/hollingsworth/arsnouveau/api/registry/PerkRegistry;getPerkProvider(Lnet/minecraft/world/item/Item;)Lcom/hollingsworth/arsnouveau/api/perk/IPerkProvider;")
    )
    private static IPerkProvider<ItemStack> getAlternativePerkHolder(Item item, Operation<IPerkProvider<ItemStack>> original){
        if(item instanceof IModifiable && item instanceof ArmorItem armorItem){
            return switch (armorItem.getType()){
                case HELMET -> original.call(ItemsRegistry.SORCERER_HOOD.get());
                case CHESTPLATE -> original.call(ItemsRegistry.SORCERER_ROBES.get());
                case LEGGINGS -> original.call(ItemsRegistry.SORCERER_LEGGINGS.get());
                case BOOTS -> original.call(ItemsRegistry.SORCERER_BOOTS.get());
            };
        }
        return original.call(item);
    }
}
