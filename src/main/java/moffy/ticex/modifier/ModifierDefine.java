package moffy.ticex.modifier;

import moffy.ticex.entity.FakeLivingEntity;
import moffy.ticex.mixin.HealthAccessor;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ModifierDefine extends NoLevelsModifier implements MeleeDamageModifierHook{

    private static FakeLivingEntity fakeLivingEntity = null;

    @Override
    public int getPriority() {
        return 9999;
    }

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE);
    }

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifierEntry, ToolAttackContext context, float baseDamage, float damage) {
        if(!context.isExtraAttack()){
            if(fakeLivingEntity == null){
                fakeLivingEntity = new FakeLivingEntity((EntityType<? extends LivingEntity>)TicEXRegistry.FAKE_LIVING_ENTITY.get(), context.getLevel());
            }
    
            LivingEntity target = context.getLivingTarget();
            Player attacker = context.getPlayerAttacker();
    
            if(target != null && attacker != null){
                fakeLivingEntity.setHealth(target.getHealth());
    
                ToolAttackContext newContext = new ToolAttackContext(attacker, attacker, context.getHand(), fakeLivingEntity, fakeLivingEntity, true, context.getCooldown(), false);
                
                for(ModifierEntry toolEntry:tool.getModifierList()){
                    toolEntry.getHook(ModifierHooks.MELEE_HIT).beforeMeleeHit(tool, modifierEntry, newContext, damage, baseDamage, damage);
                }

                fakeLivingEntity.hurt(null, damage);
                fakeLivingEntity.invulnerableTime = 0;
                
                for(ModifierEntry toolEntry:tool.getModifierList()){
                    toolEntry.getHook(ModifierHooks.MELEE_HIT).afterMeleeHit(tool, modifierEntry, newContext, damage);
                }

                float absoluteHealth = fakeLivingEntity.getFakeHealth();

                HealthAccessor accessor = (HealthAccessor)target;
                target.getEntityData().set(accessor.getTicEXHealthDataKey(), absoluteHealth);
            }

            return 0;
        }
        return damage;
    }
    
}
