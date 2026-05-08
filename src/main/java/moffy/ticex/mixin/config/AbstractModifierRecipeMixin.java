package moffy.ticex.mixin.config;

import moffy.ticex.TicEX;
import moffy.ticex.TicEXConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.modifiers.adding.AbstractModifierRecipe;
import slimeknights.tconstruct.library.tools.SlotType;

@Mixin(value = AbstractModifierRecipe.class, remap = false)
public class AbstractModifierRecipeMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void modifyLevel(ResourceLocation id, Ingredient toolRequirement, int maxToolSize, ModifierId result, IntRange level, SlotType.SlotCount slots, boolean allowCrystal, boolean checkTraitLevel, CallbackInfo ci) {
        try {
            if(TicEXConfig.USE_MORE_CONFIG != null && TicEXConfig.USE_MORE_CONFIG.get()){
                TicEXConfig.MODIFIER_CONFIG.get().getConfiguredValue(id).ifPresent(value -> ((AbstractModifierRecipeAccessor) this).setLevel(new IntRange(1, value)));
            }
        } catch (IllegalStateException ignored){}
    }
}
