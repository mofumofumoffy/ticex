package moffy.ticex.mixin.config;

import com.mojang.logging.LogUtils;
import moffy.ticex.TicEXConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.recipe.casting.AbstractCastingRecipe;

@Mixin(value = AbstractCastingRecipe.class, remap = false)
public class AbstractCastingRecipeMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void modifyRecipe(RecipeType<?> type, ResourceLocation id, String group, Ingredient cast, boolean consumed, boolean switchSlots, CallbackInfo ci) {
        LogUtils.getLogger().debug(String.valueOf(id));
        try{
            if (TicEXConfig.USE_MORE_CONFIG.get() && !TicEXConfig.SHOULD_CONSUME_SLASHBLADE.get() && id.equals(new ResourceLocation("ticex", "tools/parts/casting/catalyst_slashblade"))) {
                ((AbstractCastingRecipeAccessor) this).setConsumed(false);
            }
        }catch (IllegalStateException ignored){}
    }
}
