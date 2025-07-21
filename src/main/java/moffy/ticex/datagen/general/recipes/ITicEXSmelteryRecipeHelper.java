package moffy.ticex.datagen.general.recipes;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import slimeknights.mantle.recipe.condition.TagFilledCondition;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.tconstruct.library.data.recipe.SmelteryRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.function.Consumer;

public interface ITicEXSmelteryRecipeHelper extends ITicEXRecipeHelper {
    default SmelteryRecipeBuilder metal(Consumer<FinishedRecipe> consumer, TagKey<Fluid> fluid, ResourceLocation name) {
        return SmelteryRecipeBuilder.fluid(consumer, name, fluid).castingFolder("smeltery/casting/metal").meltingFolder("smeltery/melting/metal");
    }

    default void metalIngotOptional(Consumer<FinishedRecipe> consumer, TagKey<Fluid> fluidTag, TagKey<Item> storageTag, int temperature, ResourceLocation name) {
        SmelteryRecipeBuilder metal = metal(consumer, fluidTag, new ResourceLocation(name.getNamespace(), name.getPath().replace("molten_", "")));
        metal
                .optional()
                .oreRate(IMeltingContainer.OreRateType.METAL)
                .temperature(temperature)
                .baseUnit(90)
                .damageUnit(10)
                .melting(9.0F, "block", "storage_blocks", 3.0F, false, false)
                .meltingCasting(1.0F, TinkerSmeltery.ingotCast, 1.0F, false);

        Consumer<FinishedRecipe> wrapped = this.withCondition(consumer, new TagFilledCondition<>(storageTag));
        ItemCastingRecipeBuilder.basinRecipe(ItemOutput.fromTag(storageTag))
                .setFluid(fluidTag, 810)
                .setCoolingTime(temperature, 810)
                .save(wrapped, this.location(smelteryCastingFolder + "metal/" + name.getPath() + "/block"));
    }
}
