package moffy.ticex.network.projecte;

import java.util.Optional;
import java.util.function.Supplier;

import moffy.ticex.modifier.ModifierAbyssal;
import moffy.ticex.modifier.ModifierHurricane;
import moffy.ticex.modifier.ModifierInfernal;
import moffy.ticex.modules.general.TicEXRegistry;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.handlers.InternalAbilities;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.text.ILangEntry;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.NonNullPredicate;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@Deprecated
public record TicEXPEKeyHandler(PEKeybind key){
    public static void encode(TicEXPEKeyHandler packet, FriendlyByteBuf buf) {
        buf.writeEnum(packet.key());
    }

	public static TicEXPEKeyHandler decode(FriendlyByteBuf buf) {
		return new TicEXPEKeyHandler(buf.readEnum(PEKeybind.class));
	}

	public static void handle(TicEXPEKeyHandler packet, Supplier<NetworkEvent.Context> contextSupplier){
		packet.handle(contextSupplier.get());
	}

    public void handle(Context context) {
        ServerPlayer player = context.getSender();
		if (player == null || player.isSpectator()) {
			return;
		}
		if (key == PEKeybind.HELMET_TOGGLE) {
			ItemStack helm = player.getItemBySlot(EquipmentSlot.HEAD);
			if (!helm.isEmpty() && helm.getItem() instanceof IModifiable) {
				ToolStack tool = ToolStack.from(helm);
				if(tool.getModifierLevel(TicEXRegistry.ABYSSAL_MODIFIER.get()) > 0){
					ModifierAbyssal.toggleNightVision(tool, player);
				}
			}
			return;
		} else if (key == PEKeybind.BOOTS_TOGGLE) {
			ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
			if (!boots.isEmpty() && boots.getItem() instanceof IModifiable) {
				ToolStack tool = ToolStack.from(boots);
				if(tool.getModifierLevel(TicEXRegistry.HURRICANE_MODIFIER.get()) > 0){
					ModifierHurricane.toggleStepAssist(tool, player);
				}
			}
			return;
		}
		Optional<InternalAbilities> cap = player.getCapability(InternalAbilities.CAPABILITY).resolve();
		if (cap.isEmpty()) {
			return;
		}
		InternalAbilities internalAbilities = cap.get();
		/* for (InteractionHand hand : InteractionHand.values()) {
			ItemStack stack = player.getItemInHand(hand);
			switch (key) {
				case CHARGE -> {
					if (tryPerformCapability(stack, PECapabilities.CHARGE_ITEM_CAPABILITY, capability -> capability.changeCharge(player, stack, hand))) {
						return;
					} else if (hand == InteractionHand.MAIN_HAND && isSafe(stack) && hasAnyPiece(player)) {
						internalAbilities.setGemState(!internalAbilities.getGemState());
						ILangEntry langEntry = internalAbilities.getGemState() ? PELang.GEM_ACTIVATE : PELang.GEM_DEACTIVATE;
						player.sendSystemMessage(langEntry.translate());
						return;
					}
				}
				case EXTRA_FUNCTION -> {
					if (tryPerformCapability(stack, PECapabilities.EXTRA_FUNCTION_ITEM_CAPABILITY, capability -> capability.doExtraFunction(stack, player, hand))) {
						return;
					} else if (hand == InteractionHand.MAIN_HAND && isSafe(stack) && internalAbilities.getGemState()) {
						ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
						if (!chestplate.isEmpty() && chestplate.getItem() instanceof IModifiable && internalAbilities.getGemCooldown() == 0) {
							ToolStack tool = ToolStack.from(stack);
							if(tool.getModifierLevel(TicEXRegistry.INFERNAL_MODIFIER.get()) > 0){
								ModifierInfernal.doExplode(player);
								internalAbilities.resetGemCooldown();
								return;
							}
						}
					}
				}
				case FIRE_PROJECTILE -> {
					if (!stack.isEmpty() && internalAbilities.getProjectileCooldown() == 0 &&
						tryPerformCapability(stack, PECapabilities.PROJECTILE_SHOOTER_ITEM_CAPABILITY, capability -> capability.shootProjectile(player, stack, hand))) {
						PlayerHelper.swingItem(player, hand);
						internalAbilities.resetProjectileCooldown();
					}
					if (hand == InteractionHand.MAIN_HAND && isSafe(stack) && internalAbilities.getGemState()) {
						ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
						if (!helmet.isEmpty() && helmet.getItem() instanceof IModifiable) {
							ToolStack tool = ToolStack.from(stack);
							if(tool.getModifierLevel(TicEXRegistry.ABYSSAL_MODIFIER.get()) > 0){
								ModifierAbyssal.doZap(player);
								internalAbilities.resetGemCooldown();
								return;
							}
						}
					}
				}
				case MODE -> {
					if (tryPerformCapability(stack, PECapabilities.MODE_CHANGER_ITEM_CAPABILITY, capability -> capability.changeMode(player, stack, hand))) {
						return;
					}
				}
                default -> {
                    return;
                }
			}
		} */
		context.setPacketHandled(true);
    }
    
    private static <CAPABILITY> boolean tryPerformCapability(ItemStack stack, Capability<CAPABILITY> capability, NonNullPredicate<CAPABILITY> perform) {
		return !stack.isEmpty() && stack.getCapability(capability).filter(perform).isPresent();
	}

	private static boolean isSafe(ItemStack stack) {
		return ProjectEConfig.server.misc.unsafeKeyBinds.get() || stack.isEmpty();
	}

	public static boolean hasAnyPiece(Player player) {
		return player.getInventory().armor.stream().anyMatch((i) -> {
			if(i.getItem() instanceof IModifiable){
				ToolStack tool = ToolStack.from(i);
				return hasGemModifier(tool);
			}
			return false;
		});
	}

	public static boolean hasFullSet(Player player) {
		return player.getInventory().armor.stream().noneMatch((i) -> {
			if(i.getItem() instanceof IModifiable){
				ToolStack tool = ToolStack.from(i);
				return !hasGemModifier(tool);
			}
			return false;
		});
	}

	public static boolean hasGemModifier(IToolStackView tool){
		return tool.getModifierLevel(TicEXRegistry.ABYSSAL_MODIFIER.get()) > 0 
				|| tool.getModifierLevel(TicEXRegistry.INFERNAL_MODIFIER.get()) > 0 
				|| tool.getModifierLevel(TicEXRegistry.GRAVITY_MODIFIER.get()) > 0
				|| tool.getModifierLevel(TicEXRegistry.HURRICANE_MODIFIER.get()) > 0 ; 
	}	
}
