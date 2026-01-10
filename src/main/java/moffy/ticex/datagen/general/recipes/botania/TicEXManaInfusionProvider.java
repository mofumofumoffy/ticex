package moffy.ticex.datagen.general.recipes.botania;

import moffy.ticex.TicEX;
import moffy.ticex.datagen.general.recipes.ITicEXRecipeHelper;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import vazkii.botania.common.crafting.BlockStateIngredient;
import vazkii.botania.data.recipes.ManaInfusionProvider;

import java.util.function.Consumer;

public class TicEXManaInfusionProvider extends ManaInfusionProvider implements ITicEXRecipeHelper {
    public TicEXManaInfusionProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    public void buildRecipes(Consumer<net.minecraft.data.recipes.FinishedRecipe> consumer) {
        Consumer<net.minecraft.data.recipes.FinishedRecipe> topConsumer = withCondition(
                consumer,
                modsAvailable(ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "botania_compat"))
        );

        topConsumer.accept(new FinishedRecipe(
                prefix(TicEXRegistry.NECTAR_CORE, coresFolder),
                new ItemStack(TicEXRegistry.NECTAR_CORE.get()),
                ingr(TicEXRegistry.RECONSTRUCTION_CORE.get()),
                2000,
                "",
                new BlockStateIngredient(TinkerSmeltery.searedBricks.get()))
        );
    }
}
