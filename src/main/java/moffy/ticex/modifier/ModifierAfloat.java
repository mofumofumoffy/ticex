package moffy.ticex.modifier;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileShootModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

public class ModifierAfloat extends NoLevelsModifier implements MeleeDamageModifierHook, ProjectileShootModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE, ModifierHooks.PROJECTILE_THROWN);
    }


    @Override
    public float getMeleeDamage(IToolStackView iToolStackView, ModifierEntry modifierEntry, ToolAttackContext toolAttackContext, float baseDamage, float damage) {
        Player player = toolAttackContext.getPlayerAttacker();
        if(player != null && !toolAttackContext.getLevel().isClientSide()){
            if(player.isPassenger() || player.getVehicle() != null){
                return damage + 5;
            }
        }
        return damage;
    }

    @Override
    public void onProjectileShoot(IToolStackView iToolStackView, ModifierEntry modifierEntry, @Nullable LivingEntity livingEntity, ItemStack itemStack, Projectile projectile, @Nullable AbstractArrow abstractArrow, ModDataNBT modDataNBT, boolean b) {
        if(livingEntity != null && !livingEntity.level().isClientSide()){
            if(livingEntity.isPassenger() || livingEntity.getVehicle() != null){
                if (abstractArrow != null) {
                    abstractArrow.setBaseDamage(abstractArrow.getBaseDamage() + 5);
                }
            }
        }
    }
}
