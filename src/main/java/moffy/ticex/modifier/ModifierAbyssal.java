package moffy.ticex.modifier;

import java.util.ArrayList;
import java.util.List;

import moffy.ticex.TicEX;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.handlers.InternalTimers;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

public class ModifierAbyssal extends NoLevelsModifier implements InventoryTickModifierHook{

    public static final ResourceLocation ABYSSAL_DATA = new ResourceLocation(TicEX.MODID, "abyssal");

    public static void toggleNightVision(IToolStackView tool, Player player) {
		boolean value;
		
        ModDataNBT helmetTag = tool.getPersistentData();
		if (helmetTag.contains(ABYSSAL_DATA, Tag.TAG_BYTE)) {
			value = !helmetTag.getBoolean(ABYSSAL_DATA);
			helmetTag.putBoolean(ABYSSAL_DATA, value);
		} else {
			helmetTag.putBoolean(ABYSSAL_DATA, true);
			value = true;
		}
		if (value) {
			player.sendSystemMessage(PELang.NIGHT_VISION.translate(ChatFormatting.GREEN, PELang.GEM_ENABLED));
		} else {
			player.sendSystemMessage(PELang.NIGHT_VISION.translate(ChatFormatting.RED, PELang.GEM_DISABLED));
		}
	}


    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
    }

    @Override
    public List<Component> getDescriptionList(IToolStackView tool, ModifierEntry entry) {
        List<Component> tooltips = new ArrayList<>();
        tooltips.add(PELang.GEM_LORE_HELM.translate());
		tooltips.add(PELang.NIGHT_VISION_PROMPT.translate(ClientKeyHelper.getKeyName(PEKeybind.HELMET_TOGGLE)));
		if (tool.getPersistentData().getBoolean(ABYSSAL_DATA)) {
			tooltips.add(PELang.NIGHT_VISION.translate(ChatFormatting.GREEN, PELang.GEM_ENABLED));
		} else {
			tooltips.add(PELang.NIGHT_VISION.translate(ChatFormatting.RED, PELang.GEM_DISABLED));
		}
        return tooltips;
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry entry, Level level, LivingEntity entity, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        Item item = tool.getItem();

        if(item instanceof ArmorItem armorItem && armorItem.getType() == ArmorItem.Type.HELMET && entity instanceof Player){
            if (!level.isClientSide) {
                entity.getCapability(InternalTimers.CAPABILITY).ifPresent(handler -> {
                    handler.activateHeal();
                    if (entity.getHealth() < entity.getMaxHealth() && handler.canHeal()) {
                        entity.heal(2.0F);
                    }
                });

                if (tool.getPersistentData().getBoolean(ABYSSAL_DATA)) {
                    entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, true, false));
                } else {
                    entity.removeEffect(MobEffects.NIGHT_VISION);
                }
            }
        }
    }
    
    public static void doZap(Player player) {
		if (ProjectEConfig.server.difficulty.offensiveAbilities.get()) {
			BlockHitResult strikeResult = PlayerHelper.getBlockLookingAt(player, 120.0F);
			if (strikeResult.getType() != HitResult.Type.MISS) {
				BlockPos strikePos = strikeResult.getBlockPos();
				Level level = player.level();
				LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
				if (lightning != null) {
					lightning.moveTo(Vec3.atCenterOf(strikePos));
					lightning.setCause((ServerPlayer) player);
					level.addFreshEntity(lightning);
				}
			}
		}
	}
}
