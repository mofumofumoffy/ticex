package moffy.ticex.modifier.propeties;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import dan200.computercraft.api.lua.ILuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import moffy.ticex.lib.utils.TicEXUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class OvercastingProperty {
    public static BiFunction<Player, ItemStack, Map<String, Object>> getProperties(){
        return (user, stack)->{
            Map<String, Object> result = new HashMap<>();

            result.put("castIronsSpell", castIronsSpell(user, stack));

            return result;
        };
    }

    public static ILuaFunction castIronsSpell(Player user, ItemStack stack){
        return (args)->{
            ISpellContainer container = ISpellContainer.get(stack);
            int page = args.getInt(0);
            if(page >= 0 && page < container.getMaxSpellCount() && user instanceof ServerPlayer){
                ISpellContainer spellContainer = ISpellContainer.get(stack);
                SpellData spellData = spellContainer.getSpellAtIndex(page);
                AbstractSpell spell = spellData.getSpell();
                spell.attemptInitiateCast(stack, page, user.level(), user, CastSource.SPELLBOOK, true, TicEXUtils.getEquipmentSlotName(user, stack));
                return MethodResult.of(true);
            }
            return MethodResult.of(false);
        };
    }
}
