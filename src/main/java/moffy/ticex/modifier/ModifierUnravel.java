package moffy.ticex.modifier;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

public class ModifierUnravel extends NoLevelsModifier implements MeleeHitModifierHook, ProjectileHitModifierHook {

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT);
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        Player player = context.getPlayerAttacker();
        LivingEntity target = context.getLivingTarget();
        Level level = context.getLevel();
        if(player != null && target != null && !level.isClientSide()){
            if(context.isCritical()){
                for(EquipmentSlot slot : EquipmentSlot.values()){
                    if(slot.getType() == EquipmentSlot.Type.ARMOR){
                        ItemStack armor = target.getItemBySlot(slot);
                        if(player.getRandom().nextFloat() < 0.3f && !armor.isEmpty()){
                            ItemStack armorCopy = armor.copy();
                            ItemEntity itemEntity = new ItemEntity(
                                    level,
                                    target.getX(),
                                    target.getY(),
                                    target.getZ(),
                                    armorCopy
                            );
                            if(level.addFreshEntity(itemEntity)){
                                target.setItemSlot(slot, ItemStack.EMPTY);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
        if(attacker != null && target != null){
            Level level = target.level();
            if(!level.isClientSide() && projectile instanceof AbstractArrow arrow && arrow.isCritArrow()){
                for(EquipmentSlot slot : EquipmentSlot.values()){
                    if(slot.getType() == EquipmentSlot.Type.ARMOR){
                        ItemStack armor = target.getItemBySlot(slot);
                        if(attacker.getRandom().nextFloat() < 0.3f && !armor.isEmpty()){
                            ItemStack armorCopy = armor.copy();
                            ItemEntity itemEntity = new ItemEntity(
                                    level,
                                    target.getX(),
                                    target.getY(),
                                    target.getZ(),
                                    armorCopy
                            );
                            if(level.addFreshEntity(itemEntity)){
                                target.setItemSlot(slot, ItemStack.EMPTY);
                            }
                        }
                    }
                }
            }
        }
        return notBlocked;
    }
}
