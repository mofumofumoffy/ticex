package moffy.ticex.modifier;

import java.util.function.Predicate;

import committee.nova.mods.avaritia.common.entity.GapingVoidEntity;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.ranged.BowAmmoModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

public class ModifierEndestShot extends NoLevelsModifier implements ProjectileHitModifierHook {

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT);
    }

    @Override
    public int getPriority() {
        return 65;
    }

    @Override
    public boolean onProjectileHitEntity(
            ModifierNBT modifiers,
            ModDataNBT persistentData,
            ModifierEntry modifier,
            Projectile projectile,
            EntityHitResult hit,
            @Nullable LivingEntity attacker,
            @Nullable LivingEntity target
    ) {
        if (!projectile.level().isClientSide) {
            GapingVoidEntity ent;
            if (attacker != null) {
                ent = new GapingVoidEntity(attacker.level(), attacker);
                Direction dir = target.getDirection();
                Vec3 offset = Vec3.ZERO;
                if (dir != null) {
                    offset = new Vec3((double) dir.getStepX(), (double) dir.getStepY(), (double) dir.getStepZ());
                }

                ent.moveTo(
                        target.getX() + offset.x * 0.25,
                        target.getY() + offset.y * 0.25,
                        target.getZ() + offset.z * 0.25,
                        target.getYRot(),
                        0.0F
                );
                projectile.level().addFreshEntity(ent);
            }

            projectile.remove(Entity.RemovalReason.KILLED);
        }
        return ProjectileHitModifierHook.super.onProjectileHitEntity(modifiers, persistentData, modifier, projectile, hit, attacker, target);
    }

    @Override
    public void onProjectileHitBlock(
            ModifierNBT modifiers,
            ModDataNBT persistentData,
            ModifierEntry modifier,
            Projectile projectile,
            BlockHitResult hit,
            @Nullable LivingEntity attacker
    ) {
        ProjectileHitModifierHook.super.onProjectileHitBlock(modifiers, persistentData, modifier, projectile, hit, attacker);
        BlockPos pos = hit.getBlockPos();
        if (!projectile.level().isClientSide) {
            GapingVoidEntity ent;
            if (attacker != null) {
                ent = new GapingVoidEntity(projectile.level(), attacker);
                Direction dir = hit.getDirection();
                Vec3 offset = Vec3.ZERO;
                if (dir != null) {
                    offset = new Vec3((double) dir.getStepX(), (double) dir.getStepY(), (double) dir.getStepZ());
                }

                ent.moveTo(
                        (double) pos.getX() + offset.x * 0.25,
                        (double) pos.getY() + offset.y * 0.25,
                        (double) pos.getZ() + offset.z * 0.25,
                        projectile.getYRot(),
                        0.0F
                );
                projectile.level().addFreshEntity(ent);
            }

            projectile.remove(Entity.RemovalReason.KILLED);
        }
    }
}
