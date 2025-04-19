package moffy.ticex.lib.recipe;

import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.tools.definition.module.material.MaterialRepairToolHook;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tables.recipe.TinkerStationRepairRecipe;

public class ModifierRepairRecipe extends TinkerStationRepairRecipe{

    public ModifierRepairRecipe(ResourceLocation id) {
        super(id);
    }
    
    @Override
    public boolean matches(ITinkerStationContainer inv, Level world) {
        ItemStack tinkerable = inv.getTinkerableStack();
        if (tinkerable.isEmpty() || !tinkerable.is(TicEXRegistry.KEY_MODIFIER_UNSTABLE)) {
            return false;
        }

        MaterialId material = null;
        ToolStack tool = inv.getTinkerable();
        for (int i = 0; i < inv.getInputCount(); i++) {
            ItemStack stack = inv.getInput(i);
            if (stack.isEmpty()) {
                continue;
            }

            MaterialId inputMaterial = getMaterialFrom(inv, i);
            if (inputMaterial.equals(IMaterial.UNKNOWN_ID)) {
                return false;
            }

            if (material == null) {
                material = inputMaterial;
                if (!MaterialRepairToolHook.canRepairWith(tool, material)) {
                    return false;
                }
            } else if (!material.equals(inputMaterial)) {
                return false;
            }
        }

        return material != null;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TicEXRegistry.MODIFIER_REPAIR_RECIPE_SERIALIZER.get();
    }
}
