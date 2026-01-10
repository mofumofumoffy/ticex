package moffy.ticex.modifier;

import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.socket.SocketHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ProtectionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BlockBreakModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.function.BiConsumer;
import java.util.stream.DoubleStream;

public class ModifierApothSupplier extends NoLevelsModifier implements MeleeHitModifierHook, MeleeDamageModifierHook, ProtectionModifierHook, BlockBreakModifierHook, AttributesModifierHook, ProjectileLaunchModifierHook, ToolDamageModifierHook {
    private boolean preventStackOverflowFlg = false;

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE, ModifierHooks.MELEE_HIT, ModifierHooks.PROTECTION, ModifierHooks.BLOCK_BREAK, ModifierHooks.ATTRIBUTES, ModifierHooks.PROJECTILE_LAUNCH, ModifierHooks.TOOL_DAMAGE);
    }

    @Override
    public boolean shouldDisplay(boolean advanced) {
        return false;
    }

    @Override
    public float getMeleeDamage(IToolStackView iToolStackView, ModifierEntry modifierEntry, ToolAttackContext toolAttackContext, float baseDamage, float damage) {
        float newDamage = damage;
        if(iToolStackView instanceof ToolStack tool && toolAttackContext.getTarget() instanceof Mob mob){
            ItemStack stack = tool.createStack();
            newDamage += SocketHelper.getGems(stack).getDamageBonus(mob.getMobType());
        }
        return newDamage;
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity user = context.getAttacker();
        Entity target = context.getTarget();

        for (ItemStack s : user.getAllSlots()) {
            SocketHelper.getGems(s).doPostAttack(user, target);

            var affixes = AffixHelper.getAffixes(s);
            for (AffixInstance inst : affixes.values()) {
                int old = target.invulnerableTime;
                target.invulnerableTime = 0;
                inst.doPostAttack(user, target);
                target.invulnerableTime = old;
            }
        }
    }

    @Override
    public float getProtectionModifier(IToolStackView iToolStackView, ModifierEntry modifierEntry, EquipmentContext equipmentContext, EquipmentSlot equipmentSlot, DamageSource damageSource, float protection) {
        float newProtection = protection;
        if(iToolStackView instanceof ToolStack tool){
            ItemStack toolStack = tool.createStack();
            newProtection += SocketHelper.getGems(toolStack).getDamageProtection(damageSource);

            var affixes = AffixHelper.getAffixes(toolStack);
            for (AffixInstance inst : affixes.values()) {
                newProtection += inst.getDamageProtection(damageSource);
            }
        }
        return newProtection;
    }

    @Override
    public void addAttributes(IToolStackView iToolStackView, ModifierEntry modifierEntry, EquipmentSlot equipmentSlot, BiConsumer<Attribute, AttributeModifier> biConsumer) {
        if(iToolStackView instanceof ToolStack tool && !preventStackOverflowFlg){
            preventStackOverflowFlg = true;
            ItemStack toolStack = tool.createStack();
            SocketHelper.getGems(toolStack).addModifiers(LootCategory.forItem(toolStack), equipmentSlot, biConsumer);

            var affixes = AffixHelper.getAffixes(toolStack);
            for (AffixInstance inst : affixes.values()) {
                inst.addModifiers(equipmentSlot, biConsumer);
            }
            preventStackOverflowFlg = false;
        }

    }

    @Override
    public void afterBlockBreak(IToolStackView iToolStackView, ModifierEntry modifierEntry, ToolHarvestContext toolHarvestContext) {
        if(iToolStackView instanceof ToolStack tool){
            ItemStack toolStack = tool.createStack();
            SocketHelper.getGems(toolStack).onBlockBreak(toolHarvestContext.getPlayer(), toolHarvestContext.getWorld(), toolHarvestContext.getPos(), toolHarvestContext.getState());
            var affixes = AffixHelper.getAffixes(toolStack);
            for (AffixInstance inst : affixes.values()) {
                inst.onBlockBreak(toolHarvestContext.getPlayer(), toolHarvestContext.getWorld(), toolHarvestContext.getPos(), toolHarvestContext.getState());
            }
        }
    }

    @Override
    public void onProjectileLaunch(IToolStackView iToolStackView, ModifierEntry modifierEntry, LivingEntity livingEntity, Projectile projectile, @Nullable AbstractArrow abstractArrow, ModDataNBT modDataNBT, boolean b) {
        if(iToolStackView instanceof ToolStack tool && projectile instanceof AbstractArrow arrow){
            ItemStack toolStack = tool.createStack();
            SocketHelper.getGems(toolStack).onArrowFired(livingEntity, arrow);
            var affixes = AffixHelper.getAffixes(toolStack);
            for (AffixInstance inst : affixes.values()) {
                inst.onArrowFired(livingEntity, arrow);
            }
        }
    }

    @Override
    public int onDamageTool(IToolStackView iToolStackView, ModifierEntry modifierEntry, int amount, @Nullable LivingEntity livingEntity) {
        if(iToolStackView instanceof ToolStack tool && livingEntity instanceof ServerPlayer pUser){
            int blocked = 0;
            ItemStack toolStack = tool.createStack();
            DoubleStream socketBonuses = SocketHelper.getGems(toolStack).getDurabilityBonusPercentage(pUser);
            DoubleStream afxBonuses = AffixHelper.streamAffixes(toolStack).mapToDouble(inst -> inst.getDurabilityBonusPercentage(pUser));
            DoubleStream bonuses = DoubleStream.concat(socketBonuses, afxBonuses);
            double chance = bonuses.reduce(0, (res, ele) -> res + (1 - res) * ele);

            int delta = 1;
            if (chance < 0) {
                delta = -1;
                chance = -chance;
            }

            if (chance > 0) {
                for (int i = 0; i < amount; i++) {
                    if (pUser.getRandom().nextFloat() <= chance) blocked += delta;
                }
            }
            return amount - blocked;
        }
        return amount;
    }
}
