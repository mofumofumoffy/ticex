package moffy.ticex.lib.recipe;

import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.mantle.recipe.helper.TypeAwareRecipeSerializer;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.recipe.casting.ICastingContainer;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingRecipe;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

import java.util.Arrays;

public class EmbossmentCastingRecipe extends MaterialCastingRecipe {

    protected static final LoadableField<IMaterialItem, EmbossmentCastingRecipe> RESULT_FIELD =
        TinkerLoadables.MATERIAL_ITEM.requiredField("result", r -> r.result);
    public static final RecordLoadable<EmbossmentCastingRecipe> LOADER = RecordLoadable.create(
        LoadableRecipeSerializer.TYPED_SERIALIZER.requiredField(),
        ContextKey.ID.requiredField(),
        LoadableRecipeSerializer.RECIPE_GROUP,
        CAST_FIELD,
        ITEM_COST_FIELD,
        RESULT_FIELD,
        CAST_CONSUMED_FIELD,
        SWITCH_SLOTS_FIELD,
        EmbossmentCastingRecipe::new
    );

    protected Ingredient castIngredient;

    public EmbossmentCastingRecipe(
        TypeAwareRecipeSerializer<?> serializer,
        ResourceLocation id,
        String group,
        Ingredient cast,
        int itemCost,
        IMaterialItem result,
        boolean consumed,
        boolean switchSlots
    ) {
        super(serializer, id, group, cast, itemCost, result, consumed, switchSlots);
        this.castIngredient = cast;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TicEXRegistry.CASTING_EMBOSSMENT_RECIPE_SERIALIZER.get();
    }

    @Override
    public ItemStack assemble(ICastingContainer inv, RegistryAccess access) {
        ItemStack assembled = super.assemble(inv, access);
        ItemStack cast = inv.getStack();
        if (cast.hasTag()) {
            assembled.getOrCreateTag().put("embossed", cast.getTag());
        }
        for (EquipmentSlot slot : Arrays.stream(EquipmentSlot.values()).toArray(EquipmentSlot[]::new)) {
            cast.getAttributeModifiers(slot).forEach((attribute, modifier) -> {
                String id = ForgeRegistries.ATTRIBUTES.getKey(attribute).toString();
                assembled.getOrCreateTagElement("embossed_attributes/" + slot.getName()).putDouble(id, modifier.getAmount());
            });
        }
        return assembled;
    }
}
