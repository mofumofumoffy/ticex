package moffy.ticex.modifier.propeties;

import committee.nova.mods.avaritia.init.config.ModConfig;
import committee.nova.mods.avaritia.util.ToolUtils;
import dan200.computercraft.api.lua.ILuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import moffy.ticex.TicEX;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;
import slimeknights.tconstruct.library.tools.nbt.ToolDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class OmnipotenceProperty {

    public static final ResourceLocation ANNIHILATE_ALL_LOC = TicEX.getResource("annihilate_all");

    public static BiFunction<Player, ItemStack, Map<String, Object>> getProperties() {
        return (user, stack) -> {
            Map<String, Object> result = new HashMap<>();

            result.put("setAnnihilateAll", setAnnihilateAll(user, stack));
            result.put("annihilate", annihilate(user, stack));

            return result;
        };
    }

    public static ILuaFunction setAnnihilateAll(Player user, ItemStack stack){
        return args -> {
            Level level = user.level();
            if (!level.isClientSide){
                ToolDataNBT persistentData = ToolStack.from(stack).getPersistentData();
                boolean newValue = args.getBoolean(0);
                persistentData.putBoolean(ANNIHILATE_ALL_LOC, newValue);
                return MethodResult.of(true);
            }
            return MethodResult.of(false);
        };
    }

    public static ILuaFunction annihilate(Player user, ItemStack stack) {
        return args -> {
            Level level = user.level();
            if (!level.isClientSide && !user.getCooldowns().isOnCooldown(stack.getItem())) {
                ToolDataNBT persistentData = ToolStack.from(stack).getPersistentData();
                LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER).execute(() -> {
                    ToolUtils.aoeAttack(
                            user,
                            ModConfig.swordAttackRange.get(),
                            ModConfig.swordRangeDamage.get(),
                            persistentData.getBoolean(ANNIHILATE_ALL_LOC),
                            ModConfig.isSwordAttackLightning.get()
                    );
                    user.getCooldowns().addCooldown(stack.getItem(), 20);
                    level.playSound(user, user.getOnPos(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 5.0f);
                });
            }
            return MethodResult.of();
        };
    }
}
