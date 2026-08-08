package moffy.ticex.datagen.general.recipes;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import slimeknights.tconstruct.library.data.recipe.ISmelteryRecipeHelper;
import slimeknights.tconstruct.library.data.recipe.SmelteryRecipeBuilder;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.function.Consumer;

public interface ITicEXSmelteryRecipeHelper extends ITicEXRecipeHelper, ISmelteryRecipeHelper {
    Logger log = LoggerFactory.getLogger(ITicEXSmelteryRecipeHelper.class);

    default SmelteryRecipeBuilder metal(Consumer<FinishedRecipe> consumer, TagKey<Fluid> fluid, ResourceLocation name) {
        return SmelteryRecipeBuilder.fluid(consumer, name, fluid).castingFolder("smeltery/casting/metal").meltingFolder("smeltery/melting/metal");
    }

    default void metalItemOptional(Consumer<FinishedRecipe> consumer, TagKey<Fluid> fluidTag, int temperature, ResourceLocation name){
        metalItemOptional(consumer,fluidTag,temperature,name,false);
    }

    default void metalItemOptional(Consumer<FinishedRecipe> consumer, TagKey<Fluid> fluidTag, int temperature, ResourceLocation name, boolean hasNugget) {
        ResourceLocation itemPath = ResourceLocation.fromNamespaceAndPath(name.getNamespace(), name.getPath().replace("molten_", ""));
        SmelteryRecipeBuilder metal = metal(consumer, fluidTag, itemPath);
        metal
                .optional()
                .oreRate(IMeltingContainer.OreRateType.METAL)
                .temperature(temperature)
                .baseUnit(FluidValues.INGOT)
                .damageUnit(FluidValues.NUGGET)
                .melting(9.0F, "block", "storage_blocks", 3.0F, false, false)
                .blockCasting(9, Ingredient.EMPTY,false)
                .meltingCasting(1.0F, TinkerSmeltery.ingotCast, 1.0F, false);

        if(hasNugget){
            metal.meltingCasting((float) 1/9, TinkerSmeltery.nuggetCast, (float) 1/3, false);
        }
    }
}
