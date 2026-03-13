package moffy.ticex.entity.mekanism;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import meranha.mekaweapons.MekaWeapons;
import meranha.mekaweapons.MekaWeaponsUtils;
import meranha.mekaweapons.items.MekaArrowEntity;
import moffy.ticex.TicEX;
import moffy.ticex.entity.avaritia.EndestShotProjectile;
import moffy.ticex.lib.utils.TicEXMekanismWeaponsUtils;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.tools.item.CrystalshotItem;

public class MekanicProjectile extends MekaArrowEntity {

    private final ItemStack weaponStack;

    public MekanicProjectile(EntityType<MekanicProjectile> entityType, Level level) {
        super(entityType, level, new ItemStack(Items.ARROW));
        this.weaponStack = new ItemStack(Items.ARROW);
    }

    public MekanicProjectile(AbstractArrow arrow, ItemStack projectileStack, ItemStack weaponStack) {
        super(arrow, projectileStack, weaponStack);
        this.weaponStack = weaponStack;
        this.setBaseDamage(arrow.getBaseDamage() * TicEXMekanismWeaponsUtils.getAmplifier(weaponStack) * 5);
    }

    @Override
    public void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);
        TicEX.LOGGER.info("{}", weaponStack.getDisplayName().getString());
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }
}
