package moffy.ticex.lib.recipe;

import com.google.common.collect.ImmutableList;
import com.google.common.math.IntMath;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.modifiers.adding.AbstractModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IncrementalModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class ValidatableIncrementalModifierRecipe extends AbstractModifierRecipe {

    public static final RecordLoadable<ValidatableIncrementalModifierRecipe> LOADER = RecordLoadable.create(
        ContextKey.ID.requiredField(),
        IngredientLoadable.DISALLOW_EMPTY.requiredField("input", r -> r.input),
        IntLoadable.FROM_ONE.defaultField("amount_per_item", 1, true, r -> r.amountPerInput),
        IntLoadable.FROM_ONE.requiredField("needed_per_level", r -> r.neededPerLevel),
        TOOLS_FIELD,
        MAX_TOOL_SIZE_FIELD,
        RESULT_FIELD,
        LEVEL_FIELD,
        SLOTS_FIELD,
        ItemOutput.Loadable.OPTIONAL_STACK.emptyField("leftover", r -> r.leftover),
        ALLOW_CRYSTAL_FIELD,
        CHECK_TRAIT_LEVEL_FIELD,
        ValidatableIncrementalModifierRecipe::new
    );

    private final Ingredient input;
    private final int amountPerInput;
    private final int neededPerLevel;
    private final ItemOutput leftover;

    public ValidatableIncrementalModifierRecipe(
        ResourceLocation id,
        Ingredient input,
        int amountPerInput,
        int neededPerLevel,
        Ingredient toolRequirement,
        int maxToolSize,
        ModifierId result,
        IntRange level,
        SlotCount slots,
        ItemOutput leftover,
        boolean allowCrystal,
        boolean checkTraitLevel
    ) {
        super(id, toolRequirement, maxToolSize, result, level, slots, allowCrystal, checkTraitLevel);
        this.input = input;
        this.amountPerInput = amountPerInput;
        this.neededPerLevel = neededPerLevel;
        this.leftover = leftover;
    }

    @Override
    public boolean matches(ITinkerStationContainer inv, Level level) {
        if (!result.isBound() || !this.toolRequirement.test(inv.getTinkerableStack())) {
            return false;
        }
        return matchesCrystal(inv) || IncrementalModifierRecipe.containsOnlyIngredient(inv, input);
    }

    @Override
    public RecipeResult<LazyToolStack> getValidatedResult(ITinkerStationContainer inv, RegistryAccess access) {
        ToolStack tool = inv.getTinkerable();

        ModifierId modifier = result.getId();
        boolean newLevel = tool.getUpgrades().getEntry(modifier).getAmount(0) <= 0;

        boolean crystal = matchesCrystal(inv);
        if (crystal || newLevel) {
            Component commonError = validatePrerequisites(tool);
            if (commonError != null) {
                return RecipeResult.failure(commonError);
            }
        }

        tool = tool.copy();

        if (crystal || newLevel) {
            SlotCount slots = getSlots();
            if (slots != null) {
                tool.getPersistentData().addSlots(slots.type(), -slots.count());
            }
        }

        if (crystal) {
            tool.addModifier(modifier, 1);
        } else {
            tool.addModifierAmount(
                modifier,
                IncrementalModifierRecipe.getAvailableAmount(inv, input, amountPerInput),
                neededPerLevel
            );
        }

        Component error = tool.tryValidate();
        if (error != null) {
            return RecipeResult.failure(error);
        }

        return success(tool, inv);
    }

    @Override
    public void updateInputs(LazyToolStack result, IMutableTinkerStationContainer inv, boolean isServer) {
        if (matchesCrystal(inv)) {
            super.updateInputs(result, inv, isServer);
            return;
        }

        ToolStack inputTool = inv.getTinkerable();
        ModifierId modifier = this.result.getId();
        ModifierEntry inputEntry = inputTool.getUpgrades().getEntry(modifier);
        ModifierEntry resultEntry = result.getTool().getUpgrades().getEntry(modifier);

        int inputNeed = inputEntry.getNeeded();

        if (inputNeed == 0 || inputNeed == neededPerLevel) {
            IncrementalModifierRecipe.updateInputs(
                inv,
                input,
                resultEntry.getAmount(neededPerLevel) - inputEntry.getAmount(0),
                amountPerInput,
                leftover.get()
            );
        } else {
            int gcd = IntMath.gcd(inputNeed, neededPerLevel);
            int recipeScale = inputNeed / gcd;
            int used =
                (resultEntry.getAmount(neededPerLevel) * recipeScale) -
                ((inputEntry.getAmount(0) * neededPerLevel) / gcd);

            IncrementalModifierRecipe.updateInputs(
                inv,
                input,
                (used + recipeScale - 1) / recipeScale,
                amountPerInput,
                leftover.get()
            );
        }
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TicEXRegistry.VALIDATABLE_INCREMENTAL_RECIPE_SERIALIZER.get();
    }

    @Override
    public boolean isIncremental() {
        return true;
    }

    private List<List<ItemStack>> slotCache;

    private List<List<ItemStack>> getInputs() {
        if (slotCache == null) {
            ImmutableList.Builder<List<ItemStack>> builder = ImmutableList.builder();

            List<ItemStack> items = Arrays.asList(input.getItems());
            int maxStackSize = items.stream().mapToInt(ItemStack::getMaxStackSize).min().orElse(64);

            int needed = neededPerLevel / amountPerInput;
            if (neededPerLevel % amountPerInput > 0) {
                needed++;
            }
            Lazy<List<ItemStack>> fullSize = Lazy.of(() ->
                items
                    .stream()
                    .map(stack -> ItemHandlerHelper.copyStackWithSize(stack, maxStackSize))
                    .collect(Collectors.toList())
            );
            while (needed > maxStackSize) {
                builder.add(fullSize.get());
                needed -= maxStackSize;
            }

            if (needed > 0) {
                int remaining = needed;
                builder.add(
                    items
                        .stream()
                        .map(stack -> ItemHandlerHelper.copyStackWithSize(stack, remaining))
                        .collect(Collectors.toList())
                );
            }
            slotCache = builder.build();
        }
        return slotCache;
    }

    @Override
    public int getInputCount() {
        return getInputs().size();
    }

    @Override
    public List<ItemStack> getDisplayItems(int slot) {
        List<List<ItemStack>> inputs = getInputs();
        if (slot >= 0 && slot < inputs.size()) {
            return inputs.get(slot);
        }
        return Collections.emptyList();
    }
}
