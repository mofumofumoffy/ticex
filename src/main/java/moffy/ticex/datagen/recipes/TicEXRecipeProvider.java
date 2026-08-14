package moffy.ticex.datagen.recipes;

import moffy.ticex.datagen.recipes.apotheosis.ApotheosisRecipeProvider;
import moffy.ticex.datagen.recipes.arsnouveau.ArsRecipeProvider;
import moffy.ticex.datagen.recipes.avaritia.AvaritiaRecipeProvider;
import moffy.ticex.datagen.recipes.botania.BotaniaRecipeProvider;
import moffy.ticex.datagen.recipes.cc.CCRecipeProvider;
import moffy.ticex.datagen.recipes.create.CreateRecipeProvider;
import moffy.ticex.datagen.recipes.curios.CuriosRecipeProvider;
import moffy.ticex.datagen.recipes.draconicevolution.DERecipeProvider;
import moffy.ticex.datagen.recipes.irons.IronsRecipeProvider;
import moffy.ticex.datagen.recipes.mekanism.MekanismRecipeProvider;
import moffy.ticex.datagen.recipes.projecte.PERecipeProvider;
import moffy.ticex.datagen.recipes.psi.PsiRecipeProvider;
import moffy.ticex.datagen.recipes.slashblade.SlashbladeRecipeProvider;
import moffy.ticex.datagen.recipes.tacz.TaczRecipeProvider;
import moffy.ticex.datagen.recipes.ticex.CommonRecipeProvider;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class TicEXRecipeProvider extends RecipeProvider {

    private final BotaniaRecipeProvider botaniaRecipeProvider;

    public TicEXRecipeProvider(PackOutput pOutput) {
        super(pOutput);
        botaniaRecipeProvider = new BotaniaRecipeProvider(pOutput);
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
        if (ModList.get().isLoaded("slashblade")) new SlashbladeRecipeProvider().buildRecipes(pWriter);
        if (ModList.get().isLoaded("tacz")) new TaczRecipeProvider().buildRecipes(pWriter);
        if (ModList.get().isLoaded("irons_spellbooks")) new IronsRecipeProvider().buildRecipes(pWriter);
        if (ModList.get().isLoaded("projecte")) new PERecipeProvider().buildRecipes(pWriter);
        if (ModList.get().isLoaded("curios")) new CuriosRecipeProvider().buildRecipes(pWriter);
        if (ModList.get().isLoaded("botania")) botaniaRecipeProvider.buildRecipes(pWriter);
        if (ModList.get().isLoaded("ars_nouveau")) new ArsRecipeProvider().buildRecipes(pWriter);
    }

    public static InventoryChangeTrigger.TriggerInstance has(@NotNull ItemLike itemLike) {
        return RecipeProvider.has(itemLike);
    }
}
