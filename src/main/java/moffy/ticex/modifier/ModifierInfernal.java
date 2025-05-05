package moffy.ticex.modifier;

import java.util.List;

import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.handlers.InternalTimers;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ModifierInfernal extends NoLevelsModifier implements InventoryTickModifierHook, TooltipModifierHook{

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK, ModifierHooks.TOOLTIP);
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry entry, Level level, LivingEntity entity, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        Item item = tool.getItem();

        if(item instanceof ArmorItem armorItem && armorItem.getType() == ArmorItem.Type.CHESTPLATE && entity instanceof Player player){
            if (!level.isClientSide) {
                player.getCapability(InternalTimers.CAPABILITY).ifPresent(timers -> {
                    timers.activateFeed();
                    if (player.getFoodData().needsFood() && timers.canFeed()) {
                        player.getFoodData().eat(2, 10);
                        player.gameEvent(GameEvent.EAT);
                    }
                });
            }
        }

    }

    @Override
    public void addTooltip(IToolStackView tool, ModifierEntry entry, Player player, List<Component> tooltips, TooltipKey tooltipKey,
            TooltipFlag tooltipFlag) {
        tooltips.add(PELang.GEM_LORE_CHEST.translate());
    }

    public static void doExplode(Player player) {
		if (ProjectEConfig.server.difficulty.offensiveAbilities.get()) {
			WorldHelper.createNovaExplosion(player.level(), player, player.getX(), player.getY(), player.getZ(), 9.0F);
		}
	}
}
