package moffy.ticex.event;

import moffy.ticex.modifier.ModifierGravitiy;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class TicEXPEEvent {

    public static void onJump(LivingEvent.LivingJumpEvent evt) {
        if (evt.getEntity() instanceof Player player && player.level().isClientSide) {
            ItemStack leggingsStack = player.getItemBySlot(EquipmentSlot.LEGS);
            if (leggingsStack.getItem() instanceof IModifiable) {
                ToolStack leggings = ToolStack.from(leggingsStack);
                for (ModifierEntry entry : leggings.getModifierList()) {
                    if (entry.getLazyModifier().get() instanceof ModifierGravitiy gravitiyModifier) {
                        gravitiyModifier.getLastJumpTracker().put(player.getId(), player.level().getGameTime());
                        break;
                    }
                }
            }
        }
    }

    public static void onPlayerTick(PlayerTickEvent event){
        Player player = event.player;
        if (player.getAbilities().flying) return;

        //water&lava walking
		boolean waterWalkOnType = canWalkOnWater(player);
		boolean lavaWalkOnType = canWalkOnLava(player);
		if (waterWalkOnType|| lavaWalkOnType) {
			int x = (int) Math.floor(player.getX());
			int y = (int) (player.getY() - player.getMyRidingOffset());
			int z = (int) Math.floor(player.getZ());
			BlockPos pos = new BlockPos(x, y, z);
			FluidState below = player.level().getFluidState(pos.below());
			boolean water = waterWalkOnType && below.is(FluidTags.WATER);
			boolean lava = lavaWalkOnType && below.is(FluidTags.LAVA);
			if ((water || lava) && player.level().isEmptyBlock(pos)) {
				if (!player.isShiftKeyDown()) {
					player.setDeltaMovement(player.getDeltaMovement().multiply(1, 0, 1));
					player.fallDistance = 0.0F;
					player.setOnGround(true);
				}
			} else if (!player.level().isClientSide) {
				if (waterWalkOnType && player.isInWater()) {
					player.setAirSupply(player.getMaxAirSupply());
				}
			}
		}
    }

    private static boolean canWalkOnWater(Player player) {
		ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        if(!helmet.isEmpty() && helmet.getItem() instanceof IModifiable){
            ToolStack tool = ToolStack.from(helmet);
            return tool.getModifierLevel(TicEXRegistry.ABYSSAL_MODIFIER.get()) > 0;
        }
		return false;
	}

	private static boolean canWalkOnLava(Player player) {
		ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        if(!chestplate.isEmpty() && chestplate.getItem() instanceof IModifiable){
            ToolStack tool = ToolStack.from(chestplate);
            return tool.getModifierLevel(TicEXRegistry.INFERNAL_MODIFIER.get()) > 0;
        }
		return false;
	}
}
