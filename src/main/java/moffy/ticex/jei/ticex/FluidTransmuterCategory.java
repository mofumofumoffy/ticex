package moffy.ticex.jei.ticex;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import moffy.ticex.TicEX;
import moffy.ticex.block.transmuter.pattern.FluidTransmutationPair;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.plugin.jei.melting.MeltingFuelHandler;
import slimeknights.tconstruct.plugin.jei.util.FluidTooltipCallback;

import java.util.ArrayList;
import java.util.List;

public class FluidTransmuterCategory extends AbstractRecipeCategory<FluidTransmutationPair> {
    private static final IRecipeSlotRichTooltipCallback RICH_UNITS = (view, tooltipBuilder) -> {
        ArrayList<Component> tooltip = new ArrayList<>();
        FluidTooltipCallback.UNITS.onTooltip(view, tooltip);
        tooltipBuilder.addAll(tooltip);
    };

    private static final ResourceLocation BACKGROUND_LOC = ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "textures/gui/jei/fluid_transmutation.png");
    private static final Component TITLE = Component.translatable(
            Util.makeTranslationKey("jei", ResourceLocation.fromNamespaceAndPath(TicEX.MODID, "fluid_transmuter.title"))
    );

    private final IDrawable background;
    private final IDrawable arrow;
    private final IDrawable tank;

    public FluidTransmuterCategory(IGuiHelper helper) {
        super(
                TicEXJEIIntegration.JeiConstants.FLUID_TRANSMUTATION_RECIPE,
                TITLE,
                helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(TicEXRegistry.FLUID_TRANSMUTER.get())),
                172,
                62);

        this.background = helper.createDrawable(BACKGROUND_LOC, 0, 0, 172, 62);
        this.arrow = helper.drawableBuilder(BACKGROUND_LOC, 172, 0, 24, 17)
                .buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
        this.tank = helper.createDrawable(BACKGROUND_LOC, 172, 17, 16, 16);
    }

    public void draw(@NotNull FluidTransmutationPair pair, @NotNull IRecipeSlotsView slots, @NotNull GuiGraphics graphics, double mouseX, double mouseY) {
        this.arrow.draw(graphics, 73, 21);
    }

    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, FluidTransmutationPair pair, @NotNull IFocusGroup focuses) {
        int amount = 1000;

        builder.addSlot(RecipeIngredientRole.INPUT, 26, 11)
                .addRichTooltipCallback(RICH_UNITS)
                .setFluidRenderer(amount, false, 16, 32)
                .addIngredients(ForgeTypes.FLUID_STACK, List.of(new FluidStack(pair.inputFluid(), amount)));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 128, 11)
                .addRichTooltipCallback(RICH_UNITS)
                .setFluidRenderer(amount, false, 16, 32)
                .addIngredient(ForgeTypes.FLUID_STACK, new FluidStack(pair.outputFluid(), amount));
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 77, 43)
                .setFluidRenderer(1L, false, 16, 16)
                .setOverlay(this.tank, 0, 0)
                .addIngredients(ForgeTypes.FLUID_STACK, MeltingFuelHandler.getUsableFuels(0));
    }

    @SuppressWarnings("removal")
    public IDrawable getBackground() {
        return this.background;
    }
}
