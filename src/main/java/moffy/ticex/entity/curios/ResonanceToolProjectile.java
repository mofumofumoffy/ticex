package moffy.ticex.entity.curios;

import moffy.ticex.entity.ItemArrow;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;

public class ResonanceToolProjectile extends ItemArrow {

    public ResonanceToolProjectile(EntityType<? extends AbstractArrow> type, Level level){
        super(type, level);
    }

    @SuppressWarnings("unchecked")
    public ResonanceToolProjectile(@Nullable LivingEntity shooter, Level level) {
        super((EntityType<? extends AbstractArrow>) TicEXRegistry.RESONANCE_TOOL_PROJECTILE.get(), shooter, level);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        super.onHitBlock(pResult);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Entity target = pResult.getEntity();
        ItemStack toolStack = this.getItem();
        if(this.shooter instanceof Player player && target instanceof LivingEntity livingTarget && toolStack.getItem() instanceof IModifiable){
            livingTarget.invulnerableTime = 1;
            ToolAttackUtil.attackEntity(toolStack, player, livingTarget);
        }
        super.onHitEntity(pResult);
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

}
