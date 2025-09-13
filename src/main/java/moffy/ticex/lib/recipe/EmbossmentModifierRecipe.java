package moffy.ticex.lib.recipe;

import java.util.*;
import javax.annotation.Nullable;
import moffy.ticex.lib.hook.EmbossmentModifierHook.EmbossmentContext;
import moffy.ticex.lib.utils.TicEXUtils;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.modifiers.adding.AbstractModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class EmbossmentModifierRecipe extends AbstractModifierRecipe {

    public static final RecordLoadable<EmbossmentModifierRecipe> LOADER = RecordLoadable.create(
        ContextKey.ID.requiredField(),
        SizedIngredient.LOADABLE.list(1).requiredField("inputs", r -> r.inputs),
        SizedIngredient.LOADABLE.list(1).requiredField("emboss_inputs", r -> r.embossItem),
        TOOLS_FIELD,
        MAX_TOOL_SIZE_FIELD,
        RESULT_FIELD,
        LEVEL_FIELD,
        SLOTS_FIELD,
        EmbossmentModifierRecipe::new
    );

    private final List<SizedIngredient> embossItem;
    private final List<SizedIngredient> inputs;

    private List<SizedIngredient> ingredientsCache;

    public EmbossmentModifierRecipe(
        ResourceLocation id,
        List<SizedIngredient> inputs,
        List<SizedIngredient> embossItem,
        Ingredient toolRequirement,
        int maxToolSize,
        ModifierId result,
        IntRange level,
        @Nullable SlotCount slots
    ) {
        super(id, toolRequirement, maxToolSize, result, level, slots, false, false);
        this.inputs = inputs;
        this.embossItem = embossItem;
    }

    @Override
    public boolean matches(ITinkerStationContainer container, Level level) {
        if (!result.isBound() || !this.toolRequirement.test(container.getTinkerableStack())) {
            return false;
        }

        if (ingredientsCache == null) {
            ingredientsCache = new ArrayList<>();
        } else {
            ingredientsCache.clear();
        }

        ingredientsCache.addAll(inputs);
        ingredientsCache.addAll(embossItem);
        return ModifierRecipe.checkMatch(container, ingredientsCache);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TicEXRegistry.MODIFIER_EMBOSSMENT_RECIPE_SERIALIZER.get();
    }

    @Override
    public void updateInputs(LazyToolStack arg0, IMutableTinkerStationContainer arg1, boolean arg2) {
        ModifierRecipe.updateInputs(arg1, inputs);
        ModifierRecipe.updateInputs(arg1, embossItem);
    }

    @Override
    public List<ItemStack> getDisplayItems(int slot) {
        if (slot >= 0 && slot < inputs.size() + embossItem.size()) {
            if (slot < inputs.size()) {
                return inputs.get(slot).getMatchingStacks();
            } else {
                return embossItem.get(slot - inputs.size()).getMatchingStacks();
            }
        }
        return Collections.emptyList();
    }

    @Override
    public int getInputCount() {
        return inputs.size() + embossItem.size();
    }

    @Override
    public RecipeResult<LazyToolStack> getValidatedResult(ITinkerStationContainer inv, RegistryAccess access) {
        ToolStack tool = inv.getTinkerable();
        var commonError = this.validatePrerequisites(tool);
        if (commonError != null) {
            return RecipeResult.failure(commonError);
        } else {
            ModifierId modifier = result.getId();

            tool = tool.copy();

            if (tool.getModifierLevel(modifier) == 0) {
                SlotCount slots = getSlots();
                if (slots != null) {
                    tool.getPersistentData().addSlots(slots.type(), -slots.count());
                }
            } else {
                tool.removeModifier(modifier, 1);
            }

            tool.addModifier(modifier, 1);
            boolean result = false;
            ItemStack resultStack = tool.createStack();

            EmbossmentContext context = new EmbossmentContext(resultStack, inv);

            boolean secondary = false;
            for (int i = 0; i < inv.getInputCount(); i++) {
                ItemStack input = inv.getInput(i);
                if (embossItem.get(0).test(input)) {
                    result = tool
                            .getModifier(modifier)
                            .getHook(TicEXRegistry.EMBOSSMENT_HOOK)
                            .applyItem(context, i, secondary);
                }
                secondary = true;
            }

            if (result) {
                return LazyToolStack.success(TicEXUtils.applyCatalystEmbossment(context.getToolStack(), inv, false));
            }
            return RecipeResult.failure(context.getErrorMsg());
        }
    }
}
