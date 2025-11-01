package moffy.ticex.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingLookup;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialFluidRecipe;

import java.util.*;

@Mixin(value = MaterialCastingLookup.class, remap = false)
public class MaterialCastingLookupMixin {

    @Shadow
    @Final
    private static List<MaterialFluidRecipe> CASTING_FLUIDS;

    @Inject(
            at = @At("HEAD"),
            method = "lambda$static$0",
            cancellable = true
    )
    private static void modifyMaterialCastingCache(Fluid fluid, CallbackInfoReturnable<MaterialFluidRecipe> cir){
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
                    cir.setReturnValue(matchedRecipe);
                    return;
                }
            }

            cir.setReturnValue(matchedRecipes.get(0));
            return;
        }

        cir.setReturnValue(MaterialFluidRecipe.EMPTY);
    }
}
