package moffy.ticex.lib.recipe;

import java.util.List;
import java.util.stream.IntStream;

import moffy.ticex.lib.utils.TicEXUtils;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipe;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolPartsHook;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

public class EmbossmentBuildingRecipe extends ToolBuildingRecipe {

    public static final RecordLoadable<EmbossmentBuildingRecipe> LOADER = RecordLoadable.create(
        ContextKey.ID.requiredField(),
        LoadableRecipeSerializer.RECIPE_GROUP,
        TinkerLoadables.MODIFIABLE_ITEM.requiredField("result", r -> r.output),
        IntLoadable.FROM_ONE.defaultField("result_count", 1, true, r -> r.outputCount),
        Loadables.RESOURCE_LOCATION.nullableField("slot_layout", r -> r.layoutSlot),
        IngredientLoadable.DISALLOW_EMPTY.list(0).defaultField("extra_requirements", List.of(), r -> r.ingredients),
        EmbossmentBuildingRecipe::new
    );

    public EmbossmentBuildingRecipe(
        ResourceLocation id,
        String group,
        IModifiable output,
        int outputCount,
        ResourceLocation layoutSlot,
        List<Ingredient> ingredients
    ) {
        super(id, group, output, outputCount, layoutSlot, ingredients);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TicEXRegistry.BUILDING_EMBOSSMENT_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeResult<LazyToolStack> getValidatedResult(ITinkerStationContainer inv, RegistryAccess access) {
        List<MaterialVariant> materials = IntStream.range(0, ToolPartsHook.parts(output.getToolDefinition()).size())
            .mapToObj(i -> MaterialVariant.of(IMaterialItem.getMaterialFromStack(inv.getInput(i))))
            .toList();
        ItemStack resultStack = ToolStack.createTool(
            output.asItem(),
            output.getToolDefinition(),
            new MaterialNBT(materials)
        ).createStack(outputCount);
        IntStream.range(0, ToolPartsHook.parts(output.getToolDefinition()).size()).forEach(i -> {
            ItemStack inputStack = inv.getInput(i);
            if (inputStack.hasTag()) {
                CompoundTag inputNBT = inputStack.getTag();

                if (inputNBT.contains("embossed")) {
                    CompoundTag resultNBT = resultStack.getOrCreateTag();
                    CompoundTag embossedTag = inputNBT.getCompound("embossed");
                    for (String key : embossedTag.getAllKeys()) {
                        resultNBT.put(key, embossedTag.get(key));
                    }
                }
            }
        });
        return LazyToolStack.success(TicEXUtils.applyCatalystEmbossment(resultStack, inv, true));
    }
}
