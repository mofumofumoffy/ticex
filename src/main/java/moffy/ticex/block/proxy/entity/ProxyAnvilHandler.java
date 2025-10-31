package moffy.ticex.block.proxy.entity;

import moffy.ticex.TicEX;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.recipe.container.ISingleStackContainer;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;

import java.util.Objects;
import java.util.Optional;

public class ProxyAnvilHandler extends ItemStackHandler implements IMutableTinkerStationContainer, IProxyTicker {

    protected final ProxyBlockEntity proxyBlockEntity;
    private ITinkerStationRecipe lastRecipe;
    private MaterialRecipe lastMaterialRecipe;

    public ProxyAnvilHandler(ProxyBlockEntity proxyBlockEntity){
        super(7);
        this.proxyBlockEntity = proxyBlockEntity;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if(slot == 6) {
            return stack;
        } else {
            ItemStack result;
            if(slot == 0){
                result = stack.is(TinkerTags.Items.MULTIPART_TOOL) ? super.insertItem(slot, stack, simulate) : stack;
            } else {
                result = super.insertItem(slot, stack, simulate);
            }
            return result;
        }
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if(slot < 6){
            return ItemStack.EMPTY;
        }
        return super.extractItem(slot, amount, simulate);
    }

    @Override
    public @Nullable MaterialRecipe getInputMaterial(int i) {
        return findMaterialRecipe(getStackInSlot(i + 1), proxyBlockEntity.getLevel());
    }

    @Override
    public @NotNull ItemStack getTinkerableStack() {
        return this.getStackInSlot(0);
    }

    @Override
    public @NotNull ItemStack getInput(int i) {
        return this.getStackInSlot(i + 1);
    }

    @Override
    public int getInputCount() {
        return 6;
    }

    @Nullable
    private MaterialRecipe findMaterialRecipe(ItemStack stack, Level world) {
        if (world == null) {
            return null;
        }
        ISingleStackContainer inv = () -> stack;
        if (lastMaterialRecipe != null && lastMaterialRecipe.matches(inv, world)) {
            return lastMaterialRecipe;
        }

        Optional<MaterialRecipe> newRecipe = world.getRecipeManager().getRecipeFor(TinkerRecipeTypes.MATERIAL.get(), inv, world);
        if (newRecipe.isPresent()) {
            lastMaterialRecipe = newRecipe.get();
            return lastMaterialRecipe;
        }

        return null;
    }

    @Override
    public void setInput(int i, @NotNull ItemStack itemStack) {
        this.setStackInSlot(i, itemStack);
    }

    @Override
    public void giveItem(@NotNull ItemStack itemStack) {

    }

    @Override
    public void tick() {
        Level level = proxyBlockEntity.getLevel();
        if(level != null && !level.isClientSide()){
            RecipeManager manager = Objects.requireNonNull(level.getServer()).getRecipeManager();

            ITinkerStationRecipe recipe = lastRecipe;
            if (recipe == null || !recipe.matches(this, level)) {
                recipe = manager.getRecipeFor(TinkerRecipeTypes.TINKER_STATION.get(), this, level).orElse(null);
            }

            if (recipe != null) {
                if (lastRecipe != recipe) {
                    this.lastRecipe = recipe;
                }

                RecipeResult<LazyToolStack> validatedResult = recipe.getValidatedResult(this, level.registryAccess());
                if (validatedResult.isSuccess() && this.getStackInSlot(6).isEmpty()) {
                    this.setStackInSlot(6, validatedResult.getResult().getStack());
                    this.lastRecipe.updateInputs(validatedResult.getResult(), this, !level.isClientSide);
                    this.onContentsChanged(6);
                }
            }
        }
    }
}
