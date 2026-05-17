package moffy.ticex.modifier.propeties;

import dan200.computercraft.api.lua.ILuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SpellData;

import java.util.*;
import java.util.function.BiFunction;
import moffy.ticex.lib.utils.TicEXUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;

public class OvercastingProperty {

    public static BiFunction<Player, ItemStack, Map<String, Object>> getProperties() {
        return (user, stack) -> {
            Map<String, Object> result = new HashMap<>();

            result.put("getAllSpells", getAllSpells(user, stack));
            result.put("getIronsMana", getIronsMana(user, stack));
            result.put("castIronsSpell", castIronsSpell(user, stack));

            return result;
        };
    }

    public static ILuaFunction getIronsMana(Player user, ItemStack stack){
        return args -> {
            MagicData playerData = MagicData.getPlayerMagicData(user);
            return MethodResult.of(playerData.getMana());
        };
    }

    public static ILuaFunction getAllSpells(Player user, ItemStack stack){
        return args -> {
            if(user instanceof ServerPlayer){
                ISpellContainer container = ISpellContainer.get(stack);
                return MethodResult.of(Arrays.stream(container.getAllSpells()).map(spellSlot -> {
                    if(spellSlot != null){
                        return spellSlot.getSpell().getSpellName();
                    }
                    return null;
                }).filter(Objects::nonNull).toList());
            }
            return MethodResult.of(List.of());
        };
    }

    public static ILuaFunction castIronsSpell(Player user, ItemStack stack) {
        return args -> {

            ISpellContainer container = ISpellContainer.get(stack);
            int page = args.getInt(0) - 1;
            if (page >= 0 && page < container.getMaxSpellCount() && user instanceof ServerPlayer) {
                ISpellContainer spellContainer = ISpellContainer.get(stack);
                SpellData spellData = spellContainer.getSpellAtIndex(page);
                AbstractSpell spell = spellData.getSpell();
                LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER).execute(() -> {
                    spell.attemptInitiateCast(
                            stack,
                            page,
                            user.level(),
                            user,
                            CastSource.SPELLBOOK,
                            true,
                            TicEXUtils.getEquipmentSlotName(user, stack)
                    );
                });
                return MethodResult.of(true);
            }
            return MethodResult.of(false);
        };
    }
}
