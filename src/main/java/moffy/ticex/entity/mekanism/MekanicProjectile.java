package moffy.ticex.entity.mekanism;

import meranha.mekaweapons.items.MekaArrowEntity;
import moffy.ticex.caps.mekanism.MekanicArrowCapability;
import moffy.ticex.lib.modules.mekanism.interfaces.IMekanicArrow;
import moffy.ticex.lib.utils.TicEXMekanismWeaponsUtils;
import moffy.ticex.lib.utils.TicEXUtils;
import moffy.ticex.mixin.ProjectileAccessor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;

public class MekanicProjectile extends MekaArrowEntity {

    private final ItemStack ammo;
    private final LivingEntity shooter;
    private final AbstractArrow originalArrow;

    public MekanicProjectile(EntityType<MekanicProjectile> entityType, Level level) {
        this(entityType, level, new ItemStack(Items.ARROW));
    }

    public MekanicProjectile(EntityType<MekanicProjectile> entityType, Level level, ItemStack ammo){
        super(entityType, level, ammo);
        this.ammo = ammo;
        this.shooter = null;
        this.originalArrow = null;
    }

    public MekanicProjectile(AbstractArrow arrow, LivingEntity shooter, ItemStack ammo) {
        this(arrow, ammo, shooter, getWeaponStack(ammo));
    }

    public MekanicProjectile(AbstractArrow arrow, ItemStack ammo, LivingEntity shooter, ItemStack weaponStack) {
        super(arrow, ammo, weaponStack);
        this.ammo = ammo;
        this.shooter = shooter;
        this.originalArrow = createOriginalArrow(ammo, arrow, shooter);
        this.originalArrow.setDeltaMovement(new Vec3(1,0,0));
        this.originalArrow.setBaseDamage(arrow.getBaseDamage() * shooter.getAttributeValue(Attributes.ATTACK_DAMAGE));
    }

    private static ItemStack getWeaponStack(ItemStack ammo){
        return TicEXUtils.capabilityIfPresent(ammo, MekanicArrowCapability.MEKANIC_ARROW_CAPABILITY, IMekanicArrow::getBowItem, new ItemStack(Items.ARROW));
    }

    private static AbstractArrow createOriginalArrow(ItemStack ammo, AbstractArrow arrow, LivingEntity shooter){
        return TicEXUtils.capabilityIfPresent(ammo, MekanicArrowCapability.MEKANIC_ARROW_CAPABILITY, iMekanicArrow -> {
            ItemStack originalAmmo = iMekanicArrow.isEnergizedArrow() ?  new ItemStack(Items.ARROW) : iMekanicArrow.getCopiedOriginalAmmo();
            if(originalAmmo.getItem() instanceof ArrowItem originalArrowItem){
                return originalArrowItem.createArrow(arrow.level(), originalAmmo, shooter);
            }
            return null;
        }, null);
    }

    @Override
    public void onHitEntity(@NotNull EntityHitResult pResult) {
        if(originalArrow != null){
            ((ProjectileAccessor)originalArrow).invokeHitEntity(pResult);
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        if(originalArrow != null){
            ((ProjectileAccessor)originalArrow).invokeHitBlock(pResult);
        }
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return TicEXUtils.capabilityIfPresent(ammo, MekanicArrowCapability.MEKANIC_ARROW_CAPABILITY, iMekanicArrow -> {
            if(!iMekanicArrow.isEnergizedArrow()){
                ItemStack originalArrow = iMekanicArrow.getCopiedOriginalAmmo();
                return new ItemStack(originalArrow.getItem(), 1);
            }
            return ItemStack.EMPTY;
        }, ItemStack.EMPTY);
    }
}
