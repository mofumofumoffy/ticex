package moffy.ticex.modifier;

import committee.nova.mods.avaritia.init.registry.ModItems;
import moffy.ticex.TicEXConfig;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ModifierCondensing extends Modifier implements OnAttackedModifierHook {

    @Override
    protected void registerHooks(Builder hookBuilder) {
        super.registerHooks(hookBuilder);
        hookBuilder.addHook(this, ModifierHooks.ON_ATTACKED);
    }

    @Override
    public void onAttacked(
        IToolStackView tool,
        ModifierEntry entry,
        EquipmentContext context,
        EquipmentSlot slot,
        DamageSource source,
        float amount,
        boolean isDirectDamage
    ) {
        RandomSource random = context.getLevel().getRandom();
        if (random.nextFloat() < TicEXConfig.CONDENSING_DROP_PROBABILITY.get()) {
            Level level = context.getLevel();
            LivingEntity livingEntity = context.getEntity();
            ItemStack pileStack = new ItemStack(ModItems.neutron_pile.get());
            ItemEntity entity = new ItemEntity(
                level,
                livingEntity.getX(),
                livingEntity.getY() + 1,
                livingEntity.getZ(),
                pileStack
            );
            level.addFreshEntity(entity);
        }
    }
}
