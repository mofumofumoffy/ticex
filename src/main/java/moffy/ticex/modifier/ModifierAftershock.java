package moffy.ticex.modifier;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class ModifierAftershock extends Modifier implements MeleeHitModifierHook {

    @Override
    public void afterMeleeHit(
        IToolStackView tool,
        ModifierEntry modifier,
        ToolAttackContext context,
        float damageDealt
    ) {
        if (context.isFullyCharged()) {
            Entity entity = context.getTarget();
            ToolAttackUtil.attackEntitySecondary(
                new DamageSource(
                    context
                        .getLevel()
                        .registryAccess()
                        .registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(DamageTypes.MAGIC)
                ),
                tool.getStats().get(ToolStats.ATTACK_DAMAGE) * modifier.getLevel() * 0.1f,
                entity,
                (entity instanceof LivingEntity ? (LivingEntity) entity : null),
                true
            );
            context
                .getLevel()
                .addParticle(ParticleTypes.ENCHANTED_HIT, entity.getX(), entity.getY(), entity.getZ(), 0, 5, 0);
        }
    }

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
    }
}
