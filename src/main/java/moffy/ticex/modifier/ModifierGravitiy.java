package moffy.ticex.modifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ModifierGravitiy extends NoLevelsModifier implements InventoryTickModifierHook, TooltipModifierHook{

    private final Map<Integer, Long> lastJumpTracker = new HashMap<>();

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK, ModifierHooks.TOOLTIP);
    }

    @Override
    public void addTooltip(IToolStackView tool, ModifierEntry entry, Player player, List<Component> tooltips, TooltipKey tooltipKey,
            TooltipFlag tooltipFlag) {
        tooltips.add(PELang.GEM_LORE_LEGS.translate());
    }

    private boolean jumpedRecently(Player player) {
		return lastJumpTracker.containsKey(player.getId()) && player.level().getGameTime() - lastJumpTracker.get(player.getId()) < 5;
	}

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry entry, Level level, LivingEntity entity, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        Item item = tool.getItem();

        if(item instanceof ArmorItem armorItem && armorItem.getType() == ArmorItem.Type.LEGGINGS && entity instanceof Player player && isCorrectSlot){
            if (level.isClientSide) {
                if (player.isSecondaryUseActive() && !player.onGround() && player.getDeltaMovement().y() > -8 && !jumpedRecently(player)) {
                    player.setDeltaMovement(player.getDeltaMovement().add(0, -0.32F, 0));
                }
            }
            if (player.isSecondaryUseActive()) {
                AABB box = new AABB(player.getX() - 3.5, player.getY() - 3.5, player.getZ() - 3.5,
                        player.getX() + 3.5, player.getY() + 3.5, player.getZ() + 3.5);
                WorldHelper.repelEntitiesSWRG(level, box, player);
                if (!level.isClientSide && player.getDeltaMovement().y() < -0.08) {
                    List<Entity> entities = player.level().getEntities(player, player.getBoundingBox().move(player.getDeltaMovement()).inflate(2.0D),
                            cadidateEntity -> cadidateEntity instanceof LivingEntity);
                    for (Entity e : entities) {
                        if (e.isPickable()) {
                            e.hurt(level.damageSources().playerAttack(player), (float) -player.getDeltaMovement().y() * 6F);
                        }
                    }
                }
            }
        }
    }

    public Map<Integer, Long> getLastJumpTracker() {
        return lastJumpTracker;
    }
}
