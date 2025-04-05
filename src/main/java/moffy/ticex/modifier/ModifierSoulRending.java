package moffy.ticex.modifier;

import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.init.DEContent;

import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.LootingModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.LootingContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ModifierSoulRending extends Modifier implements LootingModifierHook{

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.WEAPON_LOOTING);
    }

    @Override
    public int updateLooting(IToolStackView tool, ModifierEntry modifierEntry, LootingContext context, int looting) {
        if (!canEntityDropSoul(context.getLivingTarget())) {
            return looting;
        }

        if (context.getLivingTarget() instanceof Player) {
            return looting;
        }

        int dropChanceModifier = tool.getModifierLevel(TicEXRegistry.SOUL_RENDING_MODIFIER.get());
        if (dropChanceModifier == 0) {
            return looting;
        }

        int rand = RANDOM.nextInt(Math.max(DEConfig.soulDropChance / dropChanceModifier, 1));
        int rand2 = RANDOM.nextInt(Math.max(DEConfig.passiveSoulDropChance / dropChanceModifier, 1));
        Entity entity = context.getTarget();
        boolean isAnimal = entity instanceof Animal;

        if ((rand == 0 && !isAnimal) || (rand2 == 0 && isAnimal)) {
            ItemStack soul = DEContent.MOB_SOUL.get().getSoulFromEntity(entity, false);
            context.getHolder().level().addFreshEntity(new ItemEntity(context.getHolder().level(), entity.getX(), entity.getY(), entity.getZ(), soul));
        }
        return looting;
    }
    
    private static boolean canEntityDropSoul(LivingEntity entity) {
        if (!entity.canChangeDimensions() && !DEConfig.allowBossSouls) {
            return false;
        }
        //noinspection DataFlowIssue
        String regName = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString();
        if (DEConfig.spawnerList.contains(regName) && DEConfig.spawnerListWhiteList) {
            return true;
        } else if (DEConfig.spawnerList.contains(regName) && !DEConfig.spawnerListWhiteList) {
            return false;
        }
        return !DEConfig.spawnerListWhiteList;
    }
}
