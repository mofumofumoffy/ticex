package moffy.ticex.datagen.general.recipes.ticex.builder;

import moffy.ticex.lib.recipe.ValidatableIncrementalModifierRecipe;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.util.LazyModifier;
import slimeknights.tconstruct.library.recipe.modifiers.adding.AbstractModifierRecipeBuilder;

import java.util.function.Consumer;

public class ValidatableIncrementalModifierRecipeBuilder extends AbstractModifierRecipeBuilder<ValidatableIncrementalModifierRecipeBuilder> {
    private Ingredient input;
    private int amountPerItem;
    private int neededPerLevel;
    private ItemOutput leftover;

    protected ValidatableIncrementalModifierRecipeBuilder(ModifierId result) {
        super(result);
        this.input = Ingredient.EMPTY;
        this.leftover = ItemOutput.EMPTY;
    }

    public static ValidatableIncrementalModifierRecipeBuilder modifier(ModifierId modifier) {
        return new ValidatableIncrementalModifierRecipeBuilder(modifier);
    }

    public static ValidatableIncrementalModifierRecipeBuilder modifier(LazyModifier modifier) {
        return modifier(modifier.getId());
    }

    public ValidatableIncrementalModifierRecipeBuilder input(Ingredient input, int amountPerItem, int neededPerLevel) {
        if (amountPerItem < 1) {
            throw new IllegalArgumentException("Amount per item must be at least 1");
        } else {
            this.input = input;
            this.amountPerItem = amountPerItem;
            this.neededPerLevel = neededPerLevel;
            return this;
        }
    }

    public ValidatableIncrementalModifierRecipeBuilder input(ItemLike item, int amountPerItem, int neededPerLevel) {
        return this.input(Ingredient.of(new ItemLike[]{item}), amountPerItem, neededPerLevel);
    }

    public ValidatableIncrementalModifierRecipeBuilder input(TagKey<Item> tag, int amountPerItem, int neededPerLevel) {
        return this.input(Ingredient.of(tag), amountPerItem, neededPerLevel);
    }

    public ValidatableIncrementalModifierRecipeBuilder leftover(ItemOutput leftover) {
        this.leftover = leftover;
        return this;
    }

    public ValidatableIncrementalModifierRecipeBuilder leftover(ItemStack stack) {
        return this.leftover(ItemOutput.fromStack(stack));
    }

    public ValidatableIncrementalModifierRecipeBuilder leftover(ItemLike item) {
        return this.leftover(ItemOutput.fromItem(item));
    }

    public void save(@NotNull Consumer<FinishedRecipe> consumer, @NotNull ResourceLocation id) {
        if (this.input == Ingredient.EMPTY) {
            throw new IllegalStateException("Must set input");
        } else {
            ResourceLocation advancementId = this.buildOptionalAdvancement(id, "modifiers");
            consumer.accept(new LoadableFinishedRecipe<>(
                    new ValidatableIncrementalModifierRecipe(id, this.input, this.amountPerItem, this.neededPerLevel, this.tools, this.maxToolSize, this.result, ModifierEntry.VALID_LEVEL.range(this.minLevel, this.maxLevel), this.slots, this.leftover, this.allowCrystal, this.checkTraitLevel),
                    ValidatableIncrementalModifierRecipe.LOADER, advancementId));
        }
    }
}
