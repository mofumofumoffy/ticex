package moffy.ticex.item.projectile;

import moffy.ticex.entity.mekanism.MekanicProjectile;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.tools.item.CrystalshotItem;

public class MekanicShotItem extends CrystalshotItem {
    public MekanicShotItem(Properties props) {
        super(props);
    }

    @Override
    public @NotNull AbstractArrow createArrow(@NotNull Level pLevel, @NotNull ItemStack pStack, @NotNull LivingEntity pShooter) {
        return new MekanicProjectile(super.createArrow(pLevel, pStack, pShooter), new ItemStack(this), pStack);
    }
}
