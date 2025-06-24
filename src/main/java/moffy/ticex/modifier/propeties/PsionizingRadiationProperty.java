package moffy.ticex.modifier.propeties;

import dan200.computercraft.api.lua.ILuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import moffy.ticex.lib.utils.TicEXPsiUtils;
import moffy.ticex.modifier.ModifierPsionizingRadiation;
import moffy.ticex.modules.general.TicEXRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class PsionizingRadiationProperty {

    public static BiFunction<Player, ItemStack, Map<String, Object>> getProperties() {
        return (user, stack) -> {
            Map<String, Object> result = new HashMap<>();

            result.put("getAutoSpelling", getAutoSpelling(user, stack));
            result.put("setAutoSpelling", setAutoSpelling(user, stack));
            result.put("castPsiSpell", castPsiSpell(user, stack));

            return result;
        };
    }

    public static ILuaFunction getAutoSpelling(Player user, ItemStack stack) {
        return args -> {
            ToolStack tool = ToolStack.from(stack);
            return MethodResult.of(tool.getPersistentData().getBoolean(ModifierPsionizingRadiation.AUTO_CASTING_LOC));
        };
    }

    public static ILuaFunction setAutoSpelling(Player user, ItemStack stack) {
        return args -> {
            ToolStack tool = ToolStack.from(stack);
            tool.getPersistentData().putBoolean(ModifierPsionizingRadiation.AUTO_CASTING_LOC, args.getBoolean(0));
            return MethodResult.of();
        };
    }

    public static ILuaFunction castPsiSpell(Player user, ItemStack stack) {
        return args -> {
            if (
                stack.getItem() instanceof IModifiable &&
                ToolStack.from(stack).getModifierLevel(TicEXRegistry.PSIONIZING_RADIATION_MODIFIER.get()) > 0
            ) {
                try {
                    TicEXPsiUtils.CastSpell(user, stack, spellContext -> {});
                    return MethodResult.of(true);
                } catch (Exception e) {
                    return MethodResult.of(false, e.getMessage());
                }
            }
            return MethodResult.of(false);
        };
    }
}
