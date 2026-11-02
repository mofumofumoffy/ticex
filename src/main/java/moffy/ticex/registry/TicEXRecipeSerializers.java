package moffy.ticex.registry;

import moffy.ticex.lib.recipe.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.recipe.helper.TypeAwareRecipeSerializer;

public class TicEXRecipeSerializers {
    public static RegistryObject<TypeAwareRecipeSerializer<EmbossmentCastingRecipe>> CASTING_EMBOSSMENT_RECIPE_SERIALIZER = null;
    public static RegistryObject<RecipeSerializer<EmbossmentBuildingRecipe>> BUILDING_EMBOSSMENT_RECIPE_SERIALIZER = null;
    public static RegistryObject<RecipeSerializer<EmbossmentModifierRecipe>> MODIFIER_EMBOSSMENT_RECIPE_SERIALIZER = null;
    public static RegistryObject<RecipeSerializer<SingleEmbossmentModifierRecipe>> SINGLE_MODIFIER_EMBOSSMENT_RECIPE_SERIALIZER = null;
    public static RegistryObject<RecipeSerializer<ValidatableIncrementalModifierRecipe>> VALIDATABLE_INCREMENTAL_RECIPE_SERIALIZER = null;
}
