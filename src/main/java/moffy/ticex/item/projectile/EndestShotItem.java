package moffy.ticex.item.projectile;

import moffy.ticex.entity.avaritia.EndestShotProjectile;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.tools.item.CrystalshotItem;

public class EndestShotItem extends CrystalshotItem {
    public EndestShotItem(Item.Properties props){
        super(props);
    }

    @Override
    public AbstractArrow createArrow(Level pLevel, ItemStack pStack, LivingEntity pShooter){
        EndestShotProjectile arrow = new EndestShotProjectile(pShooter, pLevel);
        return arrow;
    }
}
