package moffy.ticex.datagen.general.recipes;

import moffy.ticex.datagen.general.recipes.apotheosis.ApotheosisRecipeProvider;
import moffy.ticex.datagen.general.recipes.avaritia.AvaritiaRecipeProvider;
import moffy.ticex.datagen.general.recipes.cc.CCRecipeProvider;
import moffy.ticex.datagen.general.recipes.create.CreateRecipeProvider;
import moffy.ticex.datagen.general.recipes.draconicevolution.DERecipeProvider;
import moffy.ticex.datagen.general.recipes.irons.IronsRecipeProvider;
import moffy.ticex.datagen.general.recipes.mekanism.MekanismRecipeProvider;
import moffy.ticex.datagen.general.recipes.projecte.PERecipeProvider;
import moffy.ticex.datagen.general.recipes.psi.PsiRecipeProvider;
import moffy.ticex.datagen.general.recipes.sakura.SakuraRecipeProvider;
import moffy.ticex.datagen.general.recipes.slashblade.SlashbladeRecipeProvider;
import moffy.ticex.datagen.general.recipes.tacz.TaczRecipeProvider;
import moffy.ticex.datagen.general.recipes.ticex.CommonRecipeProvider;
import moffy.ticex.datagen.general.recipes.tinkersthings.ThingsRecipeProvider;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class TicEXRecipeProvider extends RecipeProvider {

    public TicEXRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> pWriter) {
        new CommonRecipeProvider().buildRecipes(pWriter);
        if (ModList.get().isLoaded("avaritia")) new AvaritiaRecipeProvider().buildRecipes(pWriter);
        if (ModList.get().isLoaded("psi")) new PsiRecipeProvider().buildRecipes(pWriter);
        if (ModList.get().isLoaded("computercraft")) new CCRecipeProvider().buildRecipes(pWriter);
        if (ModList.get().isLoaded("create")) new CreateRecipeProvider().buildRecipes(pWriter);
        if (ModList.get().isLoaded("draconicevolution")) new DERecipeProvider().buildRecipes(pWriter);
        if (ModList.get().isLoaded("apotheosis")) new ApotheosisRecipeProvider().buildRecipes(pWriter);
        if (ModList.get().isLoaded("mekanism")) new MekanismRecipeProvider().buildRecipes(pWriter);
        if (ModList.get().isLoaded("tinkers_things")) new ThingsRecipeProvider().buildRecipes(pWriter);
        if (ModList.get().isLoaded("slashblade")) new SlashbladeRecipeProvider().buildRecipes(pWriter);
        if (ModList.get().isLoaded("tacz")) new TaczRecipeProvider().buildRecipes(pWriter);
        if (ModList.get().isLoaded("irons_spellbooks")) new IronsRecipeProvider().buildRecipes(pWriter);
        if (ModList.get().isLoaded("projecte")) new PERecipeProvider().buildRecipes(pWriter);
        if (ModList.get().isLoaded("sakura")) new SakuraRecipeProvider().buildRecipes(pWriter);
    }

    public static InventoryChangeTrigger.TriggerInstance has(@NotNull ItemLike itemLike) {
        return RecipeProvider.has(itemLike);
    }
}
