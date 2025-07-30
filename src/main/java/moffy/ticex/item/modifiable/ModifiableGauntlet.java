package moffy.ticex.item.modifiable;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.utils.Util;

public class ModifiableGauntlet extends ModifiableItem{

    public ModifiableGauntlet(Properties properties, ToolDefinition toolDefinition) {
        super(properties, toolDefinition);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level levelIn, Player playerIn, InteractionHand handIn) {
        if (playerIn.isCrouching()) {
            ItemStack stack = playerIn.getItemInHand(handIn);
            InteractionResult result = ToolInventoryCapability.tryOpenContainer(stack, null, getToolDefinition(), playerIn, Util.getSlotType(handIn));
            if (result.consumesAction()) {
                return new InteractionResultHolder<>(result, stack);
            }
        }
        return super.use(levelIn, playerIn, handIn);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.EPIC;
    }
}
