package moffy.ticex.lib.recipe;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;

import moffy.ticex.lib.hook.EmbossmentModifierHook.EmbossmentContext;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.modifiers.adding.AbstractModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IncrementalModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class SingleEmbossmentModifierRecipe extends AbstractModifierRecipe{

    public static final RecordLoadable<SingleEmbossmentModifierRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    IngredientLoadable.DISALLOW_EMPTY.requiredField("emboss_input", r -> r.input),
    TOOLS_FIELD, MAX_TOOL_SIZE_FIELD, RESULT_FIELD, LEVEL_FIELD, SLOTS_FIELD,
    SingleEmbossmentModifierRecipe::new);

    private final Ingredient input;

    private List<List<ItemStack>> slotCache;

    public SingleEmbossmentModifierRecipe(ResourceLocation id, Ingredient input, 
            Ingredient toolRequirement, int maxToolSize, ModifierId result, IntRange level, SlotCount slots) {
        super(id, toolRequirement, maxToolSize, result, level, slots, false, false);
        this.input = input;
    }
    
    @Override
    public boolean matches(ITinkerStationContainer inv, Level level) {
        if (!result.isBound() || !this.toolRequirement.test(inv.getTinkerableStack())) {
        return false;
        }
        return IncrementalModifierRecipe.containsOnlyIngredient(inv, input);
    }

    @Override
  public RecipeResult<LazyToolStack> getValidatedResult(ITinkerStationContainer inv, RegistryAccess access) {
        ToolStack tool = inv.getTinkerable().copy();

        ModifierId modifier = result.getId();

        if(tool.getModifierLevel(modifier) == 0){
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
        for(int i = 0; i < inv.getInputCount(); i++){
            ItemStack inputStack = inv.getInput(i);
            if(input.test(inputStack)){
                result = tool.getModifier(modifier).getHook(TicEXRegistry.EMBOSSMENT_HOOK).applyItem(context, i, secondary);
            }
            secondary = true;
        }
        
        if(result){
            return LazyToolStack.success(context.getToolStack());
        }
        return RecipeResult.failure(context.getErrorMsg());
    }

    @Override
    public void updateInputs(LazyToolStack result, IMutableTinkerStationContainer inv, boolean isServer) {
        for(int index = 0; index < inv.getInputCount(); ++index) {
            inv.shrinkInput(index, inv.getInput(index).getCount());
        }
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TicEXRegistry.SINGLE_MODIFIER_EMBOSSMENT_RECIPE_SERIALIZER.get();
    }

    @Override
    public List<ItemStack> getDisplayItems(int slot) {
        List<List<ItemStack>> inputs = getInputs();
        if (slot >= 0 && slot < inputs.size()) {
        return inputs.get(slot);
        }
        return Collections.emptyList();
    }

    @Override
    public int getInputCount() {
        return getInputs().size();
    }

    private List<List<ItemStack>> getInputs() {
    if (slotCache == null) {
        ImmutableList.Builder<List<ItemStack>> builder = ImmutableList.builder();

        // fill extra item slots
        List<ItemStack> items = Arrays.asList(input.getItems());

        builder.add(items);
        slotCache = builder.build();
        }
        return slotCache;
    }
}
