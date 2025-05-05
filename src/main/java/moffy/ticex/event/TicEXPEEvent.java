package moffy.ticex.event;

import org.lwjgl.glfw.GLFW;

import com.google.common.collect.ImmutableBiMap;
import com.mojang.blaze3d.platform.InputConstants;

import moffy.ticex.TicEX;
import moffy.ticex.modifier.ModifierGravitiy;
import moffy.ticex.network.projecte.TicEXPEKeyHandler;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class TicEXPEEvent {
    private static ImmutableBiMap<KeyMapping, PEKeybind> mcToPe = ImmutableBiMap.of();
	//private static ImmutableBiMap<PEKeybind, KeyMapping> peToMc = ImmutableBiMap.of();

    public static void onJump(LivingEvent.LivingJumpEvent evt) {
		if (evt.getEntity() instanceof Player player && player.level().isClientSide) {
			ItemStack leggingsStack = player.getItemBySlot(EquipmentSlot.LEGS);
            if(leggingsStack.getItem() instanceof IModifiable){
                ToolStack leggings = ToolStack.from(leggingsStack);
                for(ModifierEntry entry : leggings.getModifierList()){
                    if(entry.getLazyModifier().get() instanceof ModifierGravitiy gravitiyModifier){
                        gravitiyModifier.getLastJumpTracker().put(player.getId(), player.level().getGameTime());
                        break;
                    }
                }
            }
		}
	}

    @OnlyIn(Dist.CLIENT)
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
		ImmutableBiMap.Builder<KeyMapping, PEKeybind> builder = ImmutableBiMap.builder();
		addKeyBinding(builder, PEKeybind.HELMET_TOGGLE, KeyModifier.SHIFT, GLFW.GLFW_KEY_X);
		addKeyBinding(builder, PEKeybind.BOOTS_TOGGLE, KeyModifier.NONE, GLFW.GLFW_KEY_X);
		addKeyBinding(builder, PEKeybind.CHARGE, KeyModifier.NONE, GLFW.GLFW_KEY_V);
		addKeyBinding(builder, PEKeybind.EXTRA_FUNCTION, KeyModifier.NONE, GLFW.GLFW_KEY_C);
		addKeyBinding(builder, PEKeybind.FIRE_PROJECTILE, KeyModifier.NONE, GLFW.GLFW_KEY_R);
		addKeyBinding(builder, PEKeybind.MODE, KeyModifier.NONE, GLFW.GLFW_KEY_G);
		mcToPe = builder.build();
		//peToMc = mcToPe.inverse();
	}

    @OnlyIn(Dist.CLIENT)
	private static void addKeyBinding(ImmutableBiMap.Builder<KeyMapping, PEKeybind> builder, PEKeybind keyBind, KeyModifier modifier, int keyCode) {
		builder.put(new KeyMapping(keyBind.getTranslationKey(), KeyConflictContext.IN_GAME, modifier, InputConstants.Type.KEYSYM, keyCode,
						PELang.PROJECTE.getTranslationKey()), keyBind);
	}

    @OnlyIn(Dist.CLIENT)
    public static void keyPress(TickEvent.ClientTickEvent event) {
        for (KeyMapping k : mcToPe.keySet()) {
			while (k.consumeClick()) {
				TicEX.CHANNEL.sendToServer(new TicEXPEKeyHandler(mcToPe.get(k)));
			}
		}
	}
}
