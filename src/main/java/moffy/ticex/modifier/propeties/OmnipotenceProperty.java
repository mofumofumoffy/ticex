package moffy.ticex.modifier.propeties;

import committee.nova.mods.avaritia.init.config.ModConfig;
import committee.nova.mods.avaritia.util.ToolUtils;
import dan200.computercraft.api.lua.ILuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class OmnipotenceProperty {

    public static BiFunction<Player, ItemStack, Map<String, Object>> getProperties() {
        return (user, stack) -> {
            Map<String, Object> result = new HashMap<>();

            result.put("annihilate", annihilate(user, stack));

            return result;
        };
    }

    public static ILuaFunction annihilate(Player user, ItemStack stack) {
        return args -> {
            Level level = user.level();
            if (!level.isClientSide && !user.getCooldowns().isOnCooldown(stack.getItem())) {
                ToolUtils.aoeAttack(
                    user,
                    ModConfig.swordAttackRange.get(),
                    ModConfig.swordRangeDamage.get(),
                    ModConfig.isSwordAttackAnimal.get(),
                    ModConfig.isSwordAttackLightning.get()
                );
                user.getCooldowns().addCooldown(stack.getItem(), 20);
                level.playSound(user, user.getOnPos(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 5.0f);
            }
            return MethodResult.of();
        };
    }
}
