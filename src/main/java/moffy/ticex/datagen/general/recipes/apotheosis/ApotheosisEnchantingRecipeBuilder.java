package moffy.ticex.datagen.general.recipes.apotheosis;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.shadowsoffire.apotheosis.ench.table.EnchantingRecipe;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;

import java.util.Objects;
import java.util.function.Consumer;

public class ApotheosisEnchantingRecipeBuilder extends AbstractRecipeBuilder<ApotheosisEnchantingRecipeBuilder> {
    private static final class EnchantStats {
        Float maxEterna;
        Float eterna;
        Float quanta;
        Float arcana;
        Float rectification;
        Integer clues;

        public EnchantStats(Float maxEterna, Float eterna, Float quanta, Float arcana, Float rectification, Integer clues) {
            this.maxEterna = maxEterna;
            this.eterna = eterna;
            this.quanta = quanta;
            this.arcana = arcana;
            this.rectification = rectification;
            this.clues = clues;
        }

        public EnchantStats() {
            this.maxEterna = null;
            this.eterna = null;
            this.quanta = null;
            this.arcana = null;
            this.rectification = null;
            this.clues = null;
        }

        public JsonElement toJson() {
            JsonObject json = new JsonObject();

            if(maxEterna!=null) json.addProperty("maxEterna", maxEterna);
            if(eterna!=null) json.addProperty("eterna", eterna);
            if(quanta!=null) json.addProperty("quanta", quanta);
            if(arcana!=null) json.addProperty("arcana", arcana);
            if(rectification!=null) json.addProperty("rectification", rectification);
            if(clues!=null) json.addProperty("clues", clues);

            return json;
        }
    }

    private Ingredient input;
    private EnchantStats requirements;
    private EnchantStats maxRequirements;
    private final ItemStack itemOutput;

    /**
     * set input item
     * @param item input
     * @return this
     */
    public ApotheosisEnchantingRecipeBuilder setInput(Item item) {
        return setInput(Ingredient.of(item));
    }

    /**
     * set input item
     * @param itemStack input
     * @return this
     */
    public ApotheosisEnchantingRecipeBuilder setInput(ItemStack itemStack) {
        return setInput(Ingredient.of(itemStack));
    }

    /**
     * set input item
     * @param itemLike input
     * @return this
     */
    public ApotheosisEnchantingRecipeBuilder setInput(ItemLike itemLike) {
        return setInput(Ingredient.of(itemLike));
    }

    /**
     * set input item
     * @param ingredient input
     * @return this
     */
    public ApotheosisEnchantingRecipeBuilder setInput(Ingredient ingredient) {
        this.input = ingredient;
        return this;
    }

    /**
     * set requirements.maxEterna
     * @param maxEterna requirement maxEterna
     * @return this
     */
    public ApotheosisEnchantingRecipeBuilder setMaxEterna(float maxEterna) {
        requirements.maxEterna = maxEterna;
        return this;
    }

    /**
     * set requirements.eterna
     * @param eterna requirement eterna
     * @return this
     */
    public ApotheosisEnchantingRecipeBuilder setEterna(float eterna) {
        requirements.eterna = eterna;
        return this;
    }

    /**
     * set requirements.quanta
     * @param quanta requirement quanta
     * @return this
     */
    public ApotheosisEnchantingRecipeBuilder setQuanta(float quanta) {
        requirements.quanta = quanta;
        return this;
    }

    /**
     * set requirements.arcana
     * @param arcana requirement arcana
     * @return this
     */
    public ApotheosisEnchantingRecipeBuilder setArcana(float arcana) {
        requirements.arcana = arcana;
        return this;
    }

    /**
     * set requirements.rectification
     * @param rectification requirement rectification
     * @return this
     */
    public ApotheosisEnchantingRecipeBuilder setRectification(float rectification) {
        requirements.rectification = rectification;
        return this;
    }

    /**
     * set requirements.clues
     * @param clues requirement clues
     * @return this
     */
    public ApotheosisEnchantingRecipeBuilder setClues(int clues) {
        requirements.clues = clues;
        return this;
    }

    /**
     * set maxRequirements.maxEterna
     * @param maxEterna maxRequirement maxEterna
     * @return this
     */
    public ApotheosisEnchantingRecipeBuilder setMaxEternaMax(float maxEterna) {
        maxRequirements.maxEterna = maxEterna;
        return this;
    }

    /**
     * set maxRequirements.eterna
     * @param eterna maxRequirement eterna
     * @return this
     */
    public ApotheosisEnchantingRecipeBuilder setEternaMax(float eterna) {
        maxRequirements.eterna = eterna;
        return this;
    }

    /**
     * set maxRequirements.quanta
     * @param quanta maxRequirement quanta
     * @return this
     */
    public ApotheosisEnchantingRecipeBuilder setQuantaMax(float quanta) {
        maxRequirements.quanta = quanta;
        return this;
    }

    /**
     * set maxRequirements.arcana
     * @param arcana maxRequirement arcana
     * @return this
     */
    public ApotheosisEnchantingRecipeBuilder setArcanaMax(float arcana) {
        maxRequirements.arcana = arcana;
        return this;
    }

    /**
     * set maxRequirements.rectification
     * @param rectification maxRequirement rectification
     * @return this
     */
    public ApotheosisEnchantingRecipeBuilder setRectificationMax(float rectification) {
        maxRequirements.rectification = rectification;
        return this;
    }

    /**
     * set maxRequirements.clues
     * @param clues maxRequirement clues
     * @return this
     */
    public ApotheosisEnchantingRecipeBuilder setCluesMax(int clues) {
        maxRequirements.clues = clues;
        return this;
    }

    public ApotheosisEnchantingRecipeBuilder setRequirement(float maxEterna, float eterna, float quanta, float arcana, float rectification, int clues) {
        requirements = new EnchantStats(maxEterna, eterna, quanta, arcana, rectification, clues);
        return this;
    }

    public ApotheosisEnchantingRecipeBuilder setMaxRequirement(float maxEterna, float eterna, float quanta, float arcana, float rectification, int clues) {
        maxRequirements = new EnchantStats(maxEterna, eterna, quanta, arcana, rectification, clues);
        return this;
    }

    @Override
    public void save(@NotNull Consumer<FinishedRecipe> consumer) {
        ResourceLocation resourceLocation = ForgeRegistries.ITEMS.getKey(itemOutput.getItem());
        if(resourceLocation==null) {
            throw new RuntimeException("Could not find item " + itemOutput.getItem() + " in item registry");
        }
        this.save(consumer, resourceLocation);
    }

    @Override
    public void save(@NotNull Consumer<FinishedRecipe> consumer, @NotNull ResourceLocation id) {
        consumer.accept(new ApotheosisEnchantingRecipeBuilder.EnchantingResult<>(id, this.buildOptionalAdvancement(id, "enchanting")));
    }

    private ApotheosisEnchantingRecipeBuilder(ItemStack itemOutput) {
        this.input = Ingredient.EMPTY;
        this.requirements = new EnchantStats();
        this.maxRequirements = new EnchantStats();
        this.itemOutput = itemOutput;
    }

    public static ApotheosisEnchantingRecipeBuilder builder(ItemLike item) {
        return builder(item, 1);
    }

    public static ApotheosisEnchantingRecipeBuilder builder(ItemLike item, int count) {
        return builder(new ItemStack(item, count));
    }

    public static ApotheosisEnchantingRecipeBuilder builder(ItemStack item) {
        return new ApotheosisEnchantingRecipeBuilder(item);
    }

    private class EnchantingResult<R extends Recipe<?>> extends AbstractRecipeBuilder<?>.AbstractFinishedRecipe {
        public EnchantingResult(ResourceLocation resourceLocation, ResourceLocation advancement) {
            super(resourceLocation, advancement);
        }

        public static JsonObject writeItemStack(ItemStack stack) {
            JsonObject json = new JsonObject();
            ResourceLocation resource = ForgeRegistries.ITEMS.getKey(stack.getItem());
            if(resource == null) {
                throw new RuntimeException(String.format("Item %s not found", stack.getItem().getDescriptionId()));
            }

            json.addProperty("item", resource.toString());
            if (stack.getCount() != 1) {
                json.addProperty("count", stack.getCount());
            }

            if (stack.hasTag()) {
                Objects.requireNonNull(stack.getTag());
                json.addProperty("nbt", stack.getTag().toString());
            }

            return json;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("input", ApotheosisEnchantingRecipeBuilder.this.input.toJson());
            json.add("requirements", ApotheosisEnchantingRecipeBuilder.this.requirements.toJson());
            json.add("max_requirements", ApotheosisEnchantingRecipeBuilder.this.maxRequirements.toJson());
            json.add("result", writeItemStack(ApotheosisEnchantingRecipeBuilder.this.itemOutput));
        }

        @Override
        public @NotNull RecipeSerializer<?> getType() {
            return EnchantingRecipe.SERIALIZER;
        }
    }
}
