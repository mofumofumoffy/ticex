package moffy.ticex.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingLookup;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialFluidRecipe;
import slimeknights.tconstruct.library.utils.SimpleCache;

import java.util.*;

@Mixin(value = MaterialCastingLookup.class, remap = false)
@SuppressWarnings("deprecation")
public class MaterialCastingLookupMixin {

    @Shadow
    @Final
    private static List<MaterialFluidRecipe> CASTING_FLUIDS;

    @ModifyExpressionValue(
            method = "<clinit>",
            at = @At(
                    value = "NEW",
                    target = "(Ljava/util/function/Function;)Lslimeknights/tconstruct/library/utils/SimpleCache;",
                    ordinal = 0
            )
    )
    private static SimpleCache<Fluid, MaterialFluidRecipe> modifyMaterialCastingCache(SimpleCache<Fluid, MaterialFluidRecipe> original){
        return new SimpleCache<>(fluid -> {
            List<MaterialFluidRecipe> matchedRecipes = new ArrayList<>();
            for(MaterialFluidRecipe recipe : CASTING_FLUIDS) {
                if (recipe.matches(fluid)) {
                    matchedRecipes.add(recipe);
                }
            }

            if(!matchedRecipes.isEmpty()){
                for(MaterialFluidRecipe matchedRecipe: matchedRecipes){
                    ResourceLocation recipeKey = BuiltInRegistries.RECIPE_TYPE.getKey(matchedRecipe.getType());
                    ResourceLocation fluidKey = BuiltInRegistries.FLUID.getKey(fluid);
                    if(recipeKey != null && recipeKey.getNamespace().equals(fluidKey.getNamespace())){
                        return matchedRecipe;
                    }
                }

                return matchedRecipes.get(0);
            }
            return MaterialFluidRecipe.EMPTY;
        });
    }
}
