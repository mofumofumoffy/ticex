package moffy.ticex.mixin.arsnouveau;

/*
 * This file is part of the TicEXArsModule.
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 * See the LICENSES/LGPL-3.0.md file for details.
 * 2025 Moffy
 */

import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.IWrappedCaster;
import moffy.ticex.lib.modules.arsnouveau.interfaces.OriginalStackAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = SpellContext.class, remap = false)
public class SpellContextMixin implements OriginalStackAccessor {
    @Unique
    private ItemStack ticex_1_20_1$originalStack = ItemStack.EMPTY;

    @Inject(
            at = @At("TAIL"),
            method = "<init>(Lnet/minecraft/world/level/Level;Lcom/hollingsworth/arsnouveau/api/spell/Spell;Lnet/minecraft/world/entity/LivingEntity;Lcom/hollingsworth/arsnouveau/api/spell/wrapped_caster/IWrappedCaster;Lnet/minecraft/world/item/ItemStack;)V"
    )
    public void setOriginalStack(Level level, Spell spell, LivingEntity caster, IWrappedCaster wrappedCaster, ItemStack casterTool, CallbackInfo ci){
        this.ticex_1_20_1$originalStack = casterTool;
    }

    @Override
    public ItemStack getOriginalStack() {
        return this.ticex_1_20_1$originalStack;
    }
}