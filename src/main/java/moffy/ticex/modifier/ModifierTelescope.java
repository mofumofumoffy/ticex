package moffy.ticex.modifier;

import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ProtectionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class ModifierTelescope extends Modifier implements MeleeDamageModifierHook, BreakSpeedModifierHook, ProtectionModifierHook, ConditionalStatModifierHook {
    protected static boolean canSeeStar(LivingEntity livingEntity){
        Level level = livingEntity.level();
        return !level.isRaining() && level.isNight() && level.canSeeSky(livingEntity.getOnPos());
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE, ModifierHooks.BREAK_SPEED, ModifierHooks.PROTECTION, ModifierHooks.CONDITIONAL_STAT);
    }

    @Override
    public float getProtectionModifier(IToolStackView iToolStackView, ModifierEntry modifierEntry, EquipmentContext equipmentContext, EquipmentSlot equipmentSlot, DamageSource damageSource, float v) {
        if(canSeeStar(equipmentContext.getEntity())){
            return v * (1f + modifierEntry.getLevel() / 10f);
        }
        return v;
    }

    @Override
    public float getMeleeDamage(IToolStackView iToolStackView, ModifierEntry modifierEntry, ToolAttackContext toolAttackContext, float v, float v1) {
        if(canSeeStar(toolAttackContext.getAttacker())){
            return v * (1f + modifierEntry.getLevel() / 10f);
        }
        return v;
    }

    @Override
    public void onBreakSpeed(IToolStackView iToolStackView, ModifierEntry modifierEntry, PlayerEvent.BreakSpeed breakSpeed, Direction direction, boolean b, float v) {
        if(canSeeStar(breakSpeed.getEntity())){
            breakSpeed.setNewSpeed(breakSpeed.getNewSpeed() * (1f + modifierEntry.getLevel() / 10f));
        }
    }

    @Override
    public float modifyStat(IToolStackView iToolStackView, ModifierEntry modifierEntry, LivingEntity livingEntity, FloatToolStat floatToolStat, float v, float v1) {
        if(floatToolStat.getName().equals(ToolStats.PROJECTILE_DAMAGE.getName())){
            if(canSeeStar(livingEntity)){
                return v * (1f + modifierEntry.getLevel() / 10f);
            }
        }
        return v;
    }
}
