package moffy.ticex.datagen.general.recipes;

import moffy.ticex.lib.TicEXMaterials;
import moffy.ticex.lib.TicEXTags;
import moffy.ticex.registry.TicEXFluids;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.tconstruct.library.data.recipe.IMaterialRecipeHelper;
import slimeknights.tconstruct.library.data.recipe.SmelteryRecipeBuilder;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialFluidRecipeBuilder;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer;
import slimeknights.tconstruct.library.recipe.melting.MaterialMeltingRecipeBuilder;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.function.Consumer;

public interface ITicEXSmelteryRecipeHelper extends ITicEXRecipeHelper, IMaterialRecipeHelper {

    default SmelteryRecipeBuilder metal(Consumer<FinishedRecipe> consumer, TagKey<Fluid> fluid, ResourceLocation name) {
        return SmelteryRecipeBuilder.fluid(consumer, name, fluid).castingFolder("smeltery/casting/metal").meltingFolder("smeltery/melting/metal");
    }

    default void metalMaterialItemOptional(Consumer<FinishedRecipe> consumer, TagKey<Fluid> fluidTag, MaterialId material, int temperature){
        metalMaterialItemOptional(consumer, fluidTag, material, temperature, false);
    }

    default void metalMaterialItemOptional(Consumer<FinishedRecipe> consumer, TagKey<Fluid> fluidTag, MaterialId material, int temperature, boolean hasNugget){
        metalMaterialOptional(consumer, fluidTag, material, temperature);
        metalItemOptional(consumer, fluidTag, temperature, material, hasNugget);
    }

    default void metalMaterialOptional(Consumer<FinishedRecipe> consumer, TagKey<Fluid> fluidTag, MaterialId material, int temperature){
        metalMaterialRecipe(consumer, material, materialFolder, material.getPath(), true);
        MaterialFluidRecipeBuilder.material(material)
                .setTemperature(temperature)
                .setFluid(fluidTag, FluidValues.INGOT)
                .save(consumer, prefix(material, materialCastingFolder));
        MaterialMeltingRecipeBuilder.material(material,
                        temperature,
                        FluidOutput.fromTag(fluidTag, FluidValues.INGOT))
                .save(consumer, prefix(material, materialMeltingFolder));
    }

    default void metalItemOptional(Consumer<FinishedRecipe> consumer, TagKey<Fluid> fluidTag, int temperature, ResourceLocation name){
        metalItemOptional(consumer,fluidTag,temperature,name,false);
    }

    default void metalItemOptional(Consumer<FinishedRecipe> consumer, TagKey<Fluid> fluidTag, int temperature, ResourceLocation name, boolean hasNugget) {
        SmelteryRecipeBuilder metal = metal(consumer, fluidTag, name);
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
