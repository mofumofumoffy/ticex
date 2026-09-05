package moffy.ticex.item.projectile;

import moffy.ticex.caps.mekanism.MekanicArrowCapability;
import moffy.ticex.entity.mekanism.MekanicProjectile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.tools.item.CrystalshotItem;

public class MekanicShotItem extends CrystalshotItem {
    public MekanicShotItem(Properties props) {
        super(props);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new MekanicArrowCapability.Provider();
    }

    @Override
    public @NotNull AbstractArrow createArrow(@NotNull Level pLevel, @NotNull ItemStack pStack, @NotNull LivingEntity pShooter) {
        return new MekanicProjectile(super.createArrow(pLevel, pStack, pShooter), pShooter, pStack);
    }
}
