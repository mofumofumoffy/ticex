package moffy.ticex.modifier;

import cpw.mods.modlauncher.api.INameMappingService;
import moffy.ticex.TicEX;
import moffy.ticex.lib.IEntityDataAccessor;
import moffy.ticex.lib.hook.ProvidePropertyModifierHook;
import moffy.ticex.modifier.propeties.DeflectionProperty;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.BiFunction;

public class ModifierDeflection extends Modifier implements MeleeDamageModifierHook, ProjectileHitModifierHook, ProvidePropertyModifierHook {

    public static final ResourceLocation DEFLECTION_DISABLED = new ResourceLocation(TicEX.MODID, "deflection_disabled");

    @Override
    public int getPriority() {
        return 1000;
    }

    @Override
    protected void registerHooks(Builder hookBuilder) {
        super.registerHooks(hookBuilder);
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE, ModifierHooks.PROJECTILE_HIT, TicEXRegistry.PROPERTY_PROVIDER_HOOK);
    }

    @Override
    public float getMeleeDamage(
        IToolStackView tool,
        ModifierEntry modifierEntry,
        ToolAttackContext context,
        float baseDamage,
        float damage
    ) {
        if (!context.isExtraAttack() && !tool.getPersistentData().getBoolean(DEFLECTION_DISABLED)) {
            LivingEntity target = context.getLivingTarget();
            Player attacker = context.getPlayerAttacker();

            if (target != null && attacker != null) {
                for (ModifierEntry toolEntry : tool.getModifierList()) {
                    var hook = toolEntry.getHook(ModifierHooks.MELEE_HIT);
                    hook.beforeMeleeHit(tool, modifierEntry, context, damage, 0, 0);
                }

                for (ModifierEntry toolEntry : tool.getModifierList()) {
                    var hook = toolEntry.getHook(ModifierHooks.MELEE_HIT);
                    hook.afterMeleeHit(tool, modifierEntry, context, damage);
                }

                if(target.getHealth() <= 0f){
                    return 0;
                }

                float absoluteHealth = target.getHealth() - damage;
                IEntityDataAccessor accessor = (IEntityDataAccessor) target;

                String fieldName = ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_20961_");
                //fieldName = "DATA_HEALTH_ID";

                Field key = accessor.getField(fieldName);
                if (key != null) {
                    accessor.setValue(key, absoluteHealth);

                    if(absoluteHealth <= 0f){
                        target.die(attacker.damageSources().genericKill());
                        if(target.level() instanceof ServerLevel serverLevel){
                            serverLevel.broadcastEntityEvent(target, (byte) 3);
                        }
                    }
                }
            }

            return 0;
        }
        return damage;
    }

    @Override
    public boolean onProjectileHitEntity(
        ModifierNBT modifiers,
        ModDataNBT persistentData,
        ModifierEntry modifier,
        Projectile projectile,
        EntityHitResult hit,
        LivingEntity attacker,
        LivingEntity target
    ) {
        for (ModifierEntry toolEntry : modifiers.getModifiers()) {
            if (!toolEntry.matches(this) && !persistentData.getBoolean(DEFLECTION_DISABLED)) {
                toolEntry
                    .getHook(ModifierHooks.PROJECTILE_HIT)
                    .onProjectileHitEntity(modifiers, persistentData, modifier, projectile, hit, attacker, target);
            }
        }
        return false;
    }

    @Override
    public BiFunction<Player, ItemStack, Map<String, Object>> getPropertyProvider() {
        return DeflectionProperty.getProperties();
    }
}
