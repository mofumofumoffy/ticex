package moffy.ticex.datagen.general.recipes;

import java.util.function.Consumer;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.fml.ModList;

public class TicEXRecipeProvider extends RecipeProvider{

    public TicEXRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        new CommonRecipeProvider().buildRecipes(pWriter);
        if(ModList.get().isLoaded("avaritia"))new AvaritiaRecipeProvider().buildRecipes(pWriter);
        if(ModList.get().isLoaded("computercraft"))new CCRecipeProvider().buildRecipes(pWriter);
    }
    
    public static InventoryChangeTrigger.TriggerInstance has(ItemLike itemLike){
        return RecipeProvider.has(itemLike);
    }
}
